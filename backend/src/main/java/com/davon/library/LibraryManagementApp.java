package com.davon.library;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Library Management System.
 */
@QuarkusMain
public class LibraryManagementApp implements QuarkusApplication {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApp.class);

    @Inject
    BookService bookService;

    public static void main(String[] args) {
        Quarkus.run(LibraryManagementApp.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        logger.info("Starting Library Management System");

        // Add some sample data
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setISBN("9780132350884");
        book.setPublicationYear(2008);
        book.setDescription("A handbook of agile software craftsmanship");

        bookService.createBook(book);

        logger.info("Added sample book: {}", book.getTitle());
        logger.info("Library Management System started successfully");

        Quarkus.waitForExit();
        return 0;
    }
}