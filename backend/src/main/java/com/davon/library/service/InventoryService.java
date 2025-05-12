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

    public boolean addBook(Book book) {
        return books.add(book);
    }

    public boolean removeBook(long bookId) {
        boolean removed = books.removeIf(book -> Objects.equals(book.getId(), bookId));
        if (removed) {
            bookCopies.removeIf(copy -> copy.getBook().getId() == bookId);
        }
        return removed;
    }

    public boolean addBookCopy(BookCopy copy) {
        // Only add if the referenced book exists
        boolean bookExists = books.stream().anyMatch(b -> Objects.equals(b.getId(), copy.getBook().getId()));
        if (!bookExists) return false;
        return bookCopies.add(copy);
    }

    public boolean removeBookCopy(long bookCopyId) {
        return bookCopies.removeIf(copy -> Objects.equals(copy.getId(), bookCopyId));
    }

    public boolean updateBookStatus(long bookCopyId, CopyStatus status) {
        Optional<BookCopy> copyOpt = bookCopies.stream()
                .filter(copy -> Objects.equals(copy.getId(), bookCopyId))
                .findFirst();
        copyOpt.ifPresent(copy -> copy.setStatus(status));
        return copyOpt.isPresent();
    }

    public boolean updateBookCopyLocation(long bookCopyId, String newLocation) {
        Optional<BookCopy> copyOpt = bookCopies.stream()
                .filter(copy -> Objects.equals(copy.getId(), bookCopyId))
                .findFirst();
        copyOpt.ifPresent(copy -> copy.setLocation(newLocation));
        return copyOpt.isPresent();
    }

    private boolean isBookAvailable(Book book) {
        return bookCopies.stream()
                .anyMatch(copy -> copy.getBook().equals(book) && copy.getStatus() == CopyStatus.AVAILABLE);
    }

    public List<BookCopy> getCopiesForBook(long bookId) {
        return bookCopies.stream()
                .filter(copy -> copy.getBook().getId() == bookId)
                .collect(Collectors.toList());
    }
}