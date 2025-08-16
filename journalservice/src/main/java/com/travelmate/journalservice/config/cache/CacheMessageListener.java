package com.travelmate.journalservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class CacheMessageListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(CacheMessageListener.class);

    private final CacheSyncManager cacheSyncManager;
    private final ObjectMapper objectMapper;

    public CacheMessageListener(CacheSyncManager cacheSyncManager, ObjectMapper objectMapper) {
        this.cacheSyncManager = cacheSyncManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            CacheEvent event = objectMapper.readValue(messageBody, CacheEvent.class);
            cacheSyncManager.handleCacheEvent(event);
        } catch (Exception e) {
            logger.error("Error processing cache sync message", e);
        }
    }
}
