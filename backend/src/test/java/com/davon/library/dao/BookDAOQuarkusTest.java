package com.davon.library.dao;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map;

/**
 * Quarkus test class for BookDAO implementation.
 * Demonstrates that the new DAO pattern works correctly with Quarkus framework.
 */
@QuarkusTest
@TestProfile(BookDAOQuarkusTest.TestProfileImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookDAOQuarkusTest {

    @Inject
    BookDAO bookDAO;

    public static class TestProfileImpl implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.test.disable-maven-repository", "true");
        }
    }

    @BeforeEach
    void clearData() {
        // Force clear all books before each test to ensure total isolation
        try {
            bookDAO.clearAll();
            // Double-check by verifying count is 0
            assertEquals(0, bookDAO.count(), "DAO should be empty after clearAll()");
        } catch (DAOException e) {
            fail("Failed to clear DAO: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testSaveAndFindById() throws DAOException {
        // Given
        Book book = createTestBook("Quarkus Guide", generateUniqueISBN());

        // When
        Book savedBook = bookDAO.save(book);
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());

        // Then
        assertNotNull(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Quarkus Guide", foundBook.get().getTitle());
    }

    @Test
    @Order(2)
    void testFindByISBN() throws DAOException {
        // Given
        String isbn = generateUniqueISBN();
        Book book = createTestBook("Microservices with Quarkus", isbn);
        bookDAO.save(book);

        // When
        Optional<Book> foundBook = bookDAO.findByISBN(isbn);

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(isbn, foundBook.get().getISBN());
    }

    @Test
    @Order(3)
    void testSearchBooks() throws DAOException {
        // Given - Start with empty DAO
        assertEquals(0, bookDAO.count(), "Should start with empty DAO");

        // Create unique test books
        Book book1 = createTestBook("TestQuarkusBook1_" + System.nanoTime(), generateUniqueISBN());
        Book book2 = createTestBook("TestSpringBook_" + System.nanoTime(), generateUniqueISBN());
        Book book3 = createTestBook("TestQuarkusBook2_" + System.nanoTime(), generateUniqueISBN());

        bookDAO.save(book1);
        bookDAO.save(book2);
        bookDAO.save(book3);

        // When
        List<Book> results = bookDAO.searchBooks("TestQuarkus");

        // Then - Should find exactly our 2 Quarkus test books
        assertEquals(2, results.size(), "Should find exactly 2 TestQuarkus books");
        assertTrue(results.stream().anyMatch(b -> b.getTitle().startsWith("TestQuarkusBook1_")));
        assertTrue(results.stream().anyMatch(b -> b.getTitle().startsWith("TestQuarkusBook2_")));
        assertFalse(results.stream().anyMatch(b -> b.getTitle().contains("Spring")));
    }

    @Test
    @Order(4)
    void testUpdateBook() throws DAOException {
        // Given
        Book book = createTestBook("Original Title", generateUniqueISBN());
        Book savedBook = bookDAO.save(book);

        // When
        savedBook.setTitle("Updated with Quarkus");
        Book updatedBook = bookDAO.update(savedBook);

        // Then
        assertEquals("Updated with Quarkus", updatedBook.getTitle());
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Updated with Quarkus", foundBook.get().getTitle());
    }

    @Test
    @Order(5)
    void testDeleteBook() throws DAOException {
        // Given
        Book book = createTestBook("To Be Deleted", generateUniqueISBN());
        Book savedBook = bookDAO.save(book);

        // When
        bookDAO.deleteById(savedBook.getId());

        // Then
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());
        assertFalse(foundBook.isPresent());
    }

    @Test
    @Order(6)
    void testExistsByISBN() throws DAOException {
        // Given
        String isbn = generateUniqueISBN();
        Book book = createTestBook("Existence Test", isbn);
        bookDAO.save(book);

        // When & Then
        assertTrue(bookDAO.existsByISBN(isbn));
        assertFalse(bookDAO.existsByISBN(generateUniqueISBN()));
    }

    @Test
    @Order(7)
    void testDuplicateISBNValidation() throws DAOException {
        // Given
        String isbn = generateUniqueISBN();
        Book book1 = createTestBook("First Book", isbn);
        Book book2 = createTestBook("Duplicate ISBN", isbn);

        bookDAO.save(book1);

        // When & Then
        assertThrows(DAOException.class, () -> bookDAO.save(book2),
                "Should throw DAOException for duplicate ISBN");
    }

    @Test
    @Order(8)
    void testFindAll() throws DAOException {
        // Given
        assertEquals(0, bookDAO.count(), "Should start with empty DAO");

        Book book1 = createTestBook("Book One", generateUniqueISBN());
        Book book2 = createTestBook("Book Two", generateUniqueISBN());

        bookDAO.save(book1);
        bookDAO.save(book2);

        // When
        List<Book> allBooks = bookDAO.findAll();

        // Then
        assertEquals(2, allBooks.size());
    }

    @Test
    @Order(9)
    void testCount() throws DAOException {
        // Given
        assertEquals(0, bookDAO.count(), "Should start with 0 books");

        Book book = createTestBook("Count Test", generateUniqueISBN());
        bookDAO.save(book);

        // When
        long newCount = bookDAO.count();

        // Then
        assertEquals(1, newCount);
    }

    private Book createTestBook(String title, String isbn) {
        return Book.builder()
                .title(title)
                .ISBN(isbn)
                .publicationYear(2024)
                .description("Test description for Quarkus")
                .pages(200)
                .build();
    }

    private String generateUniqueISBN() {
        // Generate a truly unique 13-digit ISBN using UUID and nanotime
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        long nanoTime = System.nanoTime();
        return "978" + uuid.substring(0, 7) + String.valueOf(nanoTime).substring(0, 3);
    }
}