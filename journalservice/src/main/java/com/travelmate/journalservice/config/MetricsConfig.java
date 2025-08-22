package com.travelmate.journalservice.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
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

/**
 * Configuration class for Journal Service metrics
 */
@Configuration
public class MetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Value("${spring.application.name:journal-service}")
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
    public Runnable registerJournalMetrics(MeterRegistry registry) {
        // Journal operation counters
        registry.counter("journal.created", "type", "journal");
        registry.counter("journal.updated", "type", "journal");
        registry.counter("journal.deleted", "type", "journal");
        registry.counter("journal.viewed", "type", "journal");

        // Tag operations
        registry.counter("journal.tags.added", "type", "tag");
        registry.counter("journal.tags.removed", "type", "tag");

        // Performance measurements
        registry.gauge("journal.average.size.bytes", Tags.empty(), 0);
        registry.gauge("journal.average.entries", Tags.empty(), 0);

        return () -> {}; // Return no-op runnable
    }
}
