package com.crashedcarsales.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight in-memory cache service that emulates the Redis-backed implementation used in tests. This
 * implementation is intentionally simple and focuses on providing the behaviours required by the existing
 * test-suite so that the application can run with or without Redis.
 */
public class CacheService {

    private static final String CAR_DETAILS_CACHE = "car-details";
    private static final String FEATURED_CARS_CACHE = "featured-cars";
    private static final String CAR_SEARCH_CACHE = "car-search";
    private static final String SESSIONS_CACHE = "sessions";

    private final Map<String, ConcurrentHashMap<String, CacheValue>> caches = new ConcurrentHashMap<>();

    public void cacheCarDetails(String carId, Object carResponse) {
        putInternal(CAR_DETAILS_CACHE, carId, carResponse, null);
    }

    public Object getCachedCarDetails(String carId) {
        return getInternal(CAR_DETAILS_CACHE, carId);
    }

    public void cacheFeaturedCars(List<?> featuredCars) {
        putInternal(FEATURED_CARS_CACHE, "all", featuredCars, null);
    }

    public Object getCachedFeaturedCars() {
        return getInternal(FEATURED_CARS_CACHE, "all");
    }

    public void cacheCarSearch(String key, List<?> results) {
        putInternal(CAR_SEARCH_CACHE, key, results, null);
    }

    public Object getCachedCarSearch(String key) {
        return getInternal(CAR_SEARCH_CACHE, key);
    }

    public void invalidateSearchCaches() {
        evictAll(CAR_SEARCH_CACHE);
    }

    public void evict(String cacheName, String key) {
        caches.computeIfAbsent(cacheName, unused -> new ConcurrentHashMap<>()).remove(key);
    }

    public void invalidateCarCaches(String carId) {
        evict(CAR_DETAILS_CACHE, carId);
        evictAll(FEATURED_CARS_CACHE);
    }

    public void storeSession(String sessionId, Map<String, Object> sessionData) {
        putInternal(SESSIONS_CACHE, sessionId, new ConcurrentHashMap<>(sessionData), null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSession(String sessionId) {
        Object value = getInternal(SESSIONS_CACHE, sessionId);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    public void updateSession(String sessionId, String field, Object value) {
        Map<String, Object> session = getSession(sessionId);
        if (session == null) {
            session = new ConcurrentHashMap<>();
        } else {
            session = new ConcurrentHashMap<>(session);
        }
        session.put(field, value);
        putInternal(SESSIONS_CACHE, sessionId, session, null);
    }

    public void deleteSession(String sessionId) {
        evict(SESSIONS_CACHE, sessionId);
    }

    public Map<String, Object> getCacheStatistics() {
        List<String> cacheNames = new ArrayList<>(caches.keySet());
        Map<String, Object> redisInfo = Map.of(
            "mode", "in-memory",
            "provider", "CacheService",
            "connected", Boolean.FALSE
        );

        return Map.of(
            "cacheNames", cacheNames,
            "totalCaches", cacheNames.size(),
            "redisInfo", redisInfo
        );
    }

    public Set<String> getCacheKeys(String cacheName) {
        return Collections.unmodifiableSet(
            caches.computeIfAbsent(cacheName, unused -> new ConcurrentHashMap<>()).keySet()
        );
    }

    public void evictAll(String cacheName) {
        caches.computeIfAbsent(cacheName, unused -> new ConcurrentHashMap<>()).clear();
    }

    public void put(String cacheName, String key, Object value, long ttl, TimeUnit timeUnit) {
        Instant expiry = ttl > 0 ? Instant.now().plusMillis(timeUnit.toMillis(ttl)) : null;
        putInternal(cacheName, key, value, expiry);
    }

    public boolean exists(String cacheName, String key) {
        return getInternal(cacheName, key) != null;
    }

    private void putInternal(String cacheName, String key, Object value, Instant expiry) {
        caches.computeIfAbsent(cacheName, unused -> new ConcurrentHashMap<>())
            .put(key, new CacheValue(value, expiry));
    }

    private Object getInternal(String cacheName, String key) {
        ConcurrentHashMap<String, CacheValue> cache = caches.computeIfAbsent(cacheName, unused -> new ConcurrentHashMap<>());
        CacheValue cacheValue = cache.get(key);
        if (cacheValue == null) {
            return null;
        }
        if (cacheValue.isExpired()) {
            cache.remove(key);
            return null;
        }
        return cacheValue.value();
    }

    private record CacheValue(Object value, Instant expiry) {
        boolean isExpired() {
            return expiry != null && Instant.now().isAfter(expiry);
        }
    }
}