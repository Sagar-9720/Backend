package com.travelmate.emailservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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

    @Bean
    public Bucket emailRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(tokensPerMinute,
            Refill.intervally(tokensPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
