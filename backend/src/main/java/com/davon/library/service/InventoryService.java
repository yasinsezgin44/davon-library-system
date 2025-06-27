package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.BookCopy.CopyStatus;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing library inventory.
 */
@ApplicationScoped
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
        String q = query.toLowerCase();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(q)
                        || book.getISBN().toLowerCase().contains(q)
                        || (book.getAuthors() != null
                                && book.getAuthors().stream().anyMatch(a -> a.getName().toLowerCase().contains(q)))
                        || (book.getCategory() != null && book.getCategory().getName().toLowerCase().contains(q))
                        || (book.getPublisher() != null && book.getPublisher().getName().toLowerCase().contains(q)))
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
        if (!bookExists)
            return false;
        return bookCopies.add(copy);
    }

    public boolean removeBookCopy(long bookCopyId) {
        return bookCopies.removeIf(copy -> Objects.equals(copy.getId(), bookCopyId));
    }

    public void updateBookStatus(Long bookId, CopyStatus status) {
        bookCopies.stream()
                .filter(copy -> copy.getBook().getId().equals(bookId))
                .forEach(copy -> copy.setStatus(status));
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

    public boolean processBookDisposal(long bookCopyId, String disposalReason) {
        Optional<BookCopy> copyOpt = bookCopies.stream()
                .filter(copy -> Objects.equals(copy.getId(), bookCopyId))
                .findFirst();
        if (copyOpt.isPresent()) {
            BookCopy copy = copyOpt.get();
            // Consider adding a DISPOSED status to CopyStatus enum
            copy.setStatus(CopyStatus.LOST); // Using LOST as alternative
            // Consider adding disposalReason to BookCopy or create disposal log
            return true;
        }
        return false;
    }
}