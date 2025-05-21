package com.davon.library.service;

import com.davon.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogingServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private CatalogingService catalogingService;

    @Mock
    private Book validBook;

    @Mock
    private Book invalidMetadataBook;

    private Book invalidIsbnBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a non-mock Book with invalid ISBN
        invalidIsbnBook = Book.builder()
                .id(2L)
                .title("Invalid ISBN Book")
                .ISBN("123") // Too short
                .build();

        // Configure mocks
        lenient().when(validBook.getISBN()).thenReturn("9781234567890");
        lenient().when(validBook.validateMetadata()).thenReturn(true);

        lenient().when(invalidMetadataBook.getISBN()).thenReturn("9781234567890");
        lenient().when(invalidMetadataBook.validateMetadata()).thenReturn(false);
    }

    @Test
    void testVerifyISBN_Valid() {
        // Act & Assert
        assertTrue(catalogingService.verifyISBN("1234567890")); // 10 digits
        assertTrue(catalogingService.verifyISBN("1234567890123")); // 13 digits
    }

    @Test
    void testVerifyISBN_Invalid() {
        // Act & Assert
        assertFalse(catalogingService.verifyISBN(null));
        assertFalse(catalogingService.verifyISBN(""));
        assertFalse(catalogingService.verifyISBN("12345")); // Too short
        assertFalse(catalogingService.verifyISBN("12345678901234")); // Too long
    }

    @Test
    void testCatalogNewBook_Success() {
        // Arrange
        when(bookRepository.save(validBook)).thenReturn(validBook);

        // Act
        Book result = catalogingService.catalogNewBook(validBook);

        // Assert
        assertNotNull(result);
        verify(bookRepository).save(validBook);
    }

    @Test
    void testCatalogNewBook_InvalidISBN() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogingService.catalogNewBook(invalidIsbnBook);
        });

        assertEquals("Invalid ISBN", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testCatalogNewBook_InvalidMetadata() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogingService.catalogNewBook(invalidMetadataBook);
        });

        assertEquals("Invalid book metadata", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }
}