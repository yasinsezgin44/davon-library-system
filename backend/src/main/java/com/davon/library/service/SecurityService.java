package com.davon.library.service;

import com.davon.library.dao.UserDAO;
import com.davon.library.model.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for security operations.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class SecurityService {

    private static final Logger logger = Logger.getLogger(SecurityService.class.getName());

    @Inject
    private UserDAO userDAO;

    // For demo purposes - in production these would be in a database
    private final Map<String, Long> verificationTokens = new HashMap<>();
    private final Map<String, Long> passwordResetTokens = new HashMap<>();
    private final Map<String, LocalDateTime> lockedAccounts = new HashMap<>();

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String password, String storedHash) {
        return hashPassword(password).equals(storedHash);
    }

    public String generateVerificationToken(Long userId) {
        String token = UUID.randomUUID().toString();
        verificationTokens.put(token, userId);
        return token;
    }

    public Long validateVerificationToken(String token) {
        return verificationTokens.remove(token);
    }

    public String generatePasswordResetToken(Long userId) {
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, userId);
        return token;
    }

    public Long validatePasswordResetToken(String token) {
        return passwordResetTokens.remove(token);
    }

    public void lockAccount(String username) {
        lockedAccounts.put(username, LocalDateTime.now().plusHours(1)); // Lock for 1 hour
    }

    public boolean isAccountLocked(String username) {
        LocalDateTime lockExpiration = lockedAccounts.get(username);
        if (lockExpiration == null) {
            return false;
        }

        // Check if lock has expired
        if (lockExpiration.isBefore(LocalDateTime.now())) {
            lockedAccounts.remove(username);
            return false;
        }

        return true;
    }

    public void unlockAccount(String username) {
        lockedAccounts.remove(username);
    }

    public void logSecurityEvent(String eventType, String username) {
        // In a real implementation, this would save to a security log database
        System.out.println("SECURITY EVENT - " + eventType + " for user: " + username + " at " + LocalDateTime.now());
    }

    public void sendSecurityAlert(String alertMessage) {
        // In a real implementation, this would send alerts to security team
        System.out.println("SECURITY ALERT: " + alertMessage);
    }

    public boolean processSecurityAlert(String alertType, String details) {
        // Process security alert based on type (suspicious activity, breach attempt,
        // etc.)
        logSecurityEvent("ALERT: " + alertType, details);
        return true;
    }
}
