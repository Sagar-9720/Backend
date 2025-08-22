package com.travelmate.authservice.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuration class for Auth Service metrics
 */
@Configuration
public class MetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Value("${spring.application.name:auth-service}")
    private String applicationName;

    /**
     * Customizes the meter registry with application name tag
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", applicationName);
    }

    /**
     * Creates TimedAspect bean to support @Timed annotation for method-level timing metrics
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Register JVM metrics
     */
    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }

    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }

    @Bean
    public JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }

    @Bean
    public ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    @Bean
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    public UptimeMetrics uptimeMetrics() {
        return new UptimeMetrics();
    }

    /**
     * Creates custom business metrics for PIN monitoring
     */
    @Bean
    public Runnable registerAuthMetrics(MeterRegistry registry) {
        // Authentication metrics counters
        registry.counter("auth.login.success", "type", "authentication");
        registry.counter("auth.login.failures", "type", "authentication");
        registry.counter("auth.registrations", "type", "authentication");
        registry.counter("auth.token.validations", "type", "authentication");
        registry.counter("auth.token.refreshes", "type", "authentication");

        // Active sessions gauge
        AtomicInteger activeSessions = new AtomicInteger(0);
        Gauge.builder("auth.sessions.active", activeSessions::get)
            .description("Number of currently active user sessions")
            .tag("type", "sessions")
            .register(registry);

        return () -> {}; // Return no-op runnable
    }
}
