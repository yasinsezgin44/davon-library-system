package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuthenticationServiceTest {

    @Inject
    AuthenticationService authenticationService;

    @InjectMock
    UserRepository userRepository;

    @InjectMock
    Pbkdf2PasswordHash passwordHash;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashedpassword");
        user.setActive(true);
    }

    @Test
    void testAuthenticate() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHash.verify("password".toCharArray(), "hashedpassword")).thenReturn(true);

        User authenticatedUser = authenticationService.authenticate("testuser", "password");

        assertEquals("testuser", authenticatedUser.getUsername());
    }

    @Test
    void testAuthenticate_userNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        assertThrows(NotAuthorizedException.class, () -> authenticationService.authenticate("testuser", "password"));
    }

    @Test
    void testAuthenticate_invalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHash.verify("wrongpassword".toCharArray(), "hashedpassword")).thenReturn(false);
        assertThrows(NotAuthorizedException.class, () -> authenticationService.authenticate("testuser", "wrongpassword"));
    }

    @Test
    void testRegister() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordHash.generate(any(char[].class))).thenReturn("newhashedpassword");

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        authenticationService.register(newUser, "newpassword");

        Mockito.verify(userRepository).persist(any(User.class));
    }
}
