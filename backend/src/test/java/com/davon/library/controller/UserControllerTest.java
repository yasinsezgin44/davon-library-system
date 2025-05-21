package com.davon.library.controller;

import com.davon.library.model.Member;
import com.davon.library.model.User;
import com.davon.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    void testGetUsersWithNullFilter() {
        List<User> users = Arrays.asList(new Member(), new Member());
        when(userService.searchUsers("")).thenReturn(users);
        List<User> result = userController.getUsers(null);
        assertEquals(2, result.size());
        verify(userService, times(1)).searchUsers("");
    }

    @Test
    void testGetUsersWithFilter() {
        List<User> users = Arrays.asList(new Member());
        when(userService.searchUsers(anyString())).thenReturn(users);
        List<User> result = userController.getUsers("filter");
        assertEquals(1, result.size());
        verify(userService, times(1)).searchUsers("filter");
    }

    @Test
    void testCreateUser() {
        Member member = new Member();
        when(userService.createUser(any(User.class))).thenReturn(member);
        User result = userController.createUser(new Object());
        assertNotNull(result);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_UserExists() {
        Member member = new Member();
        when(userService.findById(1L)).thenReturn(member);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(member);
        User result = userController.updateUser(1L, new Object());
        assertNotNull(result);
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userService.findById(1L)).thenReturn(null);
        User result = userController.updateUser(1L, new Object());
        assertNull(result);
        verify(userService, times(1)).findById(1L);
        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void testDeleteUser() {
        when(userService.deactivateUser(1L)).thenReturn(true);
        userController.deleteUser(1L);
        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    void testGetUserById() {
        Member member = new Member();
        when(userService.findById(1L)).thenReturn(member);
        User result = userController.getUserById(1L);
        assertNotNull(result);
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testSearchUsers() {
        List<User> users = Arrays.asList(new Member());
        when(userService.searchUsers("query")).thenReturn(users);
        List<User> result = userController.searchUsers("query");
        assertEquals(1, result.size());
        verify(userService, times(1)).searchUsers("query");
    }
}