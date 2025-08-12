package com.travelmate.emailservice.service;

import com.travelmate.emailservice.dto.EmailRequest;
import io.github.bucket4j.Bucket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final Bucket rateLimitBucket;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Retryable(value = {MessagingException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendEmail(EmailRequest request) throws MessagingException {
        // Check rate limit
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RuntimeException("Email rate limit exceeded. Please try again later.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());

        String content = generateEmailContent(request);
        helper.setText(content, true);

        try {
            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
            throw e;
        }
    }

    @Recover
    public void recoverEmailSending(Exception e, EmailRequest request) {
        log.error("All retry attempts failed for email to {}: {}", request.getTo(), e.getMessage());
        // Here you could implement fallback logic:
        // 1. Store failed emails in database
        // 2. Notify admin
        // 3. Queue for later retry
    }

    private String generateEmailContent(EmailRequest request) {
        Context context = new Context();
        context.setVariables(Map.of("name", request.getName(), "verificationLink", request.getLink(), "resetLink", request.getLink()));

        String templateName = switch (request.getType()) {
            case VERIFICATION -> "verification";
            case PASSWORD_RESET -> "password-reset";
        };

        return templateEngine.process(templateName, context);
    }

    public boolean checkRateLimit() {
        return rateLimitBucket.tryConsume(1);
    }

    public Duration getWaitTime() {
        if (rateLimitBucket.getAvailableTokens() > 0) {
            return Duration.ZERO;
        }
        var probe = rateLimitBucket.estimateAbilityToConsume(1);
        return Duration.ofNanos(probe.getNanosToWaitForRefill());
    }
}
