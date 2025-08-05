package com.davon.library.service;

import com.davon.library.repository.BookRepository;
import com.davon.library.model.Book;
import com.davon.library.model.Category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for cataloging operations.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class CatalogingService {

    private static final Logger logger = Logger.getLogger(CatalogingService.class.getName());

    @Inject
    private BookRepository bookRepository;

    // Constructor

    public boolean verifyISBN(String isbn) {
        // Implement actual ISBN validation algorithm
        // (check digit verification, format validation)
        return isbn != null && (isbn.length() == 10 || isbn.length() == 13);
    }

    /**
     * Adds a new book to the catalog.
     * 
     * @param book the book to add
     * @return the added book with assigned ID
     */
    @Transactional
    public Book addBookToCatalog(Book book) {
        try {
            if (book == null) {
                throw new IllegalArgumentException("Book cannot be null");
            }

            // Validate book doesn't already exist by ISBN
            if (book.getISBN() != null && bookRepository.existsByISBN(book.getISBN())) {
                throw new IllegalArgumentException("Book with ISBN " + book.getISBN() + " already exists");
            }

            bookRepository.persist(book);
            logger.info("Book added to catalog: " + book.getTitle());
            return book;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to add book to catalog", e);
            throw new RuntimeException("Failed to add book to catalog", e);
        }
    }

    /**
     * Updates a book in the catalog.
     * 
     * @param book the book to update
     * @return the updated book
     */
    @Transactional
    public Book updateBookInCatalog(Book book) {
        try {
            if (book == null || book.getId() == null) {
                throw new IllegalArgumentException("Book and book ID cannot be null");
            }

            Book existingBook = bookRepository.findById(book.getId());
            if (existingBook == null) {
                throw new IllegalArgumentException("Book not found with ID: " + book.getId());
            }

            Book updatedBook = bookRepository.getEntityManager().merge(book);
            logger.info("Book updated in catalog: " + book.getTitle());
            return updatedBook;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book in catalog", e);
            throw new RuntimeException("Failed to update book in catalog", e);
        }
    }

    /**
     * Removes a book from the catalog.
     * 
     * @param bookId the ID of the book to remove
     */
    @Transactional
    public void removeBookFromCatalog(Long bookId) {
        try {
            if (bookId == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }

            boolean deleted = bookRepository.deleteById(bookId);
            if (!deleted) {
                throw new IllegalArgumentException("Book not found with ID: " + bookId);
            }

            logger.info("Book removed from catalog with ID: " + bookId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to remove book from catalog", e);
            throw new RuntimeException("Failed to remove book from catalog", e);
        }
    }

    /**
     * Custom exception for cataloging service operations.
     */
    public static class CatalogingException extends Exception {
        public CatalogingException(String message) {
            super(message);
        }

        public CatalogingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void assignCategories(Long bookId, List<Category> categories) {
        // Implement category assignment
    }
}
