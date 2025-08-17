package com.travelmate.emailservice.service;

import com.travelmate.emailservice.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageListener {

    private final EmailService emailService;

    @KafkaListener(
        topics = "email_verification",
        groupId = "emailservice-group",
        containerFactory = "emailKafkaListenerContainerFactory"
    )
    public void handleVerificationEmail(EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest);
            log.info("Verification email sent successfully to: {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", emailRequest.getTo(), e.getMessage());
            // Optionally, send to a dead-letter topic here
        }
    }

    @KafkaListener(
        topics = "email_password_reset",
        groupId = "emailservice-group",
        containerFactory = "emailKafkaListenerContainerFactory"
    )
    public void handlePasswordResetEmail(EmailRequest emailRequest) {
        try {
            emailService.sendEmail(emailRequest);
            log.info("Password reset email sent successfully to: {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", emailRequest.getTo(), e.getMessage());
            // Optionally, send to a dead-letter topic here
        }
    }
}
