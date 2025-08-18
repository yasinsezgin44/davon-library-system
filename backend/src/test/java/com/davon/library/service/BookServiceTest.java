package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.CategoryRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import jakarta.inject.Inject;
import com.davon.library.dto.BookRequestDTO;
import com.davon.library.model.Category;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
class BookServiceTest {

    @InjectMock
    BookRepository bookRepository;

    @InjectMock
    BookCopyRepository bookCopyRepository;

    @InjectMock
    CategoryRepository categoryRepository;

    @Inject
    BookService bookService;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findByIdOptional(1L)).thenReturn(Optional.of(category));
    }

    @Test
    void testCreateBook() {
        BookRequestDTO requestDTO = new BookRequestDTO("Test Title", "1234567890123", 2023, "description", "cover.jpg",
                100, 1L, 1L, 10, Set.of(1L));
        when(bookRepository.findByIsbn("1234567890123")).thenReturn(Optional.empty());

        bookService.createBook(requestDTO);

        verify(bookRepository).persist(any(Book.class));
    }

    @Test
    void testCreateBookDuplicateIsbn() {
        BookRequestDTO requestDTO = new BookRequestDTO("Duplicate ISBN", "duplicate-isbn", 2023, "description",
                "cover.jpg", 100, 1L, 1L, 10, Set.of(1L));
        when(bookRepository.findByIsbn("duplicate-isbn")).thenReturn(Optional.of(new Book()));

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(requestDTO));
    }

    @Test
    void testUpdateBook() {
        Book existing = new Book();
        existing.setId(1L);
        existing.setTitle("Old Title");

        BookRequestDTO updated = new BookRequestDTO("New Title", "1234567890123", 2023, "description", "cover.jpg", 100,
                1L, 1L, 10, Set.of(1L));

        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(existing));

        Book result = bookService.updateBook(1L, updated);

        assertEquals("New Title", result.getTitle());
    }

    @Test
    void testUpdateBookNotFound() {
        when(bookRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.updateBook(99L, new BookRequestDTO("New Title",
                "1234567890123", 2023, "description", "cover.jpg", 100, 1L, 1L, 10, Set.of(1L))));
    }

    @Test
    void testDeleteBook() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookCopyRepository).delete("book.id", 1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book());

        when(bookRepository.listAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
    }

    @Test
    void testGetBookById() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void testIsBookAvailable() {
        when(bookCopyRepository.count("book.id = ?1 and status = 'AVAILABLE'", 1L)).thenReturn(1L);

        boolean available = bookService.isBookAvailable(1L);

        assertTrue(available);
    }
}
