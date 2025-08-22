package com.travelmate.tripservice.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bind metrics to cache and redis status
 */
public class CacheMeterBinder {

    private static final Logger logger = LoggerFactory.getLogger(CacheMeterBinder.class);

    private final CacheManager cacheManager;
    private final RedisConnectionFactory redisConnectionFactory;
    private final MeterRegistry registry;
    private final AtomicInteger redisStatus = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CacheMeterBinder(CacheManager cacheManager,
                            RedisConnectionFactory redisConnectionFactory,
                            MeterRegistry registry) {
        this.cacheManager = cacheManager;
        this.redisConnectionFactory = redisConnectionFactory;
        this.registry = registry;
        setupMetrics();
    }

    private void setupMetrics() {
        if (registry != null) {
            // Register Redis connection status metric
            if (redisConnectionFactory != null) {
                Gauge.builder("redis.connected", redisStatus, AtomicInteger::get)
                     .description("Redis connection status (1=connected, 0=disconnected)")
                     .tag("type", "connection")
                     .register(registry);

                // Schedule regular checks for Redis connection status
                scheduler.scheduleAtFixedRate(this::updateRedisConnectionStatus, 0, 30, TimeUnit.SECONDS);
            }

            // Register cache metrics if cache manager exists
            if (cacheManager != null) {
                cacheManager.getCacheNames().forEach(cacheName -> {
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        // Register metrics for this cache
                        Gauge.builder("cache.size", cache, this::estimateCacheSize)
                             .tags(Tags.of("cache", cacheName))
                             .description("Estimate of cache entries")
                             .register(registry);
                    }
                });
            }
        }
    }

    private void updateRedisConnectionStatus() {
        try {
            if (redisConnectionFactory != null) {
                RedisConnection connection = null;
                try {
                    connection = redisConnectionFactory.getConnection();
                    boolean isConnected = false;
                    try {
                        isConnected = connection != null && connection.isOpen();
                    } catch (Exception e) {
                        logger.warn("Error checking if Redis connection is open: {}", e.getMessage());
                    }
                    redisStatus.set(isConnected ? 1 : 0);
                    logger.debug("Redis connection status: {}", isConnected);
                } catch (Exception e) {
                    redisStatus.set(0);
                    logger.warn("Error checking Redis connection: {}", e.getMessage());
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Exception e) {
                            logger.warn("Error closing Redis connection: {}", e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to update Redis connection status", e);
        }
    }

    private Long estimateCacheSize(Cache cache) {
        // Simplified cache size estimation - consider implementing a more accurate approach
        // for production use depending on cache implementation
        try {
            if (cache != null) {
                return 1L; // Indicates cache exists, actual size may not be available
            }
        } catch (Exception e) {
            logger.warn("Error estimating cache size: {}", e.getMessage());
        }
        return 0L;
    }
}
