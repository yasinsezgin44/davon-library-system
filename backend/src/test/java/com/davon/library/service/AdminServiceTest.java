package com.davon.library.service;

import com.davon.library.model.Role;
import com.davon.library.model.User;
import com.davon.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AdminService adminService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = Member.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    void testCreateUserAccount_Success() {
        // Arrange
        when(securityService.hashPassword("password123")).thenReturn("hashedPassword");
        when(userService.createUser(testUser)).thenReturn(testUser);

        // Act
        boolean result = adminService.createUserAccount(testUser, "password123", "LIBRARIAN");

        // Assert
        assertTrue(result);
        assertEquals("hashedPassword", testUser.getPasswordHash());
        verify(securityService).hashPassword("password123");
        verify(userService).createUser(testUser);
        verify(userService).assignRole(eq(testUser), any(Role.class));
    }

    @Test
    void testCreateUserAccount_Failure() {
        // Arrange
        when(securityService.hashPassword("password123")).thenReturn("hashedPassword");
        when(userService.createUser(testUser)).thenReturn(null);

        // Act
        boolean result = adminService.createUserAccount(testUser, "password123", "LIBRARIAN");

        // Assert
        assertFalse(result);
        verify(securityService).hashPassword("password123");
        verify(userService).createUser(testUser);
        verify(userService, never()).assignRole(any(), any());
    }

    @Test
    void testDeleteUserAccount() {
        // Arrange
        when(userService.deactivateUser(1L)).thenReturn(true);

        // Act
        boolean result = adminService.deleteUserAccount(1L);

        // Assert
        assertTrue(result);
        verify(userService).deactivateUser(1L);
    }

    @Test
    void testSetUserRole_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);

        // Act
        boolean result = adminService.setUserRole(1L, "ADMIN");

        // Assert
        assertTrue(result);
        verify(userService).findById(1L);
        verify(userService).assignRole(eq(testUser), any(Role.class));
    }

    @Test
    void testSetUserRole_UserNotFound() {
        // Arrange
        when(userService.findById(999L)).thenReturn(null);

        // Act
        boolean result = adminService.setUserRole(999L, "ADMIN");

        // Assert
        assertFalse(result);
        verify(userService).findById(999L);
        verify(userService, never()).assignRole(any(), any());
    }

    @Test
    void testLockUserAccount_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);

        // Act
        boolean result = adminService.lockUserAccount(1L);

        // Assert
        assertTrue(result);
        verify(userService).findById(1L);
        verify(securityService).lockAccount(testUser.getUsername());
    }

    @Test
    void testLockUserAccount_UserNotFound() {
        // Arrange
        when(userService.findById(999L)).thenReturn(null);

        // Act
        boolean result = adminService.lockUserAccount(999L);

        // Assert
        assertFalse(result);
        verify(userService).findById(999L);
        verify(securityService, never()).lockAccount(any());
    }

    @Test
    void testUnlockUserAccount_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUser);

        // Act
        boolean result = adminService.unlockUserAccount(1L);

        // Assert
        assertTrue(result);
        verify(userService).findById(1L);
        verify(securityService).unlockAccount(testUser.getUsername());
    }

    @Test
    void testUnlockUserAccount_UserNotFound() {
        // Arrange
        when(userService.findById(999L)).thenReturn(null);

        // Act
        boolean result = adminService.unlockUserAccount(999L);

        // Assert
        assertFalse(result);
        verify(userService).findById(999L);
        verify(securityService, never()).unlockAccount(any());
    }

    @Test
    void testListUserAccounts() {
        // Arrange
        User secondUser = Member.builder().id(2L).username("user2").build();
        List<User> userList = Arrays.asList(testUser, secondUser);
        when(userService.searchUsers("")).thenReturn(userList);

        // Act
        List<User> result = adminService.listUserAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService).searchUsers("");
    }

    @Test
    void testConfigureSecurityPolicy() {
        // Arrange
        Map<String, String> policySettings = new HashMap<>();
        policySettings.put("passwordComplexity", "strong");
        policySettings.put("sessionTimeout", "30");

        // Act
        adminService.configureSecurityPolicy(policySettings);

        // No assertions needed for void methods with no side effects visible through
        // mocks
    }

    @Test
    void testViewSecurityLogs() {
        // Act
        List<String> logs = adminService.viewSecurityLogs();

        // Assert
        assertNotNull(logs);
        assertTrue(logs.isEmpty()); // Placeholder implementation returns empty list
    }
}