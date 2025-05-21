package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookLabelServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookLabelService bookLabelService;

    private Book testBook;
    private BookCopy testBookCopy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Author author = Author.builder()
                .id(1L)
                .name("Test Author")
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("9781234567890")
                .authors(Set.of(author))
                .build();

        testBookCopy = BookCopy.builder()
                .id(1L)
                .book(testBook)
                .location("A1.B2.C3")
                .build();
    }

    @Test
    void testGenerateLabel() {
        // Arrange
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(testBookCopy));

        // Act
        String label = bookLabelService.generateLabel(1L);

        // Assert
        assertNotNull(label);
        assertTrue(label.contains("Test Book"));
        assertTrue(label.contains("Test Author"));
        assertTrue(label.contains("9781234567890"));
        assertTrue(label.contains("ID: 1"));
        assertTrue(label.contains("Loc: A1.B2.C3"));

        verify(bookCopyRepository).findById(1L);
    }

    @Test
    void testGenerateLabel_BookCopyNotFound() {
        // Arrange
        when(bookCopyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookLabelService.generateLabel(999L);
        });

        assertEquals("BookCopy not found", exception.getMessage());
        verify(bookCopyRepository).findById(999L);
    }
}