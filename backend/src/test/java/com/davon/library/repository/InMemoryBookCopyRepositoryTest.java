package com.davon.library.repository;

import com.davon.library.model.BookCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBookCopyRepositoryTest {
    private InMemoryBookCopyRepository repository;
    private BookCopy bookCopy;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookCopyRepository();
        bookCopy = new BookCopy();
        bookCopy.setId(null);
    }

    @Test
    void testSaveAndFindById() {
        BookCopy saved = repository.save(bookCopy);
        assertNotNull(saved.getId());
        Optional<BookCopy> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved, found.get());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<BookCopy> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }
}