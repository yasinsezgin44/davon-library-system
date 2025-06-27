package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Quarkus test for BookService.
 * Tests the service layer with the new DAO pattern.
 */
@QuarkusTest
@Transactional
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
    void testCreateBook() throws BookService.BookServiceException {
        // Given
        Book book = createTestBook("Quarkus in Action", "9780123456789");

        // When
        Book createdBook = bookService.createBook(book);

        // Then
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        assertEquals("Quarkus in Action", createdBook.getTitle());
        assertEquals("9780123456789", createdBook.getISBN());
    }

    @Test
    void testGetAllBooks() throws BookService.BookServiceException {
        // Given
        Book book1 = createTestBook("Java Fundamentals", "9781111111111");
        Book book2 = createTestBook("Microservices Guide", "9782222222222");

        bookService.createBook(book1);
        bookService.createBook(book2);

        // When
        List<Book> allBooks = bookService.getAllBooks();

        // Then
        assertTrue(allBooks.size() >= 2);
        assertTrue(allBooks.stream().anyMatch(b -> b.getTitle().equals("Java Fundamentals")));
        assertTrue(allBooks.stream().anyMatch(b -> b.getTitle().equals("Microservices Guide")));
    }

    @Test
    void testGetBookById() throws BookService.BookServiceException {
        // Given
        Book book = createTestBook("Spring Boot Guide", "9783333333333");
        Book createdBook = bookService.createBook(book);

        // When
        Book foundBook = bookService.getBookById(createdBook.getId());

        // Then
        assertNotNull(foundBook);
        assertEquals(createdBook.getId(), foundBook.getId());
        assertEquals("Spring Boot Guide", foundBook.getTitle());
    }

    @Test
    void testUpdateBook() throws BookService.BookServiceException {
        // Given
        Book book = createTestBook("Original Title", "9784444444444");
        Book createdBook = bookService.createBook(book);

        // When
        createdBook.setTitle("Updated Title");
        Book updatedBook = bookService.updateBook(createdBook.getId(), createdBook);

        // Then
        assertNotNull(updatedBook);
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals(createdBook.getId(), updatedBook.getId());
    }

    @Test
    void testDeleteBook() throws BookService.BookServiceException {
        // Given
        Book book = createTestBook("To Be Deleted", "9785555555555");
        Book createdBook = bookService.createBook(book);

        // When
        bookService.deleteBook(createdBook.getId());

        // Then
        Book deletedBook = bookService.getBookById(createdBook.getId());
        assertNull(deletedBook);
    }

    @Test
    void testSearchBooks() throws BookService.BookServiceException {
        // Given
        Book book1 = createTestBook("Advanced Quarkus", "9786666666666");
        Book book2 = createTestBook("Spring Framework", "9787777777777");
        Book book3 = createTestBook("Quarkus Microservices", "9788888888888");

        bookService.createBook(book1);
        bookService.createBook(book2);
        bookService.createBook(book3);

        // When
        List<Book> results = bookService.searchBooks("Quarkus");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Advanced Quarkus")));
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Quarkus Microservices")));
    }

    @Test
    void testGetBookByISBN() throws BookService.BookServiceException {
        // Given
        String isbn = "9789999999999";
        Book book = createTestBook("ISBN Test Book", isbn);
        bookService.createBook(book);

        // When
        Book foundBook = bookService.getBookByISBN(isbn);

        // Then
        assertNotNull(foundBook);
        assertEquals(isbn, foundBook.getISBN());
        assertEquals("ISBN Test Book", foundBook.getTitle());
    }

    @Test
    void testIsISBNExists() throws BookService.BookServiceException {
        // Given
        String isbn = "9781010101010";
        Book book = createTestBook("Existence Test", isbn);
        bookService.createBook(book);

        // When & Then
        assertTrue(bookService.isISBNExists(isbn));
        assertFalse(bookService.isISBNExists("9999999999999"));
    }

    @Test
    void testCreateBookWithNullTitle() {
        // Given
        Book book = Book.builder()
                .title(null)
                .ISBN("9781111111111")
                .publicationYear(2024)
                .build();

        // When & Then
        assertThrows(BookService.BookServiceException.class, () -> {
            bookService.createBook(book);
        });
    }

    @Test
    void testCreateBookWithDuplicateISBN() throws BookService.BookServiceException {
        // Given - Use timestamp to make ISBN unique for this test run
        String isbn = "978" + String.valueOf(System.currentTimeMillis()).substring(3, 12) + "1";
        Book book1 = createTestBook("First Book", isbn);
        Book book2 = createTestBook("Second Book", isbn);

        bookService.createBook(book1);

        // When & Then
        assertThrows(BookService.BookServiceException.class, () -> {
            bookService.createBook(book2);
        });
    }

    private Book createTestBook(String title, String isbn) {
        Author author = Author.builder()
                .name("Test Author")
                .build();

        Set<Author> authors = new HashSet<>();
        authors.add(author);

        return Book.builder()
                .title(title)
                .ISBN(isbn)
                .publicationYear(2024)
                .description("Test description")
                .pages(300)
                .authors(authors)
                .build();
    }
}