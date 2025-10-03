package com.crashedcarsales.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    @Bean
    public ConcurrentHashMap<String, Bucket> rateLimitCache() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a rate limit bucket for public API endpoints
     * Allows 100 requests per minute per IP
     */
    public Bucket createPublicApiBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Creates a rate limit bucket for search endpoints
     * Allows 50 requests per minute per IP (more restrictive for search)
     */
    public Bucket createSearchApiBucket() {
        Bandwidth limit = Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Creates a rate limit bucket for strict endpoints
     * Allows 20 requests per minute per IP (very restrictive)
     */
    public Bucket createStrictApiBucket() {
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}