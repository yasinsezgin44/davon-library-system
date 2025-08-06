package com.davon.library.service;

import com.davon.library.model.Role;
import com.davon.library.model.User;
import com.davon.library.repository.RoleRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class AdminServiceTest {

    @Inject
    AdminService adminService;

    @InjectMock
    UserService userService;

    @InjectMock
    RoleRepository roleRepository;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
    }

    @Test
    void testCreateUserWithRole() {
        when(roleRepository.find("name", "ADMIN")).thenReturn(io.quarkus.panache.common.Page.of(0, 1), java.util.List.of(role));
        when(userService.createUser(any(User.class))).thenReturn(user);

        adminService.createUserWithRole(new User(), "ADMIN");

        Mockito.verify(userService).createUser(any(User.class));
    }

    @Test
    void testDeleteUser() {
        adminService.deleteUser(1L);
        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    void testAssignRoleToUser() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.find("name", "ADMIN")).thenReturn(io.quarkus.panache.common.Page.of(0, 1), java.util.List.of(role));

        adminService.assignRoleToUser(1L, "ADMIN");

        Mockito.verify(userService).getUserById(1L);
    }
}
