package com.travelmate.tripservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.model.TripModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericCacheJsonSerializer(@Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    @Bean
    public Jackson2JsonRedisSerializer<TripModel> tripModelSerializer(@Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        Jackson2JsonRedisSerializer<TripModel> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, TripModel.class);
        return serializer;
    }

    @Bean
    public Jackson2JsonRedisSerializer<DestinationModel> destinationModelSerializer(@Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        Jackson2JsonRedisSerializer<DestinationModel> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, DestinationModel.class);
        return serializer;
    }

    @Bean
    public Jackson2JsonRedisSerializer<List> listSerializer(@Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        Jackson2JsonRedisSerializer<List> serializer = new Jackson2JsonRedisSerializer<>(redisObjectMapper, List.class);
        return serializer;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer genericCacheJsonSerializer, Jackson2JsonRedisSerializer<TripModel> tripModelSerializer, Jackson2JsonRedisSerializer<DestinationModel> destinationModelSerializer) {
        try {
            StringRedisSerializer keySerializer = new StringRedisSerializer();

            RedisSerializationContext.SerializationPair<String> keyPair = RedisSerializationContext.SerializationPair.fromSerializer(keySerializer);

            // Default configuration with generic serializer for lists
            RedisSerializationContext.SerializationPair<Object> genericValuePair = RedisSerializationContext.SerializationPair.fromSerializer(genericCacheJsonSerializer);

            RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig().prefixCacheNameWith("cache:v5:")  // Changed version to invalidate old cache
                    .serializeKeysWith(keyPair).serializeValuesWith(genericValuePair).disableCachingNullValues().entryTtl(Duration.ofMinutes(10));

            Map<String, RedisCacheConfiguration> caches = new HashMap<>();

            // Trip caches - single objects use specific serializer (create new config for type safety)
            RedisCacheConfiguration tripConfig = RedisCacheConfiguration.defaultCacheConfig().prefixCacheNameWith("cache:v5:").serializeKeysWith(keyPair).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(tripModelSerializer)).disableCachingNullValues().entryTtl(Duration.ofMinutes(5));
            caches.put("trips", tripConfig);

            // Destination caches - single objects use specific serializer (create new config for type safety)
            RedisCacheConfiguration destinationConfig = RedisCacheConfiguration.defaultCacheConfig().prefixCacheNameWith("cache:v5:").serializeKeysWith(keyPair).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(destinationModelSerializer)).disableCachingNullValues().entryTtl(Duration.ofMinutes(5));
            caches.put("destinations", destinationConfig);

            // All list caches use the base configuration with generic serializer
            caches.put("allTrips", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("tripsByDestination", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("tripsByPrice", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("tripSuggestions", base.entryTtl(Duration.ofMinutes(15)));
            caches.put("tripNames", base.entryTtl(Duration.ofMinutes(15)));

            caches.put("allDestinations", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("destinationsByRegion", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("destinationsByCountry", base.entryTtl(Duration.ofMinutes(5)));
            caches.put("destinationSuggestions", base.entryTtl(Duration.ofMinutes(15)));

            return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(base).withInitialCacheConfigurations(caches).transactionAware().build();

        } catch (Exception e) {
            logger.error("Error creating RedisCacheManager, falling back to local cache", e);
            return new ConcurrentMapCacheManager();
        }
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    static class RedisCacheErrorHandler implements CacheErrorHandler {
        private static final Logger log = LoggerFactory.getLogger(RedisCacheErrorHandler.class);

        @Override
        public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
            log.error("Cache GET error - cache: {}, key: {}", cache.getName(), key, ex);
        }

        @Override
        public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
            log.error("Cache PUT error - cache: {}, key: {}", cache.getName(), key, ex);
        }

        @Override
        public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
            log.error("Cache EVICT error - cache: {}, key: {}", cache.getName(), key, ex);
        }

        @Override
        public void handleCacheClearError(RuntimeException ex, Cache cache) {
            log.error("Cache CLEAR error - cache: {}", cache.getName(), ex);
        }
    }
}
