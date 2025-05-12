package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private Member member;
    private Admin admin;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        member = Member.builder().id(1L).username("member1").fullName("Member One").email("member1@example.com")
                .passwordHash("pass").active(true).build();
        admin = Admin.builder().id(2L).username("admin1").fullName("Admin One").email("admin1@example.com")
                .passwordHash("adminpass").active(true).permissions(Set.of()).build();
    }

    @Test
    void testCreateAndUpdateUser() {
        userService.createUser(member);
        assertEquals(1, userService.getUsers().size());
        Member updated = Member.builder().id(1L).username("member1").fullName("Member One Updated")
                .email("member1@example.com").passwordHash("pass").active(true).build();
        userService.updateUser(1L, updated);
        assertEquals("Member One Updated",
                userService.getUsers().stream().filter(u -> u.getId().equals(1L)).findFirst().get().getFullName());
    }

    @Test
    void testDeactivateAndActivateUser() {
        userService.createUser(member);
        assertTrue(userService.deactivateUser(1L));
        assertFalse(member.isActive());
        assertTrue(userService.activateUser(1L));
        assertTrue(member.isActive());
    }

    @Test
    void testSearchUsers() {
        userService.createUser(member);
        List<User> results = userService.searchUsers("member1");
        assertFalse(results.isEmpty());
        assertEquals(member, results.get(0));
    }

    @Test
    void testAuthenticateUser() {
        userService.createUser(member);
        assertNotNull(userService.authenticateUser("member1", "pass"));
        assertNull(userService.authenticateUser("member1", "wrongpass"));
    }

    @Test
    void testAssignRole() {
        userService.createUser(admin);
        assertTrue(userService.assignRole(2L, "LIBRARIAN"));
        assertTrue(admin.getPermissions().contains("LIBRARIAN"));
    }
}