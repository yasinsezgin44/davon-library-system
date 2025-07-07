package com.davon.library;

import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Base class for DAO tests that provides common setup and cleanup
 * functionality.
 */
@QuarkusTest
public abstract class BaseDAOTest {

    @Inject
    protected DatabaseConnectionManager connectionManager;

    protected Book testBook;
    protected Member testMember;
    protected BookCopy testBookCopy;
    protected Loan testLoan;
    protected Fine testFine;

    @BeforeEach
    protected void baseSetUp() throws SQLException {
        cleanUpTestData();
        createTestEntities();
    }

    @AfterEach
    protected void baseTearDown() throws SQLException {
        cleanUpTestData();
    }

    protected void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up in order due to foreign key constraints

            // Clean up fines
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM fines WHERE member_id = 1")) {
                stmt.executeUpdate();
            }

            // Clean up loans
            try (PreparedStatement stmt = conn
                    .prepareStatement("DELETE FROM loans WHERE member_id = 1 OR book_copy_id = 1")) {
                stmt.executeUpdate();
            }

            // Clean up book_copies
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM book_copies WHERE book_id = 1")) {
                stmt.executeUpdate();
            }

            // Clean up book_copies first
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM book_copies WHERE book_id IN (SELECT id FROM books WHERE isbn LIKE 'TEST%' OR isbn LIKE '978-0123%' OR isbn LIKE '978-0111%' OR isbn LIKE '978-0222%' OR isbn LIKE '978-0333%')")) {
                stmt.executeUpdate();
            }

            // Clean up books with test ISBNs
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM books WHERE isbn LIKE 'TEST%' OR isbn LIKE '978-0123%' OR isbn LIKE '978-0111%' OR isbn LIKE '978-0222%' OR isbn LIKE '978-0333%'")) {
                stmt.executeUpdate();
            }

            // Clean up test users (but preserve the default user with id=1)
            try (PreparedStatement stmt = conn
                    .prepareStatement("DELETE FROM users WHERE username LIKE 'test%' AND id != 1")) {
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            // Log but don't fail - test database might not be ready
            System.out.println("Warning: Could not clean up test data: " + e.getMessage());
        }
    }

    protected void createTestEntities() {
        // Create test book with all required fields and unique ISBN
        String uniqueISBN = "TEST" + System.currentTimeMillis();
        testBook = Book.builder()
                .title("Test Book")
                .ISBN(uniqueISBN)
                .publicationYear(2024)
                .description("A test book for DAO testing")
                .pages(250)
                .build();

        // Create test member (using existing member with id=1 if available)
        testMember = Member.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .active(true)
                .status(UserStatus.ACTIVE)
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .fineBalance(0.0)
                .build();

        // Create test book copy
        testBookCopy = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Good")
                .status(BookCopy.CopyStatus.AVAILABLE)
                .location("A1-01")
                .build();

        // Create test loan
        testLoan = Loan.builder()
                .member(testMember)
                .bookCopy(testBookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();

        // Create test fine
        testFine = Fine.builder()
                .member(testMember)
                .amount(15.00)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now().minusDays(7))
                .dueDate(LocalDate.now().plusDays(30))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    /**
     * Utility method to check if database is available
     */
    protected boolean isDatabaseAvailable() {
        try (Connection conn = connectionManager.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Utility method to skip test if database is not available
     */
    protected void assumeDatabaseAvailable() {
        org.junit.jupiter.api.Assumptions.assumeTrue(isDatabaseAvailable(), "Database is not available for testing");
    }
}