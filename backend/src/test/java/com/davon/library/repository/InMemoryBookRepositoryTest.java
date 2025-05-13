package com.davon.library.repository;

import com.davon.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBookRepositoryTest {

    private InMemoryBookRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookRepository();
    }

    @Test
    void testSaveNewBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        book.setISBN("1234567890");

        // Act
        Book savedBook = repository.save(book);

        // Assert
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("Test Book", savedBook.getTitle());
    }

    @Test
    void testSaveExistingBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("Original Title");
        Book savedBook = repository.save(book);
        Long id = savedBook.getId();

        // Update the book
        savedBook.setTitle("Updated Title");

        // Act
        Book updatedBook = repository.save(savedBook);

        // Assert
        assertEquals(id, updatedBook.getId());
        assertEquals("Updated Title", updatedBook.getTitle());
    }

    @Test
    void testFindById() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        Book savedBook = repository.save(book);

        // Act
        Book foundBook = repository.findById(savedBook.getId());

        // Assert
        assertNotNull(foundBook);
        assertEquals(savedBook.getId(), foundBook.getId());
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Book foundBook = repository.findById(999L);

        // Assert
        assertNull(foundBook);
    }

    @Test
    void testDelete() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        Book savedBook = repository.save(book);

        // Act
        repository.delete(savedBook);
        Book foundBook = repository.findById(savedBook.getId());

        // Assert
        assertNull(foundBook);
    }
}