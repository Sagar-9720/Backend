package com.travelmate.tripservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

public class CacheMessageListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(CacheMessageListener.class);

    private final CacheSyncManager cacheSyncManager;
    private final GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

    public CacheMessageListener(CacheSyncManager cacheSyncManager, ObjectMapper objectMapper) {
        this.cacheSyncManager = cacheSyncManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            CacheEvent event = (CacheEvent) serializer.deserialize(message.getBody());
            cacheSyncManager.handleCacheEvent(event);
        } catch (Exception e) {
            logger.error("Error processing cache sync message", e);
        }
    }

}
