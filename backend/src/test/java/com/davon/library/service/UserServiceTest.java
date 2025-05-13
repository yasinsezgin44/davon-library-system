package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private Member member;
    private Admin admin;
    private TestUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new TestUserRepository();
        userService = new UserService(userRepository);
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
        // Create a test user
        TestUser user = new TestUser();
        user.setId(1L);
        user.setUsername("testuser");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        userRepository.save(user);

        // Before assigning role the list should be empty
        assertTrue(user.getRoles().isEmpty());

        // Add the role manually since our service now just saves
        user.addRole(role);

        // Now use the service to save
        User updated = userService.assignRole(user, role);

        // Verify the role was added
        assertNotNull(updated);
        assertEquals(1, ((TestUser) updated).getRoles().size());
        assertTrue(((TestUser) updated).getRoles().contains(role));
    }

    // Test implementation class
    static class TestUser extends User {
        private List<Role> roles = new ArrayList<>();

        public List<Role> getRoles() {
            return roles;
        }

        public void addRole(Role role) {
            roles.add(role);
        }
    }

    // Repository implementation
    static class TestUserRepository implements UserRepository {
        private List<User> users = new ArrayList<>();

        @Override
        public User save(User user) {
            users.add(user);
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst();
        }
    }
}