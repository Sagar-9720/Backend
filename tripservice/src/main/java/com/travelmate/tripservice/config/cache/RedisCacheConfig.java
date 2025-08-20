package com.travelmate.tripservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisCacheConfig {

    private static final String CACHE_SYNC_TOPIC = "cache:sync:trips";

    private final MeterRegistry meterRegistry;

    @Autowired
    public RedisCacheConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            return template;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(RedisCacheConfig.class).error("Error creating RedisTemplate", e);
            throw e;
        }
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        try {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.addMessageListener(listenerAdapter, new ChannelTopic(CACHE_SYNC_TOPIC));
            return container;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(RedisCacheConfig.class).error("Error creating RedisMessageListenerContainer", e);
            throw e;
        }
    }

    @Bean
    public MessageListenerAdapter messageListener(CacheSyncManager cacheSyncManager, ObjectMapper objectMapper) {
        try {
            MessageListenerAdapter adapter = new MessageListenerAdapter(new CacheMessageListener(cacheSyncManager, objectMapper), "onMessage");
            adapter.setSerializer(new GenericJackson2JsonRedisSerializer());
            return adapter;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(RedisCacheConfig.class).error("Error creating MessageListenerAdapter", e);
            throw e;
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        try {
            return new ObjectMapper();
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(RedisCacheConfig.class).error("Error creating ObjectMapper", e);
            throw e;
        }
    }
}
