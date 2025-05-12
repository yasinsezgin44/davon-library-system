package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.BookCopy.CopyStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing library inventory.
 */
public class InventoryService {
    private final Set<Book> books = new HashSet<>();
    private final Set<BookCopy> bookCopies = new HashSet<>();

    public int getTotalBooks() {
        return books.size();
    }

    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(this::isBookAvailable)
                .collect(Collectors.toList());
    }

    public List<Book> searchBooks(String query) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase())
                        || book.getISBN().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(long bookId) {
        books.removeIf(book -> Objects.equals(book.getId(), bookId));
        bookCopies.removeIf(copy -> copy.getBook().getId() == bookId);
    }

    public void updateBookStatus(long bookCopyId, CopyStatus status) {
        bookCopies.stream()
                .filter(copy -> Objects.equals(copy.getId(), bookCopyId))
                .findFirst()
                .ifPresent(copy -> copy.setStatus(status));
    }

    private boolean isBookAvailable(Book book) {
        return bookCopies.stream()
                .anyMatch(copy -> copy.getBook().equals(book) && copy.getStatus() == CopyStatus.AVAILABLE);
    }

    // For managing book copies
    public void addBookCopy(BookCopy copy) {
        bookCopies.add(copy);
    }

    public void removeBookCopy(long bookCopyId) {
        bookCopies.removeIf(copy -> Objects.equals(copy.getId(), bookCopyId));
    }

    public List<BookCopy> getCopiesForBook(long bookId) {
        return bookCopies.stream()
                .filter(copy -> copy.getBook().getId() == bookId)
                .collect(Collectors.toList());
    }
}