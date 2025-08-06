package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class InventoryService {

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.find("SELECT DISTINCT b FROM Book b JOIN b.copies c WHERE c.status = 'AVAILABLE'").list();
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.find("title LIKE ?1 OR isbn LIKE ?1", "%" + query + "%").list();
    }

    @Transactional
    public Book addBook(Book book) {
        bookRepository.persist(book);
        return book;
    }

    @Transactional
    public void removeBook(long bookId) {
        bookRepository.deleteById(bookId);
    }

    @Transactional
    public BookCopy addBookCopy(BookCopy copy) {
        bookCopyRepository.persist(copy);
        return copy;
    }

    @Transactional
    public void removeBookCopy(long bookCopyId) {
        bookCopyRepository.deleteById(bookCopyId);
    }

    @Transactional
    public void updateBookStatus(Long bookId, String status) {
        bookCopyRepository.update("status = ?1 WHERE book.id = ?2", status, bookId);
    }

    @Transactional
    public boolean updateBookCopyLocation(long bookCopyId, String newLocation) {
        BookCopy copy = bookCopyRepository.findById(bookCopyId);
        if (copy != null) {
            copy.setLocation(newLocation);
            bookCopyRepository.persist(copy);
            return true;
        }
        return false;
    }

    public List<BookCopy> getCopiesForBook(long bookId) {
        return bookCopyRepository.list("book.id", bookId);
    }

    @Transactional
    public boolean processBookDisposal(long bookCopyId, String disposalReason) {
        BookCopy copy = bookCopyRepository.findById(bookCopyId);
        if (copy != null) {
            copy.setStatus("LOST");
            bookCopyRepository.persist(copy);
            return true;
        }
        return false;
    }
}
