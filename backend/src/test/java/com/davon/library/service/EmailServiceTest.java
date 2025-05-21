package com.davon.library.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // Get Logback logger
        Logger emailServiceLogger = (Logger) LoggerFactory.getLogger(EmailService.class);

        // Create and start a ListAppender
        listAppender = new ListAppender<>();
        listAppender.start();

        // Add appender to the logger
        emailServiceLogger.addAppender(listAppender);
        emailServiceLogger.setLevel(Level.INFO); // Ensure INFO messages are captured
    }

    @AfterEach
    void tearDown() {
        // Detach appender
        Logger emailServiceLogger = (Logger) LoggerFactory.getLogger(EmailService.class);
        emailServiceLogger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    void sendVerificationEmail() {
        String testEmail = "test@example.com";
        String testToken = "testToken123";
        emailService.sendVerificationEmail(testEmail, testToken);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Sending email to: " + testEmail));
        assertTrue(logsList.get(1).getFormattedMessage().contains("Subject: Verify Your Library Account"));
        assertTrue(
                logsList.get(2).getFormattedMessage().contains("http://library.example.com/verify?token=" + testToken));
    }

    @Test
    void sendPasswordResetEmail() {
        String testEmail = "reset@example.com";
        String testToken = "resetToken456";
        emailService.sendPasswordResetEmail(testEmail, testToken);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Sending email to: " + testEmail));
        assertTrue(logsList.get(1).getFormattedMessage().contains("Subject: Reset Your Library Account Password"));
        assertTrue(logsList.get(2).getFormattedMessage()
                .contains("http://library.example.com/reset-password?token=" + testToken));
    }

    @Test
    void sendSecurityNotification() {
        String testEmail = "security@example.com";
        String message = "Test security alert.";
        emailService.sendSecurityNotification(testEmail, message);

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Sending email to: " + testEmail));
        assertTrue(logsList.get(1).getFormattedMessage().contains("Subject: Security Notification - Library Account"));
        assertTrue(logsList.get(2).getFormattedMessage().contains(message));
    }
}