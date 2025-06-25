package com.davon.library.service;

import com.davon.library.model.Book;
import lombok.RequiredArgsConstructor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing books.
 */
@ApplicationScoped
public class BookService {
    private final Set<Book> books = new HashSet<>();

    @Inject
    BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return books.stream().collect(Collectors.toList());
    }

    public Book getBookById(Long id) {
        return books.stream()
                .filter(book -> Objects.equals(book.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public Book createBook(Book book) {
        books.add(book);
        return book;
    }

    public Book updateBook(Long id, Book updatedBook) {
        books.removeIf(book -> Objects.equals(book.getId(), id));
        books.add(updatedBook);
        return updatedBook;
    }

    public void deleteBook(Long id) {
        books.removeIf(book -> Objects.equals(book.getId(), id));
    }

    public List<Book> searchBooks(String query) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase())
                        || (book.getISBN() != null && book.getISBN().toLowerCase().contains(query.toLowerCase()))
                        || (book.getDescription() != null
                                && book.getDescription().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
    }
}