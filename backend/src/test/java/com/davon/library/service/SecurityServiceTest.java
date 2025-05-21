package com.davon.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityService securityService;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void testHashPassword() {
        // Act
        String hash1 = securityService.hashPassword("password123");
        String hash2 = securityService.hashPassword("password123");
        String hash3 = securityService.hashPassword("differentPassword");

        // Assert
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotNull(hash3);
        assertEquals(hash1, hash2); // Same password should produce same hash
        assertNotEquals(hash1, hash3); // Different passwords should produce different hashes
    }

    @Test
    void testVerifyPassword() {
        // Arrange
        String password = "securePassword123";
        String hash = securityService.hashPassword(password);

        // Act
        boolean validMatch = securityService.verifyPassword(password, hash);
        boolean invalidMatch = securityService.verifyPassword("wrongPassword", hash);

        // Assert
        assertTrue(validMatch);
        assertFalse(invalidMatch);
    }

    @Test
    void testGenerateAndValidateVerificationToken() {
        // Arrange
        Long userId = 123L;

        // Act
        String token = securityService.generateVerificationToken(userId);
        Long retrievedUserId = securityService.validateVerificationToken(token);

        // Assert
        assertNotNull(token);
        assertEquals(userId, retrievedUserId);

        // Token should be invalidated after validation
        Long secondValidation = securityService.validateVerificationToken(token);
        assertNull(secondValidation);
    }

    @Test
    void testGenerateAndValidatePasswordResetToken() {
        // Arrange
        Long userId = 456L;

        // Act
        String token = securityService.generatePasswordResetToken(userId);
        Long retrievedUserId = securityService.validatePasswordResetToken(token);

        // Assert
        assertNotNull(token);
        assertEquals(userId, retrievedUserId);

        // Token should be invalidated after validation
        Long secondValidation = securityService.validatePasswordResetToken(token);
        assertNull(secondValidation);
    }

    @Test
    void testAccountLocking() {
        // Arrange
        String username = "testUser";

        // Act - initially account should not be locked
        boolean initialLockStatus = securityService.isAccountLocked(username);

        // Lock the account
        securityService.lockAccount(username);
        boolean lockedStatus = securityService.isAccountLocked(username);

        // Unlock the account
        securityService.unlockAccount(username);
        boolean finalLockStatus = securityService.isAccountLocked(username);

        // Assert
        assertFalse(initialLockStatus);
        assertTrue(lockedStatus);
        assertFalse(finalLockStatus);
    }

    @Test
    void testLogSecurityEvent() {
        // Arrange
        String eventType = "LOGIN_ATTEMPT";
        String username = "testUser";

        // Act
        securityService.logSecurityEvent(eventType, username);

        // Assert - should output to console
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("SECURITY EVENT"));
        assertTrue(output.contains(eventType));
        assertTrue(output.contains(username));
    }

    @Test
    void testSendSecurityAlert() {
        // Arrange
        String alertMessage = "Suspicious activity detected";

        // Act
        securityService.sendSecurityAlert(alertMessage);

        // Assert - should output to console
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("SECURITY ALERT"));
        assertTrue(output.contains(alertMessage));
    }

    @Test
    void testProcessSecurityAlert() {
        // Arrange
        String alertType = "BRUTE_FORCE_ATTEMPT";
        String details = "Multiple failed logins for user: admin";

        // Act
        boolean result = securityService.processSecurityAlert(alertType, details);

        // Assert
        assertTrue(result);
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("SECURITY EVENT"));
        assertTrue(output.contains("ALERT: " + alertType));
        assertTrue(output.contains(details));
    }
}