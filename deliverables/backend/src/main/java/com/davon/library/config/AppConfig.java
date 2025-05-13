package com.davon.library.config;

import com.davon.library.repository.InMemoryBookRepository;
import com.davon.library.service.BookRepository;
import com.davon.library.service.BookService;

/**
 * Application configuration class.
 */
public class AppConfig {

    public BookRepository bookRepository() {
        return new InMemoryBookRepository();
    }

    public BookService bookService() {
        return new BookService(bookRepository());
    }
}