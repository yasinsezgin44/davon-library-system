package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.BookRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @InjectMock
    BookRepository bookRepository;

    @InjectMock
    BookCopyRepository bookCopyRepository;

    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);

        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
    }

    @Test
    void testAddBookCopy() {
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));
        inventoryService.addBookCopy(1L, "Shelf A");
        Mockito.verify(bookCopyRepository).persist(any(BookCopy.class));
    }

    @Test
    void testRemoveBookCopy() {
        when(bookCopyRepository.deleteById(1L)).thenReturn(true);
        inventoryService.removeBookCopy(1L);
        Mockito.verify(bookCopyRepository).deleteById(1L);
    }

    @Test
    void testRemoveBookCopy_notFound() {
        when(bookCopyRepository.deleteById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> inventoryService.removeBookCopy(1L));
    }

    @Test
    void testUpdateBookCopyStatus() {
        when(bookCopyRepository.findByIdOptional(1L)).thenReturn(Optional.of(bookCopy));
        BookCopy updatedCopy = inventoryService.updateBookCopyStatus(1L, CopyStatus.IN_REPAIR);
        assertEquals(CopyStatus.IN_REPAIR, updatedCopy.getStatus());
    }
}
