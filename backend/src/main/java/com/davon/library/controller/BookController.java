package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * REST controller for book-related operations.
 */
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public Book getBookById(Long id) {
        return bookService.getBookById(id);
    }

    public Book createBook(Book book) {
        return bookService.createBook(book);
    }

    public Book updateBook(Long id, Book book) {
        return bookService.updateBook(id, book);
    }

    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }

    public List<Book> searchBooks(String query) {
        return bookService.searchBooks(query);
    }
}