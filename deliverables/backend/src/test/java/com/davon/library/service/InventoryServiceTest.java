package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {
    private InventoryService inventoryService;
    private Book book;
    private BookCopy copy;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
        book = Book.builder().id(1L).title("Test Book").ISBN("1234567890").build();
        copy = BookCopy.builder().id(1L).book(book).status(BookCopy.CopyStatus.AVAILABLE).build();
    }

    @Test
    void testAddAndRemoveBook() {
        assertTrue(inventoryService.addBook(book));
        assertEquals(1, inventoryService.getTotalBooks());
        assertTrue(inventoryService.removeBook(book.getId()));
        assertEquals(0, inventoryService.getTotalBooks());
    }

    @Test
    void testAddAndRemoveBookCopy() {
        inventoryService.addBook(book);
        assertTrue(inventoryService.addBookCopy(copy));
        assertEquals(1, inventoryService.getCopiesForBook(book.getId()).size());
        assertTrue(inventoryService.removeBookCopy(copy.getId()));
        assertEquals(0, inventoryService.getCopiesForBook(book.getId()).size());
    }

    @Test
    void testSearchBooks() {
        inventoryService.addBook(book);
        List<Book> results = inventoryService.searchBooks("Test");
        assertFalse(results.isEmpty());
        assertEquals(book, results.get(0));
    }

    @Test
    void testUpdateBookStatus() {
        inventoryService.addBook(book);
        inventoryService.addBookCopy(copy);
        assertTrue(inventoryService.updateBookStatus(copy.getId(), BookCopy.CopyStatus.LOST));
        assertEquals(BookCopy.CopyStatus.LOST, inventoryService.getCopiesForBook(book.getId()).get(0).getStatus());
    }
}