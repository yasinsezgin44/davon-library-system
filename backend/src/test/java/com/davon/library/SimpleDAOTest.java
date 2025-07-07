package com.davon.library;

import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimpleDAOTest {

    @Inject
    BookDAO bookDAO;

    @Inject
    DatabaseConnectionManager connectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        // Clean up test data before each test
        cleanUpTestData();
    }

    private void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up books table for testing
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE isbn LIKE 'TEST%'")) {
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        // Test basic database connectivity
        try (Connection conn = connectionManager.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            System.out.println("✅ Database connection successful");
        }
    }

    @Test
    void testSimpleBookSave() throws DAOException {
        // Create a simple book with all required fields
        Book book = Book.builder()
                .title("Test Book")
                .ISBN("TEST123456789")
                .publicationYear(2024)
                .description("A test book")
                .pages(100)
                .build();

        // Save the book
        Book savedBook = bookDAO.save(book);

        // Verify the save
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("TEST123456789", savedBook.getISBN());
        assertEquals(2024, savedBook.getPublicationYear());

        System.out.println("✅ Book saved successfully with ID: " + savedBook.getId());

        // Test retrieval
        Optional<Book> retrievedBook = bookDAO.findById(savedBook.getId());
        assertTrue(retrievedBook.isPresent());
        assertEquals("Test Book", retrievedBook.get().getTitle());

        System.out.println("✅ Book retrieved successfully");
    }

    @Test
    void testBookCount() {
        // Test count operation
        long count = bookDAO.count();
        assertTrue(count >= 0);
        System.out.println("✅ Book count: " + count);
    }

    @Test
    void testFindByISBN() throws DAOException {
        // Create and save a book
        Book book = Book.builder()
                .title("ISBN Test Book")
                .ISBN("TEST987654321")
                .publicationYear(2024)
                .description("Testing ISBN search")
                .pages(150)
                .build();

        Book savedBook = bookDAO.save(book);

        // Find by ISBN
        Optional<Book> foundBook = bookDAO.findByISBN("TEST987654321");
        assertTrue(foundBook.isPresent());
        assertEquals("ISBN Test Book", foundBook.get().getTitle());

        System.out.println("✅ Find by ISBN working");
    }

    @Test
    void testBookExistence() throws DAOException {
        // Create and save a book
        Book book = Book.builder()
                .title("Existence Test Book")
                .ISBN("TESTEXIST123")
                .publicationYear(2024)
                .description("Testing existence check")
                .pages(200)
                .build();

        Book savedBook = bookDAO.save(book);

        // Test existence
        assertTrue(bookDAO.existsById(savedBook.getId()));
        assertFalse(bookDAO.existsById(999999L));
        assertTrue(bookDAO.existsByISBN("TESTEXIST123"));
        assertFalse(bookDAO.existsByISBN("NONEXISTENT"));

        System.out.println("✅ Existence checks working");
    }
}