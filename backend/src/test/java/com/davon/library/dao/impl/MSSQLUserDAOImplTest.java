package com.davon.library.dao.impl;

import com.davon.library.dao.UserDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Member;
import com.davon.library.model.User;
import com.davon.library.model.UserStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MSSQLUserDAOImplTest {

    @Inject
    UserDAO userDAO;

    @Inject
    DatabaseConnectionManager connectionManager;

    private Member testMember;

    @BeforeEach
    void setUp() throws SQLException {
        // Clean up test data
        cleanUpTestData();

        // Set up test member (concrete User implementation)
        testMember = Member.builder()
                .username("testuser123")
                .passwordHash("hashedpassword123")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("123-456-7890")
                .active(true)
                .status(UserStatus.ACTIVE)
                .lastLogin(LocalDate.now().minusDays(1))
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .address("123 Test Street, Test City")
                .fineBalance(0.0)
                .build();
    }

    private void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up users table for testing
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username LIKE 'testuser%'")) {
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testSaveUser() throws DAOException {
        // Act
        User savedUser = userDAO.save(testMember);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testMember.getUsername(), savedUser.getUsername());
        assertEquals(testMember.getPasswordHash(), savedUser.getPasswordHash());
        assertEquals(testMember.getFullName(), savedUser.getFullName());
        assertEquals(testMember.getEmail(), savedUser.getEmail());
        assertEquals(testMember.getPhoneNumber(), savedUser.getPhoneNumber());
        assertEquals(testMember.isActive(), savedUser.isActive());
        assertEquals(testMember.getStatus(), savedUser.getStatus());
        assertEquals(testMember.getLastLogin(), savedUser.getLastLogin());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getLastModifiedAt());
    }

    @Test
    void testFindById() throws DAOException {
        // Arrange
        User savedUser = userDAO.save(testMember);

        // Act
        Optional<User> foundUser = userDAO.findById(savedUser.getId());

        // Assert
        assertTrue(foundUser.isPresent());
        User user = foundUser.get();
        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getUsername(), user.getUsername());
        assertEquals(savedUser.getPasswordHash(), user.getPasswordHash());
        assertEquals(savedUser.getFullName(), user.getFullName());
        assertEquals(savedUser.getEmail(), user.getEmail());
        assertEquals(savedUser.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(savedUser.isActive(), user.isActive());
        assertEquals(savedUser.getStatus(), user.getStatus());
        assertEquals(savedUser.getLastLogin(), user.getLastLogin());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Optional<User> foundUser = userDAO.findById(999999L);

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername() throws DAOException {
        // Arrange
        User savedUser = userDAO.save(testMember);

        // Act
        Optional<User> foundUser = userDAO.findByUsername(testMember.getUsername());

        // Assert
        assertTrue(foundUser.isPresent());
        User user = foundUser.get();
        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getUsername(), user.getUsername());
        assertEquals(savedUser.getFullName(), user.getFullName());
        assertEquals(savedUser.getEmail(), user.getEmail());
    }

    @Test
    void testFindByUsernameNotFound() {
        // Act
        Optional<User> foundUser = userDAO.findByUsername("nonexistentuser");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testSaveUserWithNullValues() throws DAOException {
        // Arrange
        Member memberWithNulls = Member.builder()
                .username("testnull")
                .passwordHash("hashedpassword")
                .fullName("Test Null User")
                .email("testnull@example.com")
                .active(true)
                .status(UserStatus.ACTIVE)
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(1))
                .build();

        // Act
        User savedUser = userDAO.save(memberWithNulls);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(memberWithNulls.getUsername(), savedUser.getUsername());
        assertNull(savedUser.getPhoneNumber());
        assertNull(savedUser.getLastLogin());
    }

    @Test
    void testMemberSpecificBehavior() {
        // Test Member-specific methods
        testMember.setFineBalance(25.00);

        // Test fine payment
        assertTrue(testMember.payFines(15.00));
        assertEquals(10.00, testMember.getFineBalance(), 0.01);

        // Test invalid payment amount
        assertFalse(testMember.payFines(15.00)); // More than balance
        assertFalse(testMember.payFines(-5.00)); // Negative amount
        assertEquals(10.00, testMember.getFineBalance(), 0.01);

        // Test add fine
        testMember.addFine(5.00);
        assertEquals(15.00, testMember.getFineBalance(), 0.01);

        // Test add negative fine (should not change balance)
        testMember.addFine(-2.00);
        assertEquals(15.00, testMember.getFineBalance(), 0.01);
    }

    @Test
    void testMembershipRenewal() {
        // Arrange
        LocalDate originalEndDate = testMember.getMembershipEndDate();

        // Act
        boolean renewed = testMember.renewMembership(6);

        // Assert
        assertTrue(renewed);
        assertEquals(originalEndDate.plusMonths(6), testMember.getMembershipEndDate());
    }

    @Test
    void testBorrowBooks() {
        // Arrange
        List<Long> bookIds = List.of(1L, 2L, 3L);

        // Act
        boolean result = testMember.borrowBooks(bookIds);

        // Assert
        assertTrue(result); // Default implementation returns true
    }

    @Test
    void testUserIsAdmin() {
        // Act & Assert
        assertFalse(testMember.isAdmin()); // Member is not admin
    }

    @Test
    void testUserStatusValues() {
        // Test setting different status values
        testMember.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE, testMember.getStatus());

        testMember.setStatus(UserStatus.INACTIVE);
        assertEquals(UserStatus.INACTIVE, testMember.getStatus());

        testMember.setStatus(UserStatus.SUSPENDED);
        assertEquals(UserStatus.SUSPENDED, testMember.getStatus());
    }

    @Test
    void testActiveFlag() {
        // Test active flag behavior
        testMember.setActive(true);
        assertTrue(testMember.isActive());

        testMember.setActive(false);
        assertFalse(testMember.isActive());
    }

    @Test
    void testUserEquality() throws DAOException {
        // Arrange
        User savedUser1 = userDAO.save(testMember);

        Member testMember2 = Member.builder()
                .username("testuser456")
                .passwordHash("hashedpassword456")
                .fullName("Test User 2")
                .email("test2@example.com")
                .phoneNumber("987-654-3210")
                .active(true)
                .status(UserStatus.ACTIVE)
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(1))
                .build();

        User savedUser2 = userDAO.save(testMember2);

        // Act & Assert
        assertEquals(savedUser1, savedUser1); // Same object
        assertNotEquals(savedUser1, savedUser2); // Different users
        assertNotEquals(savedUser1, null); // Null comparison
    }

    @Test
    void testUserToString() {
        // Act
        String userString = testMember.toString();

        // Assert
        assertNotNull(userString);
        assertTrue(userString.contains("Member"));
        assertTrue(userString.contains(testMember.getUsername()));
        assertTrue(userString.contains(testMember.getFullName()));
        // Should exclude loans, reservations, and fines due to @ToString exclude
        assertFalse(userString.contains("loans"));
        assertFalse(userString.contains("reservations"));
        assertFalse(userString.contains("fines"));
    }

    @Test
    void testDefaultBuilderValues() {
        // Arrange
        Member memberWithDefaults = Member.builder()
                .username("testdefaults")
                .fullName("Test Defaults")
                .build();

        // Assert
        assertNotNull(memberWithDefaults.getLoans());
        assertNotNull(memberWithDefaults.getReservations());
        assertNotNull(memberWithDefaults.getFines());
        assertEquals(0.0, memberWithDefaults.getFineBalance(), 0.01);
        assertTrue(memberWithDefaults.getLoans().isEmpty());
        assertTrue(memberWithDefaults.getReservations().isEmpty());
        assertTrue(memberWithDefaults.getFines().isEmpty());
    }

    // Test the unimplemented methods to ensure they behave as expected
    @Test
    void testUnimplementedMethods() {
        // These methods are not implemented in the current DAO, test their current
        // behavior

        // update method throws UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> userDAO.update(testMember));

        // Other methods return default values
        assertTrue(userDAO.findAll().isEmpty());
        assertFalse(userDAO.existsById(1L));
        assertEquals(0, userDAO.count());
        assertFalse(userDAO.findByEmail("test@example.com").isPresent());
        assertTrue(userDAO.findByRole(null).isEmpty());
        assertTrue(userDAO.findActiveUsers().isEmpty());
        assertTrue(userDAO.findInactiveUsers().isEmpty());
        assertTrue(userDAO.searchUsers("search").isEmpty());
        assertTrue(userDAO.findByStatus("ACTIVE").isEmpty());
        assertFalse(userDAO.existsByUsername("testuser"));
        assertFalse(userDAO.existsByEmail("test@example.com"));
        assertTrue(userDAO.findByCreatedDateBetween(null, null).isEmpty());
        assertEquals(0, userDAO.countByRole(null));
        assertEquals(0, userDAO.countActiveUsers());
    }

    @Test
    void testSaveUserWithDuplicateUsername() throws DAOException {
        // Arrange
        userDAO.save(testMember);

        Member duplicateUser = Member.builder()
                .username(testMember.getUsername()) // Same username
                .passwordHash("differenthash")
                .fullName("Different User")
                .email("different@example.com")
                .active(true)
                .status(UserStatus.ACTIVE)
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(1))
                .build();

        // Act & Assert
        // Should throw DAOException due to unique constraint on username
        assertThrows(DAOException.class, () -> userDAO.save(duplicateUser));
    }
}