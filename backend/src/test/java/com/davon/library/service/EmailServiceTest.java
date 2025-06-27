package com.davon.library.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    private TestLogHandler testLogHandler;
    private Logger emailServiceLogger;

    @BeforeEach
    void setUp() {
        // Get Java Util Logging logger
        emailServiceLogger = Logger.getLogger(EmailService.class.getName());

        // Create and add custom test handler
        testLogHandler = new TestLogHandler();
        emailServiceLogger.addHandler(testLogHandler);
        emailServiceLogger.setLevel(java.util.logging.Level.INFO);
    }

    @AfterEach
    void tearDown() {
        // Remove test handler
        emailServiceLogger.removeHandler(testLogHandler);
    }

    @Test
    void sendVerificationEmail() {
        String testEmail = "test@example.com";
        String testToken = "testToken123";
        emailService.sendVerificationEmail(testEmail, testToken);

        List<String> logMessages = testLogHandler.getMessages();
        assertEquals(3, logMessages.size());
        assertTrue(logMessages.get(0).contains("Sending email to: " + testEmail));
        assertTrue(logMessages.get(1).contains("Subject: Verify Your Library Account"));
        assertTrue(logMessages.get(2).contains("http://library.example.com/verify?token=" + testToken));
    }

    @Test
    void sendPasswordResetEmail() {
        String testEmail = "reset@example.com";
        String testToken = "resetToken456";
        emailService.sendPasswordResetEmail(testEmail, testToken);

        List<String> logMessages = testLogHandler.getMessages();
        assertEquals(3, logMessages.size());
        assertTrue(logMessages.get(0).contains("Sending email to: " + testEmail));
        assertTrue(logMessages.get(1).contains("Subject: Reset Your Library Account Password"));
        assertTrue(logMessages.get(2).contains("http://library.example.com/reset-password?token=" + testToken));
    }

    @Test
    void sendSecurityNotification() {
        String testEmail = "security@example.com";
        String message = "Test security alert.";
        emailService.sendSecurityNotification(testEmail, message);

        List<String> logMessages = testLogHandler.getMessages();
        assertEquals(3, logMessages.size());
        assertTrue(logMessages.get(0).contains("Sending email to: " + testEmail));
        assertTrue(logMessages.get(1).contains("Subject: Security Notification - Library Account"));
        assertTrue(logMessages.get(2).contains(message));
    }

    /**
     * Custom log handler for testing
     */
    private static class TestLogHandler extends Handler {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {
            // No-op
        }

        @Override
        public void close() throws SecurityException {
            messages.clear();
        }

        public List<String> getMessages() {
            return new ArrayList<>(messages);
        }
    }
}