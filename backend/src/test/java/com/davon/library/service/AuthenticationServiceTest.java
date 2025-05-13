package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private SecurityService securityService;

    @Mock
    private User mockUser;

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthenticationService(userService, emailService, securityService);
    }

    @Test
    void testSuccessfulLogin() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = "hashed_password";

        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn(username);
        when(mockUser.getPasswordHash()).thenReturn(hashedPassword);

        when(securityService.isAccountLocked(username)).thenReturn(false);
        when(securityService.hashPassword(password)).thenReturn(hashedPassword);
        when(userService.authenticateUser(username, hashedPassword)).thenReturn(mockUser);

        // Act
        AuthenticationService.LoginResult result = authService.login(username, password);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getSessionId());
        assertEquals("Login successful", result.getMessage());
    }

    @Test
    void testFailedLogin() {
        // Arrange
        String username = "testuser";
        String password = "wrong_password";
        String hashedPassword = "hashed_password";

        when(securityService.isAccountLocked(username)).thenReturn(false);
        when(securityService.hashPassword(password)).thenReturn(hashedPassword);
        when(userService.authenticateUser(username, hashedPassword)).thenReturn(null);

        // Act
        AuthenticationService.LoginResult result = authService.login(username, password);

        // Assert
        assertFalse(result.isSuccess());
        assertNull(result.getSessionId());
        assertEquals("Invalid credentials", result.getMessage());
    }

    @Test
    void testLoginWithLockedAccount() {
        // Arrange
        String username = "lockeduser";
        String password = "password123";

        when(securityService.isAccountLocked(username)).thenReturn(true);

        // Act
        AuthenticationService.LoginResult result = authService.login(username, password);

        // Assert
        assertFalse(result.isSuccess());
        assertNull(result.getSessionId());
        assertEquals("Account is locked", result.getMessage());

        // Verify no authentication attempt was made
        verify(userService, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    void testSessionValidation() {
        // First create a session
        String username = "testuser";
        String password = "password123";
        String hashedPassword = "hashed_password";

        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn(username);

        when(securityService.isAccountLocked(username)).thenReturn(false);
        when(securityService.hashPassword(password)).thenReturn(hashedPassword);
        when(userService.authenticateUser(username, hashedPassword)).thenReturn(mockUser);

        AuthenticationService.LoginResult loginResult = authService.login(username, password);
        String sessionId = loginResult.getSessionId();

        // Test validation
        boolean isValid = authService.validateSession(sessionId);
        assertTrue(isValid);

        // Test invalid session
        boolean invalidResult = authService.validateSession("invalid-session-id");
        assertFalse(invalidResult);
    }

    @Test
    void testLogout() {
        // First create a session
        String username = "testuser";
        String password = "password123";
        String hashedPassword = "hashed_password";

        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn(username);

        when(securityService.isAccountLocked(username)).thenReturn(false);
        when(securityService.hashPassword(password)).thenReturn(hashedPassword);
        when(userService.authenticateUser(username, hashedPassword)).thenReturn(mockUser);

        AuthenticationService.LoginResult loginResult = authService.login(username, password);
        String sessionId = loginResult.getSessionId();

        // Test logout
        boolean logoutResult = authService.logout(sessionId);
        assertTrue(logoutResult);

        // Session should no longer be valid
        assertFalse(authService.validateSession(sessionId));
    }

    @Test
    void testRegisterAccount() {
        // Arrange
        User newUser = mock(Member.class);
        when(newUser.getId()).thenReturn(1L);

        String email = "newuser@example.com";
        String password = "password123";
        String hashedPassword = "hashed_password";

        when(newUser.getEmail()).thenReturn(email);
        when(securityService.hashPassword(password)).thenReturn(hashedPassword);
        when(userService.createUser(any(User.class))).thenReturn(newUser);
        when(securityService.generateVerificationToken(1L)).thenReturn("verification-token");

        // Act
        boolean result = authService.registerAccount(newUser, password);

        // Assert
        assertTrue(result);
        verify(newUser).setPasswordHash(hashedPassword);
        verify(newUser).setActive(false);

        // Verify email was sent
        verify(emailService).sendVerificationEmail(email, "verification-token");
    }

    @Test
    void testVerifyEmail() {
        // Arrange
        String token = "valid-token";
        Long userId = 1L;

        when(securityService.validateVerificationToken(token)).thenReturn(userId);
        when(userService.activateUser(userId)).thenReturn(true);

        // Act
        boolean result = authService.verifyEmail(token);

        // Assert
        assertTrue(result);
        verify(userService).activateUser(userId);
    }

    @Test
    void testVerifyEmailWithInvalidToken() {
        // Arrange
        String token = "invalid-token";

        when(securityService.validateVerificationToken(token)).thenReturn(null);

        // Act
        boolean result = authService.verifyEmail(token);

        // Assert
        assertFalse(result);
        verify(userService, never()).activateUser(anyLong());
    }
}