package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.ReservationRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    ReservationRepository reservationRepository;

    private Book book;

    @BeforeEach
    @Transactional
    void setUp() {
        reservationRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();

        book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890123");
        bookRepository.persist(book);
    }

    @Test
    @Transactional
    void testAddBookCopy() {
        BookCopy bookCopy = inventoryService.addBookCopy(book.getId());

        assertNotNull(bookCopy);
        assertNotNull(bookCopy.getId());
        assertEquals(book.getId(), bookCopy.getBook().getId());
    }
}
