package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LibrarianService {

    private static final Logger log = LoggerFactory.getLogger(LibrarianService.class);

    @Inject
    private BookService bookService;

    @Inject
    private UserService userService;

    @Inject
    private LoanService loanService;

    @Inject
    private InventoryService inventoryService;

    @Transactional
    public Book addBookToCatalog(BookRequestDTO bookRequestDTO) {
        log.info("Librarian adding book to catalog: {}", bookRequestDTO.title());
        return bookService.createBook(bookRequestDTO);
    }

    @Transactional
    public Book updateBookInCatalog(Long bookId, Book bookDetails) {
        log.info("Librarian updating book in catalog: {}", bookId);
        return bookService.updateBook(bookId, bookDetails);
    }

    @Transactional
    public void removeBookFromCatalog(Long bookId) {
        log.info("Librarian removing book from catalog: {}", bookId);
        bookService.deleteBook(bookId);
    }

    @Transactional
    public User registerMember(User user) {
        log.info("Librarian registering new member: {}", user.getUsername());
        return userService.createUser(user);
    }

    @Transactional
    public Loan checkoutBookForMember(Long bookId, Long memberId) {
        log.info("Librarian checking out book {} for member {}", bookId, memberId);
        return loanService.checkoutBook(bookId, memberId);
    }

    @Transactional
    public void returnBookForMember(Long loanId) {
        log.info("Librarian returning book for loan {}", loanId);
        loanService.returnBook(loanId);
    }
}
