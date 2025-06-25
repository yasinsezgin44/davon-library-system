package com.davon.library.service;

import com.davon.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() throws Exception {
        bookService = new BookService();

        // Use reflection to inject the mock repository
        Field repositoryField = BookService.class.getDeclaredField("bookRepository");
        repositoryField.setAccessible(true);
        repositoryField.set(bookService, bookRepository);

        // Create a test book
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .description("A test book for unit testing")
                .pages(200)
                .build();

        // Mock repository save method
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testCreateBook() {
        Book createdBook = bookService.createBook(testBook);

        assertNotNull(createdBook);
        assertEquals("Test Book", createdBook.getTitle());
        assertEquals("1234567890", createdBook.getISBN());

        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void testGetAllBooks() {
        // The service uses an internal Set, so we need to add books first
        bookService.createBook(testBook);

        List<Book> books = bookService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void testGetBookById() {
        // Add book to internal storage
        bookService.createBook(testBook);

        Book foundBook = bookService.getBookById(1L);
        assertNotNull(foundBook);
        assertEquals("Test Book", foundBook.getTitle());

        // Non-existent book
        Book notFoundBook = bookService.getBookById(999L);
        assertNull(notFoundBook);
    }

    @Test
    void testUpdateBook() {
        // Add original book
        bookService.createBook(testBook);

        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .build();

        Book result = bookService.updateBook(1L, updatedBook);
        assertNotNull(result);
        assertEquals("Updated Test Book", result.getTitle());

        // Verify the book was updated in the internal collection
        Book foundBook = bookService.getBookById(1L);
        assertEquals("Updated Test Book", foundBook.getTitle());
    }

    @Test
    void testDeleteBook() {
        // Add book first
        bookService.createBook(testBook);
        assertEquals(1, bookService.getAllBooks().size());

        bookService.deleteBook(1L);

        // Verify the book was deleted
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(0, allBooks.size());

        Book foundBook = bookService.getBookById(1L);
        assertNull(foundBook);
    }

    @Test
    void testSearchBooks() {
        // Add books with distinct searchable terms
        Book javaBook = Book.builder()
                .id(2L)
                .title("Programming in Java")
                .ISBN("2222222222")
                .description("A Java programming guide")
                .publicationYear(2020)
                .build();

        Book pythonBook = Book.builder()
                .id(3L)
                .title("Python Basics")
                .ISBN("3333333333")
                .description("A Python programming guide")
                .publicationYear(2021)
                .build();

        bookService.createBook(testBook);
        bookService.createBook(javaBook);
        bookService.createBook(pythonBook);

        // Search by title
        List<Book> pythonBooks = bookService.searchBooks("Python");
        assertEquals(1, pythonBooks.size());
        assertEquals("Python Basics", pythonBooks.get(0).getTitle());

        // Search by ISBN
        List<Book> isbnBooks = bookService.searchBooks("2222");
        assertEquals(1, isbnBooks.size());
        assertEquals("Programming in Java", isbnBooks.get(0).getTitle());

        // Search by description
        List<Book> testingBooks = bookService.searchBooks("testing");
        assertEquals(1, testingBooks.size());
        assertEquals("Test Book", testingBooks.get(0).getTitle());

        // Search with no matches
        List<Book> noMatches = bookService.searchBooks("xyz123");
        assertEquals(0, noMatches.size());
    }
}