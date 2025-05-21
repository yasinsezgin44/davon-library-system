package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Publisher;
import com.davon.library.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private BookService bookService;
    private TestBookRepository bookRepository;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository = new TestBookRepository();
        bookService = new BookService(bookRepository);

        // Create a test book
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .description("A test book for unit testing")
                .pages(200)
                .build();

        // Add the test book
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

        // Verify it was added to the collection
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(2, allBooks.size());
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

        // Verify the book was updated in the collection
        Book foundBook = bookService.getBookById(1L);
        assertEquals("Updated Test Book", foundBook.getTitle());
    }

    @Test
    void testDeleteBook() {
        bookService.deleteBook(1L);

        // Verify the book was deleted
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

    // Test repository implementation
    static class TestBookRepository implements BookRepository {
        private Set<Book> books = new HashSet<>();

        @Override
        public Book save(Book book) {
            books.add(book);
            return book;
        }

        @Override
        public Book findById(Long id) {
            return books.stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void delete(Book book) {
            books.remove(book);
        }
    }
}