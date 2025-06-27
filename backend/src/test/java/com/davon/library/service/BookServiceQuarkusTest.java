package com.davon.library.service;

import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Book;
import com.davon.library.service.BookService.BookServiceException;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Quarkus test class for BookService implementation.
 * Tests the service layer integration with DAO pattern.
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BookServiceQuarkusTest {

    @Inject
    BookService bookService;

    @Inject
    BookDAO bookDAO;

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
    void testCreateBook() throws BookServiceException {
        // Given
        Book book = createTestBook("Service Test Book", generateUniqueISBN());

        // When
        Book createdBook = bookService.createBook(book);

        // Then
        assertNotNull(createdBook.getId());
        assertEquals("Service Test Book", createdBook.getTitle());
    }

    @Test
    void testGetBookById() throws BookServiceException {
        // Given
        Book book = createTestBook("Get By ID Test", generateUniqueISBN());
        Book savedBook = bookService.createBook(book);

        // When
        Book foundBook = bookService.getBookById(savedBook.getId());

        // Then
        assertNotNull(foundBook);
        assertEquals(savedBook.getId(), foundBook.getId());
        assertEquals("Get By ID Test", foundBook.getTitle());
    }

    @Test
    void testGetBookByISBN() throws BookServiceException {
        // Given
        String isbn = generateUniqueISBN();
        Book book = createTestBook("ISBN Test Book", isbn);
        bookService.createBook(book);

        // When
        Book foundBook = bookService.getBookByISBN(isbn);

        // Then
        assertNotNull(foundBook);
        assertEquals(isbn, foundBook.getISBN());
    }

    @Test
    void testSearchBooks() throws BookServiceException {
        // Given
        Book book1 = createTestBook("Java Programming", generateUniqueISBN());
        Book book2 = createTestBook("Python Guide", generateUniqueISBN());
        Book book3 = createTestBook("Advanced Java", generateUniqueISBN());

        bookService.createBook(book1);
        bookService.createBook(book2);
        bookService.createBook(book3);

        // When
        List<Book> results = bookService.searchBooks("Java");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Java Programming")));
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Advanced Java")));
    }

    @Test
    void testUpdateBook() throws BookServiceException {
        // Given
        Book book = createTestBook("Original Service Title", generateUniqueISBN());
        Book savedBook = bookService.createBook(book);

        // When
        savedBook.setTitle("Updated Service Title");
        Book updatedBook = bookService.updateBook(savedBook.getId(), savedBook);

        // Then
        assertEquals("Updated Service Title", updatedBook.getTitle());
    }

    @Test
    void testDeleteBook() throws BookServiceException {
        // Given
        Book book = createTestBook("To Be Deleted by Service", generateUniqueISBN());
        Book savedBook = bookService.createBook(book);

        // When
        bookService.deleteBook(savedBook.getId());

        // Then
        Book deletedBook = bookService.getBookById(savedBook.getId());
        assertNull(deletedBook, "Book should be null after deletion");
    }

    @Test
    void testGetAllBooks() throws BookServiceException {
        // Given
        int initialCount = bookService.getAllBooks().size();

        Book book1 = createTestBook("All Books Test 1", generateUniqueISBN());
        Book book2 = createTestBook("All Books Test 2", generateUniqueISBN());

        bookService.createBook(book1);
        bookService.createBook(book2);

        // When
        List<Book> allBooks = bookService.getAllBooks();

        // Then
        assertEquals(initialCount + 2, allBooks.size());
    }

    @Test
    void testDuplicateISBNHandling() throws BookServiceException {
        // Given
        String isbn = generateUniqueISBN();
        Book book1 = createTestBook("First Book", isbn);
        Book book2 = createTestBook("Duplicate ISBN Book", isbn);

        bookService.createBook(book1);

        // When & Then
        assertThrows(BookServiceException.class, () -> bookService.createBook(book2),
                "Should throw BookServiceException for duplicate ISBN");
    }

    @Test
    void testValidation() {
        // When & Then - Test null book
        assertThrows(BookServiceException.class, () -> bookService.createBook(null),
                "Should throw BookServiceException for null book");

        // Test book with null title
        Book invalidBook = Book.builder()
                .ISBN(generateUniqueISBN())
                .publicationYear(2024)
                .build(); // Missing title

        assertThrows(BookServiceException.class, () -> bookService.createBook(invalidBook),
                "Should throw BookServiceException for book with null title");
    }

    private Book createTestBook(String title, String isbn) {
        return Book.builder()
                .title(title)
                .ISBN(isbn)
                .publicationYear(2024)
                .description("Test description for service layer")
                .pages(250)
                .build();
    }

    private String generateUniqueISBN() {
        // Generate a truly unique 13-digit ISBN using UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return "978" + uuid.substring(0, 10); // Take first 10 chars after 978 prefix
    }
}