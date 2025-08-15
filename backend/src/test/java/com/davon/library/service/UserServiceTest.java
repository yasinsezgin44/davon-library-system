package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import jakarta.inject.Inject;

@QuarkusTest
class UserServiceTest {

    @InjectMock
    UserRepository userRepository;

    @Inject
    UserService userService;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User created = userService.createUser(user);

        verify(userRepository).persist(user);
        assertEquals(user, created);
    }

    @Test
    void testCreateUserDuplicateUsername() {
        User user = new User();
        user.setUsername("duplicate");

        when(userRepository.existsByUsername("duplicate")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUser() {
        User existing = new User();
        existing.setId(1L);
        existing.setFullName("Old Name");

        User updated = new User();
        updated.setFullName("New Name");

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(existing));

        User result = userService.updateUser(1L, updated);

        assertEquals("New Name", result.getFullName());
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, new User()));
    }

    @Test
    void testDeactivateUser() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        boolean deactivated = userService.deactivateUser(1L);

        assertFalse(user.getActive());
        assertTrue(deactivated);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userRepository.listAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(user, result);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void testCountUsers() {
        when(userRepository.count()).thenReturn(5L);

        long count = userService.countUsers();

        assertEquals(5, count);
    }
}
