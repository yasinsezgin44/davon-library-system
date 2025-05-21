package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.event.UserStatusListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private TestUserRepository userRepository;
    private Member testMember;
    private Librarian testLibrarian;
    private TestUserStatusListener testListener;

    @BeforeEach
    void setUp() {
        userRepository = new TestUserRepository();
        userService = new UserService(userRepository);

        // Create test users
        testMember = Member.builder()
                .id(1L)
                .username("testmember")
                .passwordHash("hashed_password")
                .fullName("Test Member")
                .email("member@test.com")
                .phoneNumber("123-456-7890")
                .active(true)
                .status("ACTIVE")
                .lastLogin(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(1))
                .address("123 Test St")
                .build();

        testLibrarian = Librarian.builder()
                .id(2L)
                .username("testlibrarian")
                .passwordHash("hashed_password")
                .fullName("Test Librarian")
                .email("librarian@test.com")
                .phoneNumber("987-654-3210")
                .active(true)
                .status("ACTIVE")
                .lastLogin(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .employeeId("EMP001")
                .employmentDate(LocalDate.now().minusYears(1))
                .build();

        // Add test users
        userService.createUser(testMember);
        userService.createUser(testLibrarian);

        // Create and register a listener
        testListener = new TestUserStatusListener();
        userService.addStatusListener(testListener);
    }

    @Test
    void testCreateUser() {
        Admin newAdmin = Admin.builder()
                .id(3L)
                .username("testadmin")
                .passwordHash("admin_password")
                .fullName("Test Admin")
                .email("admin@test.com")
                .active(true)
                .build();

        User createdUser = userService.createUser(newAdmin);
        assertNotNull(createdUser);
        assertEquals("testadmin", createdUser.getUsername());

        // Verify it was added to the collection
        Set<User> allUsers = userService.getUsers();
        assertEquals(3, allUsers.size());
    }

    @Test
    void testUpdateUser() {
        Member updatedMember = Member.builder()
                .id(1L)
                .username("testmember")
                .passwordHash("hashed_password")
                .fullName("Updated Member Name")
                .email("updated.member@test.com")
                .phoneNumber("123-456-7890")
                .active(true)
                .build();

        User result = userService.updateUser(1L, updatedMember);
        assertNotNull(result);
        assertEquals("Updated Member Name", result.getFullName());
        assertEquals("updated.member@test.com", result.getEmail());

        // Verify the user was updated in the collection
        User foundUser = userService.findById(1L);
        assertEquals("Updated Member Name", foundUser.getFullName());
    }

    @Test
    void testDeactivateAndActivateUser() {
        // Test deactivation
        boolean deactivated = userService.deactivateUser(1L);
        assertTrue(deactivated);

        User foundUser = userService.findById(1L);
        assertFalse(foundUser.isActive());

        // Test failed authentication when inactive
        User authenticatedUser = userService.authenticateUser("testmember", "hashed_password");
        assertNull(authenticatedUser);

        // Test activation
        boolean activated = userService.activateUser(1L);
        assertTrue(activated);

        foundUser = userService.findById(1L);
        assertTrue(foundUser.isActive());

        // Test successful authentication after activation
        authenticatedUser = userService.authenticateUser("testmember", "hashed_password");
        assertNotNull(authenticatedUser);
        assertEquals("testmember", authenticatedUser.getUsername());
    }

    @Test
    void testSearchUsers() {
        // Search by username
        List<User> userResults = userService.searchUsers("librarian");
        assertEquals(1, userResults.size());
        assertEquals("testlibrarian", userResults.get(0).getUsername());

        // Search by full name
        List<User> nameResults = userService.searchUsers("Test");
        assertEquals(2, nameResults.size());

        // Search by email
        List<User> emailResults = userService.searchUsers("member@test");
        assertEquals(1, emailResults.size());
        assertEquals("testmember", emailResults.get(0).getUsername());

        // Search with no matches
        List<User> noMatches = userService.searchUsers("xyz123");
        assertEquals(0, noMatches.size());
    }

    @Test
    void testAuthenticateUser() {
        // Successful authentication
        User authenticatedUser = userService.authenticateUser("testmember", "hashed_password");
        assertNotNull(authenticatedUser);
        assertEquals("testmember", authenticatedUser.getUsername());

        // Failed authentication - wrong password
        User wrongPassword = userService.authenticateUser("testmember", "wrong_password");
        assertNull(wrongPassword);

        // Failed authentication - wrong username
        User wrongUsername = userService.authenticateUser("wrongusername", "hashed_password");
        assertNull(wrongUsername);
    }

    @Test
    void testFindUserByEmail() {
        User foundUser = userService.findUserByEmail("member@test.com");
        assertNotNull(foundUser);
        assertEquals("testmember", foundUser.getUsername());

        // Case insensitive email search
        User caseInsensitive = userService.findUserByEmail("MEMBER@test.com");
        assertNotNull(caseInsensitive);
        assertEquals("testmember", caseInsensitive.getUsername());

        // Not found email
        User notFound = userService.findUserByEmail("notfound@test.com");
        assertNull(notFound);
    }

    @Test
    void testUpdateProfile() {
        UserService.UserProfile profile = new UserService.UserProfile(
                "Updated Member",
                "updated@test.com",
                "555-555-5555",
                "456 New Address");

        boolean updated = userService.updateProfile(1L, profile);
        assertTrue(updated);

        User foundUser = userService.findById(1L);
        assertEquals("Updated Member", foundUser.getFullName());
        assertEquals("updated@test.com", foundUser.getEmail());
        assertEquals("555-555-5555", foundUser.getPhoneNumber());

        // Check address update (specific to Member)
        Member memberUser = (Member) foundUser;
        assertEquals("456 New Address", memberUser.getAddress());

        // Test update for non-existent user
        boolean nonExistent = userService.updateProfile(999L, profile);
        assertFalse(nonExistent);
    }

    @Test
    void testUpdateUserStatus() {
        // Verify initial status
        assertEquals("ACTIVE", testMember.getStatus());

        // Update status
        boolean updated = userService.updateUserStatus(1L, "SUSPENDED");
        assertTrue(updated);

        // Verify status update
        User foundUser = userService.findById(1L);
        assertEquals("SUSPENDED", foundUser.getStatus());

        // Verify listener was notified
        assertEquals(1, testListener.getStatusChangeCount());
        assertEquals(testMember, testListener.getLastUser());
        assertEquals("ACTIVE", testListener.getLastOldStatus());
        assertEquals("SUSPENDED", testListener.getLastNewStatus());

        // Test update for non-existent user
        boolean nonExistent = userService.updateUserStatus(999L, "SUSPENDED");
        assertFalse(nonExistent);
    }

    // Test implementations

    static class TestUserRepository implements UserRepository {
        private Set<User> users = new HashSet<>();

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

    static class TestUserStatusListener implements UserStatusListener {
        private int statusChangeCount = 0;
        private User lastUser;
        private String lastOldStatus;
        private String lastNewStatus;

        @Override
        public void onUserStatusChange(User user, String oldStatus, String newStatus) {
            statusChangeCount++;
            lastUser = user;
            lastOldStatus = oldStatus;
            lastNewStatus = newStatus;
        }

        public int getStatusChangeCount() {
            return statusChangeCount;
        }

        public User getLastUser() {
            return lastUser;
        }

        public String getLastOldStatus() {
            return lastOldStatus;
        }

        public String getLastNewStatus() {
            return lastNewStatus;
        }
    }
}