package com.davon.library.service;

import com.davon.library.repository.BookRepository;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.Author;
import com.davon.library.model.Category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class BookService {

    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    public List<Book> getAllBooks() {
        return bookRepository.listAll();
    }

    public Book getBookById(Long id) {
        if (id == null) {
            return null;
        }
        return bookRepository.findById(id);
    }

    @Transactional
    public Book createBook(Book book) throws BookServiceException {
        try {
            validateBookForCreation(book);
            if (book != null && book.getIsbn() != null && isISBNExists(book.getIsbn())) {
                throw new BookServiceException("Book with ISBN " + book.getIsbn() + " already exists");
            }
            bookRepository.persist(book);
            return book;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create book", e);
            throw new BookServiceException("Failed to create book: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) throws BookServiceException {
        try {
            if (id == null) {
                throw new BookServiceException("Book ID cannot be null");
            }
            Book existingBook = bookRepository.findById(id);
            if (existingBook == null) {
                throw new BookServiceException("Book not found with ID: " + id);
            }
            updatedBook.setId(id);
            validateBookForUpdate(updatedBook);
            return bookRepository.getEntityManager().merge(updatedBook);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book", e);
            throw new BookServiceException("Failed to update book: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteBook(Long id) throws BookServiceException {
        try {
            if (id == null) {
                throw new BookServiceException("Book ID cannot be null");
            }
            Book book = bookRepository.findById(id);
            if (book == null) {
                throw new BookServiceException("Book not found with ID: " + id);
            }
            bookCopyRepository.delete("book.id", id);
            bookRepository.delete(book);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete book", e);
            throw new BookServiceException("Failed to delete book: " + e.getMessage(), e);
        }
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.find("title LIKE ?1 OR isbn LIKE ?1", "%" + query.trim() + "%").list();
    }

    public List<Book> getBooksByCategory(Category category) {
        return bookRepository.find("category", category).list();
    }

    public List<Book> getBooksByAuthor(Author author) {
        return bookRepository.find("authors", author).list();
    }

    public Book getBookByISBN(String isbn) {
        return bookRepository.find("isbn", isbn).firstResult();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.find("SELECT DISTINCT b FROM Book b JOIN b.copies c WHERE c.status = 'AVAILABLE'").list();
    }

    public boolean isISBNExists(String isbn) {
        return bookRepository.find("isbn", isbn).count() > 0;
    }

    private void validateBookForCreation(Book book) throws BookServiceException {
        if (book == null) {
            throw new BookServiceException("Book cannot be null");
        }
        if (book.getId() != null) {
            throw new BookServiceException("Book ID should be null for new books");
        }
    }

    private void validateBookForUpdate(Book book) throws BookServiceException {
        if (book == null) {
            throw new BookServiceException("Book cannot be null");
        }
        if (book.getId() == null) {
            throw new BookServiceException("Book ID cannot be null for updates");
        }
    }

    public boolean isBookAvailable(Long bookId) {
        return bookCopyRepository.count("book.id = ?1 and status = 'AVAILABLE'", bookId) > 0;
    }

    public long countBooks() {
        return bookRepository.count();
    }

    public static class BookServiceException extends Exception {
        public BookServiceException(String message) {
            super(message);
        }

        public BookServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
