package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final EmailService emailService;
    private final SecurityService securityService;

    // Map to store active sessions (in a real app, this would be in a
    // database/cache)
    private final Map<String, UserSession> activeSessions = new HashMap<>();

    // Map to track failed login attempts
    private final Map<String, Integer> failedLoginAttempts = new HashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public LoginResult login(String username, String password) {
        // Check if account is locked
        if (securityService.isAccountLocked(username)) {
            return new LoginResult(false, null, "Account is locked");
        }

        // Authenticate user
        User user = userService.authenticateUser(username, securityService.hashPassword(password));

        if (user == null) {
            handleFailedLogin(username);
            return new LoginResult(false, null, "Invalid credentials");
        }

        // Reset failed attempts on successful login
        failedLoginAttempts.remove(username);

        // Create session
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(user.getId(), username, LocalDateTime.now());
        activeSessions.put(sessionId, session);

        return new LoginResult(true, sessionId, "Login successful");
    }

    public boolean logout(String sessionId) {
        return activeSessions.remove(sessionId) != null;
    }

    public boolean validateSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }

        // Check if session is expired (e.g., 30 minutes)
        if (session.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            activeSessions.remove(sessionId);
            return false;
        }

        // Update last access time
        session.setLastAccessedAt(LocalDateTime.now());
        return true;
    }

    public boolean registerAccount(User newUser, String password) {
        // Hash the password
        newUser.setPasswordHash(securityService.hashPassword(password));

        // Create inactive user (pending email verification)
        newUser.setActive(false);

        // Save user
        User createdUser = userService.createUser(newUser);

        // Generate verification token
        String verificationToken = securityService.generateVerificationToken(createdUser.getId());

        // Send verification email
        emailService.sendVerificationEmail(createdUser.getEmail(), verificationToken);

        return createdUser != null;
    }

    public boolean verifyEmail(String token) {
        Long userId = securityService.validateVerificationToken(token);
        if (userId == null) {
            return false;
        }

        // Activate user
        return userService.activateUser(userId);
    }

    public boolean resetPassword(String email) {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return false;
        }

        // Generate password reset token
        String resetToken = securityService.generatePasswordResetToken(user.getId());

        // Send reset email
        emailService.sendPasswordResetEmail(email, resetToken);

        return true;
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userService.findById(userId);
        if (user == null) {
            return false;
        }

        // Verify old password
        if (!securityService.verifyPassword(oldPassword, user.getPasswordHash())) {
            return false;
        }

        // Update password
        user.setPasswordHash(securityService.hashPassword(newPassword));
        userService.updateUser(userId, user);

        return true;
    }

    private void handleFailedLogin(String username) {
        int attempts = failedLoginAttempts.getOrDefault(username, 0) + 1;
        failedLoginAttempts.put(username, attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            securityService.lockAccount(username);
            securityService.logSecurityEvent("Multiple failed login attempts", username);
            securityService.sendSecurityAlert("Multiple failed login attempts for user: " + username);
        }
    }

    // Inner classes for authentication data structures
    @Data
    @AllArgsConstructor
    public static class LoginResult {
        private boolean success;
        private String sessionId;
        private String message;

        // Add explicit getter methods to resolve the linter errors
        public boolean isSuccess() {
            return success;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getMessage() {
            return message;
        }
    }

    @Data
    @AllArgsConstructor
    public static class UserSession {
        private Long userId;
        private String username;
        private LocalDateTime createdAt;
        private LocalDateTime lastAccessedAt;

        public UserSession(Long userId, String username, LocalDateTime createdAt) {
            this.userId = userId;
            this.username = username;
            this.createdAt = createdAt;
            this.lastAccessedAt = createdAt;
        }
    }
}
