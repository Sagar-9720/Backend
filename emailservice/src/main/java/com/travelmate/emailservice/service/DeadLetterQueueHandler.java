package com.travelmate.emailservice.service;

import com.travelmate.emailservice.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueHandler {

    private final EmailService emailService;

    @KafkaListener(topics = "email_verification_dlq", groupId = "emailservice-group")
    public void handleFailedVerificationEmails(EmailRequest request) {
        log.error("Processing failed verification email for: {}", request.getTo());
        retryWithBackoff(request);
    }

    @KafkaListener(topics = "email_password_reset_dlq", groupId = "emailservice-group")
    public void handleFailedPasswordResetEmails(EmailRequest request) {
        log.error("Processing failed password reset email for: {}", request.getTo());
        retryWithBackoff(request);
    }

    private void retryWithBackoff(EmailRequest request) {
        int maxRetries = 3;
        int currentAttempt = 1;
        long backoffInterval = 5000; // Start with 5 seconds

        while (currentAttempt <= maxRetries) {
            try {
                // Wait for rate limit availability
                while (!emailService.checkRateLimit()) {
                    Thread.sleep(1000); // Wait 1 second before checking again
                }

                emailService.sendEmail(request);
                log.info("Successfully resent email to {} after {} attempts", request.getTo(), currentAttempt);
                return;
            } catch (Exception e) {
                log.warn("Retry attempt {} failed for {}: {}", currentAttempt, request.getTo(), e.getMessage());
                try {
                    Thread.sleep(backoffInterval * currentAttempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            currentAttempt++;
        }
        log.error("All retry attempts failed for {}", request.getTo());
    }
}
