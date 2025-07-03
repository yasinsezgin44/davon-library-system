package com.davon.library;

import com.davon.library.database.DatabaseConnectionManager;
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
 * Enhanced with MSSQL database connectivity demonstration.
 */
@QuarkusMain
public class LibraryManagementApp implements QuarkusApplication {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApp.class);

    @Inject
    BookService bookService;

    @Inject
    DatabaseConnectionManager databaseConnectionManager;

    public static void main(String[] args) {
        Quarkus.run(LibraryManagementApp.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        logger.info("Starting Library Management System with MSSQL Database");

        // Test database connectivity
        if (databaseConnectionManager.isDatabaseAccessible()) {
            logger.info("Database connection successful: {}", databaseConnectionManager.getDatabaseInfo());
        } else {
            logger.error("Database connection failed!");
            return 1;
        }

        // Add some sample data
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setISBN("9780132350884");
        book.setPublicationYear(2008);
        book.setDescription("A handbook of agile software craftsmanship");

        try {
            bookService.createBook(book);
            logger.info("Successfully added sample book: {}", book.getTitle());
        } catch (Exception e) {
            logger.error("Failed to add sample book", e);
        }

        logger.info("Library Management System started successfully");
        logger.info("API available at: http://localhost:8080");
        logger.info("Swagger UI available at: http://localhost:8080/q/swagger-ui");
        logger.info("Database status endpoint: http://localhost:8080/api/database/status");

        Quarkus.waitForExit();
        return 0;
    }
}