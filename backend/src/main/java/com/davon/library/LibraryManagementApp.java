package com.davon.library;

import com.davon.library.config.AppConfig;
import com.davon.library.controller.BookController;
import com.davon.library.controller.UserController;
import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import com.davon.library.service.LoanService;
import com.davon.library.service.UserService;
import com.davon.library.service.AuthenticationService;
import com.davon.library.repository.InMemoryLoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Library Management System.
 */
public class LibraryManagementApp {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApp.class);

    public static void main(String[] args) {
        logger.info("Starting Library Management System");

        // Initialize configuration
        AppConfig appConfig = new AppConfig();

        // Initialize all services
        UserService userService = appConfig.userService();
        BookService bookService = appConfig.bookService();
        LoanService loanService = new LoanService(
                new InMemoryLoanRepository(),
                appConfig.bookCopyRepository());
        AuthenticationService authService = appConfig.authenticationService();

        // Initialize controllers
        BookController bookController = new BookController(bookService);
        UserController userController = appConfig.userController();

        // Add some sample data
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setISBN("9780132350884");
        book.setPublicationYear(2008);
        book.setDescription("A handbook of agile software craftsmanship");

        bookController.createBook(book);

        logger.info("Added sample book: {}", book.getTitle());
        logger.info("Library Management System started successfully");
    }
}