package com.crashedcarsales.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, Bucket> rateLimitCache;
    private final ObjectMapper objectMapper;

    @Autowired
    public RateLimitInterceptor(ConcurrentHashMap<String, Bucket> rateLimitCache) {
        this.rateLimitCache = rateLimitCache;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String clientIP = getClientIP(request);

        // Skip rate limiting for non-public API endpoints
        if (!requestURI.startsWith("/api/public/")) {
            return true;
        }

        Bucket bucket = getBucketForRequest(requestURI, clientIP);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // Add rate limit headers
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-Rate-Limit-Retry-After-Seconds",
                String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            return true;
        } else {
            // Rate limit exceeded
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

            // Send JSON error response
            sendRateLimitErrorResponse(response, waitForRefill);
            return false;
        }
    }

    private Bucket getBucketForRequest(String requestURI, String clientIP) {
        String cacheKey = clientIP + ":" + getRateLimitType(requestURI);

        return rateLimitCache.computeIfAbsent(cacheKey, key -> {
            RateLimitConfig rateLimitConfig = new RateLimitConfig();
            return switch (getRateLimitType(requestURI)) {
                case "search" -> rateLimitConfig.createSearchApiBucket();
                case "strict" -> rateLimitConfig.createStrictApiBucket();
                default -> rateLimitConfig.createPublicApiBucket();
            };
        });
    }

    private String getRateLimitType(String requestURI) {
        if (requestURI.contains("/search")) {
            return "search";
        } else if (requestURI.contains("/similar") || requestURI.contains("/stats")) {
            return "strict";
        }
        return "public";
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    private void sendRateLimitErrorResponse(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorResponse = objectMapper.writeValueAsString(
            new RateLimitErrorResponse("Rate limit exceeded. Please try again later.", retryAfterSeconds)
        );

        response.getWriter().write(errorResponse);
    }

    public static class RateLimitErrorResponse {
        private String error;
        private String message;
        private long retryAfterSeconds;

        public RateLimitErrorResponse(String message, long retryAfterSeconds) {
            this.error = "RATE_LIMIT_EXCEEDED";
            this.message = message;
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
        public long getRetryAfterSeconds() { return retryAfterSeconds; }
    }
}