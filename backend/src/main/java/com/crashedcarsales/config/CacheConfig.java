package com.crashedcarsales.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${spring.cache.redis.time-to-live:600000}")
    private long defaultTtl;

    /**
     * RedisTemplate configuration for manual cache operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        logger.info("RedisTemplate configured successfully");
        return template;
    }

    /**
     * CacheManager configuration with custom TTL for different cache types
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMillis(defaultTtl))
            .serializeKeysWith(
                org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                    .fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer())
            );

        // Custom cache configurations for different data types
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Short-lived cache for frequently changing data (5 minutes)
        cacheConfigurations.put("car-search", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("car-listings", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("dealer-stats", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));

        // Medium-lived cache for relatively stable data (15 minutes)
        cacheConfigurations.put("car-details", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("dealer-profile", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        // Long-lived cache for static data (1 hour)
        cacheConfigurations.put("featured-cars", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("car-makes-models", defaultCacheConfig.entryTtl(Duration.ofHours(1)));

        // Very long-lived cache for configuration data (24 hours)
        cacheConfigurations.put("system-config", defaultCacheConfig.entryTtl(Duration.ofHours(24)));

        logger.info("CacheManager configured with custom TTL settings");
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}