package com.travelmate.authservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        
        String emailContent = String.format(
            """
            <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>Please click the link below to reset your password:</p>
                    <p><a href="%s">Reset Password</a></p>
                    <p>This link will expire in 30 minutes.</p>
                    <p>If you didn't request a password reset, please ignore this email.</p>
                </body>
            </html>
            """,
            resetLink
        );
        
        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String verificationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Email Verification");

        String emailContent = String.format(
            """
            <html>
                <body>
                    <h2>Email Verification</h2>
                    <p>Please click the link below to verify your email address:</p>
                    <p><a href="%s">Verify Email</a></p>
                    <p>This link will expire in 24 hours.</p>
                    <p>If you didn't create an account, please ignore this email.</p>
                </body>
            </html>
            """,
            verificationLink
        );

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}
