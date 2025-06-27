package com.davon.library.service;

import com.davon.library.dao.BookDAO;
import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Service for generating book labels.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class BookLabelService {

    private static final Logger logger = Logger.getLogger(BookLabelService.class.getName());

    @Inject
    private BookDAO bookDAO;

    /**
     * Generate labels based on book information.
     * Note: BookCopy functionality would require a BookCopyDAO implementation.
     * 
     * @param bookId the ID of the book to generate label for
     * @return formatted label string
     * @throws BookLabelException if label generation fails
     */
    public String generateLabel(Long bookId) throws BookLabelException {
        try {
            Book book = bookDAO.findById(bookId)
                    .orElseThrow(() -> new BookLabelException("Book not found with ID: " + bookId));

            return String.format("%s\n%s\n%s\nID: %d",
                    book.getTitle(),
                    book.getAuthors().stream()
                            .map(Author::getName)
                            .collect(Collectors.joining(", ")),
                    book.getISBN(),
                    book.getId());
        } catch (Exception e) {
            logger.severe("Failed to generate label for book ID: " + bookId + " - " + e.getMessage());
            throw new BookLabelException("Failed to generate label", e);
        }
    }

    /**
     * Custom exception for book label service operations.
     */
    public static class BookLabelException extends Exception {
        public BookLabelException(String message) {
            super(message);
        }

        public BookLabelException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
