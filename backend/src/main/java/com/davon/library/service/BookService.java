package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.BookCopyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Transactional
    public Book createBook(Book book) {
        log.debug("Creating book: {}", book.getTitle());
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        bookRepository.persist(book);
        return book;
    }

    @Transactional
    public Book updateBook(Long bookId, Book updatedBook) {
        log.debug("Updating book: {}", bookId);
        Book existingBook = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setPublicationYear(updatedBook.getPublicationYear());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setAuthors(updatedBook.getAuthors());

        return existingBook;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        log.debug("Deleting book: {}", bookId);
        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
        
        bookCopyRepository.delete("book.id", book.getId());
        bookRepository.delete(book);
    }

    public List<Book> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.listAll();
    }

    public Optional<Book> getBookById(Long bookId) {
        log.debug("Fetching book by ID: {}", bookId);
        return bookRepository.findByIdOptional(bookId);
    }

    public List<Book> searchBooks(String query) {
        log.debug("Searching books with query: {}", query);
        return bookRepository.search(query);
    }

    public boolean isBookAvailable(Long bookId) {
        return bookCopyRepository.count("book.id = ?1 and status = 'AVAILABLE'", bookId) > 0;
    }
}
