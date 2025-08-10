package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.EmailRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceClient {
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;
    private static final String TOPIC_VERIFICATION = "email_verification";
    private static final String TOPIC_PASSWORD_RESET = "email_password_reset";

    public EmailServiceClient(KafkaTemplate<String, EmailRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendVerificationEmail(String email, String name, String verificationLink) {
        EmailRequest request = new EmailRequest(
            email,
            "Email Verification",
            verificationLink,
            name,
            EmailRequest.EmailType.VERIFICATION
        );
        kafkaTemplate.send(TOPIC_VERIFICATION, request);
    }

    public void sendPasswordResetEmail(String email, String name, String resetLink) {
        EmailRequest request = new EmailRequest(
            email,
            "Password Reset Request",
            resetLink,
            name,
            EmailRequest.EmailType.PASSWORD_RESET
        );
        kafkaTemplate.send(TOPIC_PASSWORD_RESET, request);
    }
}
