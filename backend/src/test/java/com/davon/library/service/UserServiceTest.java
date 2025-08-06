package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @InjectMock
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setActive(true);
    }

    @Test
    void testCreateUser() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        userService.createUser(user);

        Mockito.verify(userRepository).persist(any(User.class));
    }

    @Test
    void testCreateUser_usernameExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUser() {
        User updatedDetails = new User();
        updatedDetails.setFullName("Updated Name");

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        User updatedUser = userService.updateUser(1L, updatedDetails);

        assertEquals("Updated Name", updatedUser.getFullName());
    }

    @Test
    void testUpdateUser_notFound() {
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, new User()));
    }

    @Test
    void testGetUserById() {
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));
        Optional<User> foundUser = userService.getUserById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
}
