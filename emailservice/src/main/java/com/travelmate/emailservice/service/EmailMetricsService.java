package com.travelmate.emailservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailMetricsService {
    private final Counter emailsSentCounter;
    private final Counter emailsFailedCounter;
    private final Counter rateLimitHitsCounter;
    private final Timer emailSendingTimer;
    private final Counter verificationEmailsCounter;
    private final Counter passwordResetEmailsCounter;

    public EmailMetricsService(MeterRegistry registry) {
        this.emailsSentCounter = Counter.builder("emails.sent.total")
                .description("Total number of emails sent successfully")
                .register(registry);

        this.emailsFailedCounter = Counter.builder("emails.failed.total")
                .description("Total number of failed email attempts")
                .register(registry);

        this.rateLimitHitsCounter = Counter.builder("emails.ratelimit.hits")
                .description("Number of rate limit hits")
                .register(registry);

        this.emailSendingTimer = Timer.builder("emails.sending.time")
                .description("Time taken to send emails")
                .register(registry);

        this.verificationEmailsCounter = Counter.builder("emails.verification.sent")
                .description("Number of verification emails sent")
                .register(registry);

        this.passwordResetEmailsCounter = Counter.builder("emails.password.reset.sent")
                .description("Number of password reset emails sent")
                .register(registry);
    }

    public void recordEmailSent() {
        emailsSentCounter.increment();
    }

    public void recordEmailFailed() {
        emailsFailedCounter.increment();
    }

    public void recordRateLimitHit() {
        rateLimitHitsCounter.increment();
    }

    public void recordEmailSendingTime(long timeMs) {
        emailSendingTimer.record(timeMs, TimeUnit.MILLISECONDS);
    }

    public void recordVerificationEmailSent() {
        verificationEmailsCounter.increment();
    }

    public void recordPasswordResetEmailSent() {
        passwordResetEmailsCounter.increment();
    }
}
