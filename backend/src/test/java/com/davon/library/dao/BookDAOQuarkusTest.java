package com.davon.library.dao;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Random;

/**
 * Quarkus test class for BookDAO implementation.
 * Demonstrates that the new DAO pattern works correctly with Quarkus framework.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.Random.class)
class BookDAOQuarkusTest {

    @Inject
    BookDAO bookDAO;

    private static final Random random = new Random();

    @BeforeEach
    void clearData() {
        // Clear all books before each test to ensure isolation
        try {
            bookDAO.clearAll();
        } catch (DAOException e) {
            // Ignore - clear may fail if not supported
        }
    }

    @Test
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
    void testSearchBooks() throws DAOException {
        // Given - Use unique ISBNs to avoid conflicts
        Book book1 = createTestBook("Quarkus Programming", generateUniqueISBN());
        Book book2 = createTestBook("Spring Boot Guide", generateUniqueISBN());
        Book book3 = createTestBook("Advanced Quarkus", generateUniqueISBN());

        bookDAO.save(book1);
        bookDAO.save(book2);
        bookDAO.save(book3);

        // When
        List<Book> results = bookDAO.searchBooks("Quarkus");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Quarkus Programming")));
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Advanced Quarkus")));
    }

    @Test
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
    void testFindAll() throws DAOException {
        // Given
        int initialCount = bookDAO.findAll().size();

        Book book1 = createTestBook("Book One", generateUniqueISBN());
        Book book2 = createTestBook("Book Two", generateUniqueISBN());

        bookDAO.save(book1);
        bookDAO.save(book2);

        // When
        List<Book> allBooks = bookDAO.findAll();

        // Then
        assertEquals(initialCount + 2, allBooks.size());
    }

    @Test
    void testCount() throws DAOException {
        // Given
        long initialCount = bookDAO.count();

        Book book = createTestBook("Count Test", generateUniqueISBN());
        bookDAO.save(book);

        // When
        long newCount = bookDAO.count();

        // Then
        assertEquals(initialCount + 1, newCount);
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
        // Generate a unique 13-digit ISBN using timestamp and random number
        long timestamp = System.currentTimeMillis();
        int randomNum = random.nextInt(1000);
        return "978" + String.valueOf(timestamp).substring(3, 12) + (randomNum % 10);
    }
}