package com.davon.library.service;

import com.davon.library.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@QuarkusTest
class BookServiceTest {

    @Inject
    BookService bookService;

    @InjectMock
    BookRepository bookRepository;

    private Book testBook;
    private Set<Book> mockBooks;

    @BeforeEach
    void setUp() {
        // Create a test book
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .description("A test book for unit testing")
                .pages(200)
                .build();

        // Mock repository behavior - simulate in-memory storage
        mockBooks = new HashSet<>();

        // Mock save method
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            mockBooks.add(book);
            return book;
        });

        // Mock findById method
        when(bookRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return mockBooks.stream()
                    .filter(book -> book.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        });

        // Mock delete method
        doAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            mockBooks.remove(book);
            return null;
        }).when(bookRepository).delete(any(Book.class));

        // Add the test book to start
        bookService.createBook(testBook);
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = bookService.getAllBooks();
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void testGetBookById() {
        Book foundBook = bookService.getBookById(1L);
        assertNotNull(foundBook);
        assertEquals("Test Book", foundBook.getTitle());

        // Non-existent book
        Book notFoundBook = bookService.getBookById(999L);
        assertNull(notFoundBook);
    }

    @Test
    void testCreateBook() {
        Book newBook = Book.builder()
                .id(2L)
                .title("Another Test Book")
                .ISBN("0987654321")
                .publicationYear(2022)
                .build();

        Book createdBook = bookService.createBook(newBook);
        assertNotNull(createdBook);
        assertEquals("Another Test Book", createdBook.getTitle());

        // Verify save was called
        verify(bookRepository, atLeast(2)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .build();

        Book result = bookService.updateBook(1L, updatedBook);
        assertNotNull(result);
        assertEquals("Updated Test Book", result.getTitle());
    }

    @Test
    void testDeleteBook() {
        bookService.deleteBook(1L);

        // Verify the book was deleted from the service's internal storage
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(0, allBooks.size());

        Book foundBook = bookService.getBookById(1L);
        assertNull(foundBook);
    }

    @Test
    void testSearchBooks() {
        // Add more books with distinct searchable terms
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

        bookService.createBook(javaBook);
        bookService.createBook(pythonBook);

        // Verify we have 3 total books
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(3, allBooks.size());

        // Search by title - single result
        List<Book> pythonBooks = bookService.searchBooks("Python");
        assertEquals(1, pythonBooks.size());
        assertEquals("Python Basics", pythonBooks.get(0).getTitle());

        // Search by specific ISBN
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