package com.davon.library.service;

import com.davon.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookRepository);
    }

    @Test
    void testCreateBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book savedBook = bookService.createBook(book);

        // Assert
        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
    }

    @Test
    void testGetBookById() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        // Act
        bookService.createBook(book);
        Book foundBook = bookService.getBookById(1L);

        // Assert
        assertNotNull(foundBook);
        assertEquals(1L, foundBook.getId());
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    void testUpdateBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Original Title");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Title");

        // Act
        bookService.createBook(book);
        Book result = bookService.updateBook(1L, updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void testDeleteBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        // Act
        bookService.createBook(book);
        bookService.deleteBook(1L);
        Book result = bookService.getBookById(1L);

        // Assert
        assertNull(result);
    }

    @Test
    void testSearchBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setTitle("Java Programming");
        book1.setAuthor("John Doe");
        book1.setIsbn("1234567890");

        Book book2 = new Book();
        book2.setTitle("Python Basics");
        book2.setAuthor("Jane Smith");
        book2.setIsbn("0987654321");

        // Act
        bookService.createBook(book1);
        bookService.createBook(book2);
        List<Book> results = bookService.searchBooks("Java");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }
}