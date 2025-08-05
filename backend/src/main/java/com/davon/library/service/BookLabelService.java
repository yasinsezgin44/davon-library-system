package com.davon.library.service;

import com.davon.library.repository.BookRepository;
import com.davon.library.model.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Service for generating book labels and barcodes.
 */
@ApplicationScoped
public class BookLabelService {

    private static final Logger logger = Logger.getLogger(BookLabelService.class.getName());

    @Inject
    BookRepository bookRepository;

    /**
     * Generates a label for a book.
     * 
     * @param bookId the book ID
     * @return the generated label content
     */
    public String generateBookLabel(Long bookId) {
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        StringBuilder label = new StringBuilder();
        label.append("BOOK LABEL\n");
        label.append("Title: ").append(book.getTitle()).append("\n");
        label.append("ISBN: ").append(book.getISBN()).append("\n");

        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            label.append("Author(s): ");
            book.getAuthors().forEach(author -> label.append(author.getName()).append(" "));
            label.append("\n");
        }

        if (book.getPublisher() != null) {
            label.append("Publisher: ").append(book.getPublisher().getName()).append("\n");
        }

        label.append("Year: ").append(book.getPublicationYear()).append("\n");

        logger.info("Generated label for book: " + book.getTitle());
        return label.toString();
    }

    /**
     * Generates a barcode for a book.
     * 
     * @param bookId the book ID
     * @return the barcode data
     */
    public String generateBookBarcode(Long bookId) {
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        // Simple barcode generation - in production would use a proper barcode library
        String barcode = "B" + String.format("%08d", bookId) + book.getISBN();

        logger.info("Generated barcode for book: " + book.getTitle());
        return barcode;
    }
}
