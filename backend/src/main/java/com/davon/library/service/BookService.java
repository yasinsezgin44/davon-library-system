package com.davon.library.service;

import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for managing books.
 * This service follows SOLID principles by depending on abstractions (BookDAO)
 * and focusing only on business logic, not data access.
 */
@ApplicationScoped
public class BookService {

    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    private final BookDAO bookDAO;

    /**
     * Constructor-based injection preferred for immutability and testability.
     */
    @Inject
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Retrieves all books from the system.
     * 
     * @return a list of all books
     */
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    /**
     * Retrieves a book by its ID.
     * 
     * @param id the book ID
     * @return the book if found, null otherwise
     */
    public Book getBookById(Long id) {
        if (id == null) {
            return null;
        }
        return bookDAO.findById(id).orElse(null);
    }

    /**
     * Creates a new book in the system.
     * 
     * @param book the book to create
     * @return the created book with assigned ID
     * @throws BookServiceException if the book creation fails
     */
    public Book createBook(Book book) throws BookServiceException {
        try {
            // 1. Validate common book metadata rules
            validateBookForCreation(book);

            // 2. Prevent duplicate ISBNs at the service layer before hitting the DAO
            if (book != null && book.getISBN() != null && isISBNExists(book.getISBN())) {
                throw new BookServiceException("Book with ISBN " + book.getISBN() + " already exists");
            }

            // 3. Persist the new book
            return bookDAO.save(book);
        } catch (DAOException e) {
            // Convert DAO layer exceptions to service layer exceptions so that callers
            // never need to depend on DAO specific types.
            logger.log(Level.SEVERE, "Failed to create book", e);
            throw new BookServiceException("Failed to create book: " + e.getMessage(), e);
        } catch (Exception e) {
            // Defensive catch-all to guarantee the contract of throwing only
            // BookServiceException out of this method.
            logger.log(Level.SEVERE, "Unexpected error while creating book", e);
            throw new BookServiceException("Failed to create book due to unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing book in the system.
     * 
     * @param id          the ID of the book to update
     * @param updatedBook the updated book data
     * @return the updated book
     * @throws BookServiceException if the book update fails
     */
    public Book updateBook(Long id, Book updatedBook) throws BookServiceException {
        try {
            if (id == null) {
                throw new BookServiceException("Book ID cannot be null");
            }

            if (!bookDAO.existsById(id)) {
                throw new BookServiceException("Book not found with ID: " + id);
            }

            updatedBook.setId(id);
            validateBookForUpdate(updatedBook);
            return bookDAO.update(updatedBook);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to update book", e);
            throw new BookServiceException("Failed to update book: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a book by its ID.
     * 
     * @param id the ID of the book to delete
     * @throws BookServiceException if the book deletion fails
     */
    public void deleteBook(Long id) throws BookServiceException {
        try {
            if (id == null) {
                throw new BookServiceException("Book ID cannot be null");
            }

            if (!bookDAO.existsById(id)) {
                throw new BookServiceException("Book not found with ID: " + id);
            }

            bookDAO.deleteById(id);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to delete book", e);
            throw new BookServiceException("Failed to delete book: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for books using various criteria.
     * 
     * @param query the search query
     * @return a list of books matching the search criteria
     */
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookDAO.searchBooks(query.trim());
    }

    /**
     * Finds books by category.
     * 
     * @param category the category to search for
     * @return a list of books in the specified category
     */
    public List<Book> getBooksByCategory(Category category) {
        return bookDAO.findByCategory(category);
    }

    /**
     * Finds books by author.
     * 
     * @param author the author to search for
     * @return a list of books by the specified author
     */
    public List<Book> getBooksByAuthor(Author author) {
        return bookDAO.findByAuthor(author);
    }

    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return the book if found, null otherwise
     */
    public Book getBookByISBN(String isbn) {
        return bookDAO.findByISBN(isbn).orElse(null);
    }

    /**
     * Gets all available books (books with available copies).
     * 
     * @return a list of available books
     */
    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailableBooks();
    }

    /**
     * Checks if a book exists by its ISBN.
     * 
     * @param isbn the ISBN to check
     * @return true if a book with this ISBN exists, false otherwise
     */
    public boolean isISBNExists(String isbn) {
        return bookDAO.existsByISBN(isbn);
    }

    /**
     * Validates a book for creation.
     * 
     * @param book the book to validate
     * @throws BookServiceException if validation fails
     */
    private void validateBookForCreation(Book book) throws BookServiceException {
        if (book == null) {
            throw new BookServiceException("Book cannot be null");
        }

        if (book.getId() != null) {
            throw new BookServiceException("Book ID should be null for new books");
        }

        validateBookMetadata(book);
    }

    /**
     * Validates a book for update.
     * 
     * @param book the book to validate
     * @throws BookServiceException if validation fails
     */
    private void validateBookForUpdate(Book book) throws BookServiceException {
        if (book == null) {
            throw new BookServiceException("Book cannot be null");
        }

        if (book.getId() == null) {
            throw new BookServiceException("Book ID cannot be null for updates");
        }

        validateBookMetadata(book);
    }

    /**
     * Validates book metadata.
     * 
     * @param book the book to validate
     * @throws BookServiceException if validation fails
     */
    private void validateBookMetadata(Book book) throws BookServiceException {
        if (!book.validateMetadata()) {
            throw new BookServiceException("Invalid book metadata");
        }
    }

    /**
     * Custom exception for book service operations.
     */
    public static class BookServiceException extends Exception {
        public BookServiceException(String message) {
            super(message);
        }

        public BookServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}