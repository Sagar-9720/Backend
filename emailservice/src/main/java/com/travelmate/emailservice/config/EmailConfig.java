package com.travelmate.emailservice.config;

import com.travelmate.emailservice.dto.EmailRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Configuration
public class EmailConfig {

    @Value("${email.retry.initial-interval:1000}")
    private long retryInitialInterval;

    @Value("${email.retry.max-interval:10000}")
    private long retryMaxInterval;

    @Value("${email.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${email.rate-limit.tokens-per-minute:60}")
    private int tokensPerMinute;

    @Value("${spring.json.trusted.packages:com.travelmate.emailservice.dto,com.travelmate.authservice.dto}")
    private String trustedPackages;

    @Bean
    public Bucket emailRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(tokensPerMinute,
                Refill.intervally(tokensPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public ConsumerFactory<String, EmailRequest> emailConsumerFactory() {
        JsonDeserializer<EmailRequest> deserializer = new JsonDeserializer<>(EmailRequest.class);
        deserializer.addTrustedPackages(trustedPackages);

        // Create a custom type mapper for the class mapping
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("com.travelmate.authservice.dto.EmailRequest", EmailRequest.class);
        typeMapper.setIdClassMapping(mappings);

        // Apply the type mapper to the deserializer
        deserializer.setTypeMapper(typeMapper);
        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, "emailservice-group"
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailRequest> emailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(emailConsumerFactory());
        return factory;
    }
}
