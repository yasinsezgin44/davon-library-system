package com.davon.library.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailService {
    private final String fromEmail = "noreply@librarysystem.com";

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String subject = "Verify Your Library Account";
        String content = "Please click the link below to verify your account:\n" +
                "http://library.example.com/verify?token=" + verificationToken;

        sendEmail(toEmail, subject, content);
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "Reset Your Library Account Password";
        String content = "Please click the link below to reset your password:\n" +
                "http://library.example.com/reset-password?token=" + resetToken;

        sendEmail(toEmail, subject, content);
    }

    public void sendSecurityNotification(String toEmail, String message) {
        String subject = "Security Notification - Library Account";
        sendEmail(toEmail, subject, message);
    }

    private void sendEmail(String toEmail, String subject, String content) {
        // In a real implementation, this would connect to an email service
        // For now, just log the email
        log.info("Sending email to: {}", toEmail);
        log.info("Subject: {}", subject);
        log.info("Content: {}", content);
    }
}
