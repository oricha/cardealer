package com.crashedcarsales.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
    }

    // ==================== BASIC CACHE OPERATIONS ====================

    /**
     * Get value from cache
     */
    public Object get(String cacheName, String key) {
        try {
            String fullKey = buildKey(cacheName, key);
            Object value = redisTemplate.opsForValue().get(fullKey);

            if (value != null) {
                logger.debug("Cache hit for key: {} in cache: {}", key, cacheName);
            } else {
                logger.debug("Cache miss for key: {} in cache: {}", key, cacheName);
            }

            return value;
        } catch (Exception e) {
            logger.error("Error getting value from cache: {}:{}", cacheName, key, e);
            return null;
        }
    }

    /**
     * Put value in cache with default TTL
     */
    public void put(String cacheName, String key, Object value) {
        try {
            String fullKey = buildKey(cacheName, key);
            redisTemplate.opsForValue().set(fullKey, value);

            logger.debug("Cached value for key: {} in cache: {}", key, cacheName);
        } catch (Exception e) {
            logger.error("Error putting value in cache: {}:{}", cacheName, key, e);
        }
    }

    /**
     * Put value in cache with custom TTL
     */
    public void put(String cacheName, String key, Object value, long timeout, TimeUnit unit) {
        try {
            String fullKey = buildKey(cacheName, key);
            redisTemplate.opsForValue().set(fullKey, value, timeout, unit);

            logger.debug("Cached value for key: {} in cache: {} with TTL: {} {}",
                key, cacheName, timeout, unit);
        } catch (Exception e) {
            logger.error("Error putting value in cache with TTL: {}:{}", cacheName, key, e);
        }
    }

    /**
     * Delete value from cache
     */
    public boolean evict(String cacheName, String key) {
        try {
            String fullKey = buildKey(cacheName, key);
            Boolean deleted = redisTemplate.delete(fullKey);

            if (Boolean.TRUE.equals(deleted)) {
                logger.debug("Deleted cache entry for key: {} in cache: {}", key, cacheName);
            }

            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            logger.error("Error deleting cache entry: {}:{}", cacheName, key, e);
            return false;
        }
    }

    /**
     * Delete all entries in a cache
     */
    public void evictAll(String cacheName) {
        try {
            Set<String> keys = redisTemplate.keys(buildKey(cacheName, "*"));
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                logger.info("Cleared {} entries from cache: {}", keys.size(), cacheName);
            }
        } catch (Exception e) {
            logger.error("Error clearing cache: {}", cacheName, e);
        }
    }

    /**
     * Check if key exists in cache
     */
    public boolean exists(String cacheName, String key) {
        try {
            String fullKey = buildKey(cacheName, key);
            return Boolean.TRUE.equals(redisTemplate.hasKey(fullKey));
        } catch (Exception e) {
            logger.error("Error checking cache existence: {}:{}", cacheName, key, e);
            return false;
        }
    }

    // ==================== CAR-SPECIFIC CACHE OPERATIONS ====================

    /**
     * Cache car search results
     */
    public void cacheCarSearch(String searchKey, Object results) {
        put("car-search", searchKey, results, 5, TimeUnit.MINUTES);
    }

    /**
     * Get cached car search results
     */
    public Object getCachedCarSearch(String searchKey) {
        return get("car-search", searchKey);
    }

    /**
     * Cache car listings
     */
    public void cacheCarListings(String listKey, Object listings) {
        put("car-listings", listKey, listings, 5, TimeUnit.MINUTES);
    }

    /**
     * Get cached car listings
     */
    public Object getCachedCarListings(String listKey) {
        return get("car-listings", listKey);
    }

    /**
     * Cache car details
     */
    public void cacheCarDetails(String carId, Object carDetails) {
        put("car-details", carId, carDetails, 15, TimeUnit.MINUTES);
    }

    /**
     * Get cached car details
     */
    public Object getCachedCarDetails(String carId) {
        return get("car-details", carId);
    }

    /**
     * Cache featured cars
     */
    public void cacheFeaturedCars(Object featuredCars) {
        put("featured-cars", "all", featuredCars, 1, TimeUnit.HOURS);
    }

    /**
     * Get cached featured cars
     */
    public Object getCachedFeaturedCars() {
        return get("featured-cars", "all");
    }

    // ==================== DEALER-SPECIFIC CACHE OPERATIONS ====================

    /**
     * Cache dealer profile
     */
    public void cacheDealerProfile(String dealerId, Object profile) {
        put("dealer-profile", dealerId, profile, 15, TimeUnit.MINUTES);
    }

    /**
     * Get cached dealer profile
     */
    public Object getCachedDealerProfile(String dealerId) {
        return get("dealer-profile", dealerId);
    }

    /**
     * Cache dealer statistics
     */
    public void cacheDealerStats(String dealerId, Object stats) {
        put("dealer-stats", dealerId, stats, 5, TimeUnit.MINUTES);
    }

    /**
     * Get cached dealer statistics
     */
    public Object getCachedDealerStats(String dealerId) {
        return get("dealer-stats", dealerId);
    }

    // ==================== CACHE INVALIDATION STRATEGIES ====================

    /**
     * Invalidate car-related caches when car is updated
     */
    public void invalidateCarCaches(String carId) {
        logger.info("Invalidating car caches for car ID: {}", carId);

        // Delete specific car cache
        evict("car-details", carId);

        // Clear search results that might contain this car
        evictAll("car-search");

        // Clear featured cars cache
        evict("featured-cars", "all");

        logger.debug("Car caches invalidated for car ID: {}", carId);
    }

    /**
     * Invalidate dealer-related caches when dealer data is updated
     */
    public void invalidateDealerCaches(String dealerId) {
        logger.info("Invalidating dealer caches for dealer ID: {}", dealerId);

        // Delete dealer profile and stats
        evict("dealer-profile", dealerId);
        evict("dealer-stats", dealerId);

        // Clear car listings for this dealer
        evictAll("car-listings");

        logger.debug("Dealer caches invalidated for dealer ID: {}", dealerId);
    }

    /**
     * Invalidate all caches (for system maintenance)
     */
    public void invalidateAllCaches() {
        logger.info("Invalidating all caches");

        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            evictAll(cacheName);
        }

        logger.info("All caches invalidated");
    }

    /**
     * Invalidate search caches (for search index updates)
     */
    public void invalidateSearchCaches() {
        logger.info("Invalidating search caches");

        evictAll("car-search");
        evictAll("car-listings");

        logger.debug("Search caches invalidated");
    }

    // ==================== SESSION MANAGEMENT ====================

    /**
     * Store session data
     */
    public void storeSession(String sessionId, Map<String, Object> sessionData) {
        put("session", sessionId, sessionData, 30, TimeUnit.MINUTES);
        logger.debug("Stored session data for session ID: {}", sessionId);
    }

    /**
     * Get session data
     */
    public Map<String, Object> getSession(String sessionId) {
        Object sessionData = get("session", sessionId);
        if (sessionData instanceof Map) {
            return (Map<String, Object>) sessionData;
        }
        return null;
    }

    /**
     * Update session data
     */
    public void updateSession(String sessionId, String key, Object value) {
        Map<String, Object> sessionData = getSession(sessionId);
        if (sessionData == null) {
            sessionData = new HashMap<>();
        }

        sessionData.put(key, value);
        storeSession(sessionId, sessionData);
    }

    /**
     * Delete session
     */
    public void deleteSession(String sessionId) {
        evict("session", sessionId);
        logger.debug("Deleted session for session ID: {}", sessionId);
    }

    /**
     * Extend session TTL
     */
    public void extendSession(String sessionId) {
        Object sessionData = get("session", sessionId);
        if (sessionData != null) {
            put("session", sessionId, sessionData, 30, TimeUnit.MINUTES);
            logger.debug("Extended session TTL for session ID: {}", sessionId);
        }
    }

    // ==================== CACHE STATISTICS ====================

    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            stats.put("cacheNames", cacheNames);
            stats.put("totalCaches", cacheNames.size());

            // Get Redis info
            Map<String, Object> redisInfo = new HashMap<>();
            Set<String> allKeys = redisTemplate.keys("*");
            redisInfo.put("totalKeys", allKeys != null ? allKeys.size() : 0);

            stats.put("redisInfo", redisInfo);

        } catch (Exception e) {
            logger.error("Error getting cache statistics", e);
            stats.put("error", e.getMessage());
        }

        return stats;
    }

    /**
     * Get cache keys for a specific cache
     */
    public Set<String> getCacheKeys(String cacheName) {
        try {
            Set<String> keys = redisTemplate.keys(buildKey(cacheName, "*"));
            if (keys != null) {
                // Remove the cache name prefix to return just the keys
                Set<String> cleanKeys = new HashSet<>();
                String prefix = cacheName + ":";
                for (String key : keys) {
                    if (key.startsWith(prefix)) {
                        cleanKeys.add(key.substring(prefix.length()));
                    }
                }
                return cleanKeys;
            }
            return Set.of();
        } catch (Exception e) {
            logger.error("Error getting cache keys for cache: {}", cacheName, e);
            return Set.of();
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Build full cache key with cache name prefix
     */
    private String buildKey(String cacheName, String key) {
        return cacheName + ":" + key;
    }

    /**
     * Get cache TTL for a key
     */
    public Long getTtl(String cacheName, String key) {
        try {
            String fullKey = buildKey(cacheName, key);
            return redisTemplate.getExpire(fullKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error getting TTL for cache: {}:{}", cacheName, key, e);
            return -1L;
        }
    }

    /**
     * Set cache TTL for a key
     */
    public boolean setTtl(String cacheName, String key, long timeout, TimeUnit unit) {
        try {
            String fullKey = buildKey(cacheName, key);
            return Boolean.TRUE.equals(redisTemplate.expire(fullKey, timeout, unit));
        } catch (Exception e) {
            logger.error("Error setting TTL for cache: {}:{}", cacheName, key, e);
            return false;
        }
    }
}