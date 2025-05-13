package com.davon.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private Logger logger; // Mock the SLF4J Logger

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Replace the actual logger in EmailService with the mock logger
        Field logField = EmailService.class.getDeclaredField("log");
        logField.setAccessible(true);
        logField.set(emailService, logger);
    }

    @Test
    void sendVerificationEmail() {
        String testEmail = "test@example.com";
        String testToken = "verificationToken123";
        String expectedSubject = "Verify Your Library Account";
        String expectedContentPrefix = "Please click the link below to verify your account:";
        String expectedLink = "http://library.example.com/verify?token=" + testToken;

        emailService.sendVerificationEmail(testEmail, testToken);

        verify(logger, times(3)).info(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        List<String> capturedValues = stringArgumentCaptor.getAllValues();

        assertEquals("Sending email to: {}", capturedValues.get(0));
        assertEquals(testEmail, capturedValues.get(1));
        assertEquals("Subject: {}", capturedValues.get(2));
        assertEquals(expectedSubject, capturedValues.get(3));
        assertEquals("Content: {}", capturedValues.get(4));
        assertTrue(capturedValues.get(5).contains(expectedContentPrefix));
        assertTrue(capturedValues.get(5).contains(expectedLink));
    }

    @Test
    void sendPasswordResetEmail() {
        String testEmail = "reset@example.com";
        String testToken = "resetToken456";
        String expectedSubject = "Reset Your Library Account Password";
        String expectedContentPrefix = "Please click the link below to reset your password:";
        String expectedLink = "http://library.example.com/reset-password?token=" + testToken;

        emailService.sendPasswordResetEmail(testEmail, testToken);

        verify(logger, times(3)).info(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        List<String> capturedValues = stringArgumentCaptor.getAllValues();

        assertEquals("Sending email to: {}", capturedValues.get(0));
        assertEquals(testEmail, capturedValues.get(1));
        assertEquals("Subject: {}", capturedValues.get(2));
        assertEquals(expectedSubject, capturedValues.get(3));
        assertEquals("Content: {}", capturedValues.get(4));
        assertTrue(capturedValues.get(5).contains(expectedContentPrefix));
        assertTrue(capturedValues.get(5).contains(expectedLink));
    }

    @Test
    void sendSecurityNotification() {
        String testEmail = "security@example.com";
        String testMessage = "This is a security alert.";
        String expectedSubject = "Security Notification - Library Account";

        emailService.sendSecurityNotification(testEmail, testMessage);

        verify(logger, times(3)).info(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        List<String> capturedValues = stringArgumentCaptor.getAllValues();

        assertEquals("Sending email to: {}", capturedValues.get(0));
        assertEquals(testEmail, capturedValues.get(1));
        assertEquals("Subject: {}", capturedValues.get(2));
        assertEquals(expectedSubject, capturedValues.get(3));
        assertEquals("Content: {}", capturedValues.get(4));
        assertEquals(testMessage, capturedValues.get(5));
    }
}