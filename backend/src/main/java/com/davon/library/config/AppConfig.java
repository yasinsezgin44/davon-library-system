package com.davon.library.config;

import com.davon.library.repository.InMemoryBookRepository;
import com.davon.library.service.BookRepository;
import com.davon.library.service.BookService;
import com.davon.library.service.EmailService;
import com.davon.library.service.SecurityService;
import com.davon.library.service.AuthenticationService;
import com.davon.library.controller.UserController;
import com.davon.library.repository.InMemoryUserRepository;
import com.davon.library.service.UserRepository;
import com.davon.library.service.UserService;
import com.davon.library.repository.InMemoryBookCopyRepository;
import com.davon.library.service.BookCopyRepository;

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

    public EmailService emailService() {
        return new EmailService();
    }

    public SecurityService securityService() {
        return new SecurityService(userRepository());
    }

    public AuthenticationService authenticationService() {
        return new AuthenticationService(userService(), emailService(), securityService());
    }

    public UserController userController() {
        return new UserController(userService());
    }

    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    public UserService userService() {
        return new UserService(userRepository());
    }

    public BookCopyRepository bookCopyRepository() {
        return new InMemoryBookCopyRepository();
    }
}