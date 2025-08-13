package com.travelmate.tripservice.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class FeignClientConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, 2000L, 3);
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id).circuitBreakerConfig(CircuitBreakerConfig.custom().slidingWindowSize(10).failureRateThreshold(50).waitDurationInOpenState(Duration.ofSeconds(10)).build()).timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build()).build());
    }

    /**
     * Feign interceptor to automatically prepend "Bearer " to Authorization headers
     * if it's not already present.
     */
    @Bean
    public RequestInterceptor bearerAuthRequestInterceptor() {
        return requestTemplate -> {
            String authHeader = requestTemplate.headers().getOrDefault("Authorization", java.util.Collections.emptyList())
                    .stream().findFirst().orElse(null);

            if (authHeader != null) {
                // Always remove any existing Authorization header to avoid duplicates
                requestTemplate.header("Authorization");
                // Only add Bearer prefix if not already present
                if (!authHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", "Bearer " + authHeader);
                } else {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
