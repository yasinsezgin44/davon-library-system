package com.davon.library.service;

import com.davon.library.repository.UserRepository;
import com.davon.library.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
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
    private UserRepository userRepository;

    // For demo purposes - in production these would be in a database
    private final Map<String, Long> verificationTokens = new HashMap<>();
    private final Map<String, Long> passwordResetTokens = new HashMap<>();
    private final Map<String, LocalDateTime> lockedAccounts = new HashMap<>();

    /**
     * Validates user credentials.
     *
     * @param username the username
     * @param password the plain text password
     * @return the authenticated user if valid, null otherwise
     */
    public User validateCredentials(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        String hashedPassword = hashPassword(password);

        if (user.getPasswordHash().equals(hashedPassword) && user.isActive()) {
            return user;
        }

        return null;
    }

    /**
     * Changes a user's password.
     *
     * @param userId          the user ID
     * @param currentPassword the current password
     * @param newPassword     the new password
     * @return true if password was changed successfully
     */
    @Transactional
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }

        // Verify current password
        String currentHashed = hashPassword(currentPassword);
        if (!user.getPasswordHash().equals(currentHashed)) {
            return false;
        }

        // Set new password
        String newHashed = hashPassword(newPassword);
        user.setPasswordHash(newHashed);
        userRepository.persist(user);

        return true;
    }

    /**
     * Checks if a user has the required role.
     *
     * @param user         the user to check
     * @param requiredRole the required role
     * @return true if user has the role
     */
    public boolean hasRole(User user, String requiredRole) {
        if (user == null || requiredRole == null) {
            return false;
        }

        // Simple role check based on user type
        switch (requiredRole.toLowerCase()) {
            case "admin":
                return user.isAdmin();
            case "librarian":
                return user instanceof com.davon.library.model.Librarian || user.isAdmin();
            case "member":
                return user instanceof com.davon.library.model.Member;
            default:
                return false;
        }
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password the plain text password
     * @return the hashed password
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     *
     * @param password   the plain text password
     * @param storedHash the stored hash
     * @return true if password matches
     */
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
