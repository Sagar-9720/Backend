package com.travelmate.tripservice.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    /**
     * Generic RedisTemplate for TripModel, DestinationModel, lists, etc.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(CACHE_SYNC_TOPIC));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(CacheSyncManager cacheSyncManager) {
        return new MessageListenerAdapter(new CacheMessageListener(cacheSyncManager), "onMessage");
    }
}
