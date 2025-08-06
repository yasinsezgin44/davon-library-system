package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.BookCopyRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class BookServiceTest {

    @Inject
    BookService bookService;

    @InjectMock
    BookRepository bookRepository;

    @InjectMock
    BookCopyRepository bookCopyRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
    }

    @Test
    void testCreateBook() {
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
        bookService.createBook(book);
        Mockito.verify(bookRepository).persist(any(Book.class));
    }

    @Test
    void testCreateBook_isbnExists() {
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(book));
    }

    @Test
    void testUpdateBook() {
        Book updatedDetails = new Book();
        updatedDetails.setTitle("Updated Title");

        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));
        Book updatedBook = bookService.updateBook(1L, updatedDetails);
        assertEquals("Updated Title", updatedBook.getTitle());
    }

    @Test
    void testUpdateBook_notFound() {
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.updateBook(1L, new Book()));
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = bookService.getBookById(1L);
        assertTrue(foundBook.isPresent());
        assertEquals("Test Book", foundBook.get().getTitle());
    }
}
