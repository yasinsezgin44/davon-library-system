package com.davon.library.controller;

import com.davon.library.model.Member;
import com.davon.library.model.User;
import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserControllerTest {

    @Inject
    UserController userController;

    @InjectMock
    UserService userService;

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

        Response response = userController.createUser(member);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        User result = (User) response.getEntity();
        assertNotNull(result);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_BadRequest() {
        Member member = new Member();
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Invalid user"));

        Response response = userController.createUser(member);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_UserExists() {
        Member member = new Member();
        when(userService.findById(1L)).thenReturn(member);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(member);

        Response response = userController.updateUser(1L, member);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        User result = (User) response.getEntity();
        assertNotNull(result);
        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        Member member = new Member();
        when(userService.findById(1L)).thenReturn(null);

        Response response = userController.updateUser(1L, member);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userService, times(1)).findById(1L);
        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void testDeleteUser() {
        when(userService.deactivateUser(1L)).thenReturn(true);

        Response response = userController.deleteUser(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new RuntimeException("User not found")).when(userService).deactivateUser(1L);

        Response response = userController.deleteUser(1L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    void testGetUserById() {
        Member member = new Member();
        when(userService.findById(1L)).thenReturn(member);

        Response response = userController.getUserById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        User result = (User) response.getEntity();
        assertNotNull(result);
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.findById(1L)).thenReturn(null);

        Response response = userController.getUserById(1L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
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