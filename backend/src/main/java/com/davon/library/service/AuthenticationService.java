package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private EmailService emailService;

    @Inject
    private SecurityService securityService;

    @Inject
    private UserRepository userRepository;

    private final Map<String, UserSession> activeSessions = new HashMap<>();
    private final Map<String, Integer> failedLoginAttempts = new HashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public LoginResult login(String username, String password) {
        if (securityService.isAccountLocked(username)) {
            return new LoginResult(false, null, "Account is locked");
        }

        User user = userService.authenticateUser(username, securityService.hashPassword(password));

        if (user == null) {
            handleFailedLogin(username);
            return new LoginResult(false, null, "Invalid credentials");
        }

        failedLoginAttempts.remove(username);

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

        if (session.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            activeSessions.remove(sessionId);
            return false;
        }

        session.setLastAccessedAt(LocalDateTime.now());
        return true;
    }

    public boolean registerAccount(User newUser, String password) throws AuthenticationException {
        try {
            newUser.setPasswordHash(securityService.hashPassword(password));
            newUser.setActive(false);
            User createdUser = userService.createUser(newUser);
            String verificationToken = securityService.generateVerificationToken(createdUser.getId());
            emailService.sendVerificationEmail(createdUser.getEmail(), verificationToken);
            return createdUser != null;
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to register account", e);
            throw new AuthenticationException("Failed to register account: " + e.getMessage(), e);
        }
    }

    public boolean verifyEmail(String token) throws AuthenticationException {
        try {
            Long userId = securityService.validateVerificationToken(token);
            if (userId == null) {
                return false;
            }
            User user = userService.findById(userId);
            if (user != null) {
                user.setActive(true);
                userService.updateUser(userId, user);
                return true;
            }
            return false;
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to verify email", e);
            throw new AuthenticationException("Failed to verify email: " + e.getMessage(), e);
        }
    }

    public boolean resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        String resetToken = securityService.generatePasswordResetToken(user.getId());
        emailService.sendPasswordResetEmail(email, resetToken);
        return true;
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) throws AuthenticationException {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return false;
            }
            if (!securityService.verifyPassword(oldPassword, user.getPasswordHash())) {
                return false;
            }
            user.setPasswordHash(securityService.hashPassword(newPassword));
            userService.updateUser(userId, user);
            return true;
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to change password", e);
            throw new AuthenticationException("Failed to change password: " + e.getMessage(), e);
        }
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
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

    public static class LoginResult {
        private boolean success;
        private String sessionId;
        private String message;

        public LoginResult(boolean success, String sessionId, String message) {
            this.success = success;
            this.sessionId = sessionId;
            this.message = message;
        }

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

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getLastAccessedAt() {
            return lastAccessedAt;
        }

        public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
            this.lastAccessedAt = lastAccessedAt;
        }
    }
}
