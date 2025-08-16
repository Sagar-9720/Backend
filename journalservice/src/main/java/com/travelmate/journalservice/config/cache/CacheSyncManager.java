package com.travelmate.journalservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CacheSyncManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheSyncManager.class);
    private final String instanceId = UUID.randomUUID().toString();
    private static final String CACHE_SYNC_TOPIC = "cache:sync:trips";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private ObjectMapper objectMapper;

    public void publishCacheEvent(String cacheName, String key, String operation) {
        try {
            CacheEvent event = new CacheEvent(cacheName, key, operation, instanceId);
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CACHE_SYNC_TOPIC, message);
            logger.debug("Published cache event: {}", message);
        } catch (Exception e) {
            logger.error("Error publishing cache event", e);
        }
    }

    public void handleCacheEvent(CacheEvent event) {
        // Skip if event is from this instance
        if (instanceId.equals(event.getInstanceId())) {
            return;
        }

        try {
            switch (event.getOperation()) {
                case "CLEAR":
                    cacheManager.getCache(event.getCacheName()).clear();
                    break;
                case "DELETE":
                    cacheManager.getCache(event.getCacheName()).evict(event.getKey());
                    break;
                default:
                    logger.warn("Unknown cache operation: {}", event.getOperation());
            }
            logger.debug("Processed cache event: {} for cache: {}", event.getOperation(), event.getCacheName());
        } catch (Exception e) {
            logger.error("Error handling cache event", e);
        }
    }
}
