package com.travelmate.tripservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {
            // Create the JSON serializer for Redis cache values
            GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
            StringRedisSerializer keySerializer = new StringRedisSerializer();

            // Create serialization pairs for keys and values
            RedisSerializationContext.SerializationPair<String> keySerializationPair =
                    RedisSerializationContext.SerializationPair.fromSerializer(keySerializer);
            RedisSerializationContext.SerializationPair<Object> valueSerializationPair =
                    RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);

            // Base cache configuration with JSON serialization
            RedisCacheConfiguration baseCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeKeysWith(keySerializationPair)
                    .serializeValuesWith(valueSerializationPair)
                    .disableCachingNullValues();

            // Define specific cache TTLs
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

            // Trip cache configs (all inherit the serializer from baseCacheConfig)
            cacheConfigurations.put("trips", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("allTrips", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("tripsByDestination", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("tripsByPrice", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("tripSuggestions", baseCacheConfig.entryTtl(Duration.ofMinutes(15)));
            cacheConfigurations.put("tripNames", baseCacheConfig.entryTtl(Duration.ofMinutes(15)));

            // Destination cache configs
            cacheConfigurations.put("destinations", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("allDestinations", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("destinationsByRegion", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("destinationsByCountry", baseCacheConfig.entryTtl(Duration.ofMinutes(5)));
            cacheConfigurations.put("destinationSuggestions", baseCacheConfig.entryTtl(Duration.ofMinutes(15)));

            // Create cache manager with configs
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(baseCacheConfig)
                    .withInitialCacheConfigurations(cacheConfigurations)
                    .build();
        } catch (Exception e) {
            logger.error("Error creating RedisCacheManager, falling back to local cache", e);
            return new org.springframework.cache.concurrent.ConcurrentMapCacheManager();
        }
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    /**
     * Custom error handler to prevent cache exceptions from breaking application flow
     */
    static class RedisCacheErrorHandler implements CacheErrorHandler {
        private static final Logger log = LoggerFactory.getLogger(RedisCacheErrorHandler.class);

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.error("Cache GET error - cache: {}, key: {}", cache.getName(), key, exception);
            // Allow the application to continue without breaking
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.error("Cache PUT error - cache: {}, key: {}", cache.getName(), key, exception);
            // Allow the application to continue without breaking
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.error("Cache EVICT error - cache: {}, key: {}", cache.getName(), key, exception);
            // Allow the application to continue without breaking
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.error("Cache CLEAR error - cache: {}", cache.getName(), exception);
            // Allow the application to continue without breaking
        }
    }
}
