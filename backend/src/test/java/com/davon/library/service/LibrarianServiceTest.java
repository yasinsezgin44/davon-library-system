package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Loan;
import com.davon.library.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
class LibrarianServiceTest {

    @Inject
    LibrarianService librarianService;

    @InjectMock
    BookService bookService;

    @InjectMock
    UserService userService;

    @InjectMock
    LoanService loanService;

    private Book book;
    private User user;
    private Loan loan;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        loan = new Loan();
        loan.setId(1L);
    }

    @Test
    void testAddBookToCatalog() {
        when(bookService.createBook(any(Book.class))).thenReturn(book);
        librarianService.addBookToCatalog(new Book());
        Mockito.verify(bookService).createBook(any(Book.class));
    }

    @Test
    void testRegisterMember() {
        when(userService.createUser(any(User.class))).thenReturn(user);
        librarianService.registerMember(new User());
        Mockito.verify(userService).createUser(any(User.class));
    }

    @Test
    void testCheckoutBookForMember() {
        when(loanService.checkoutBook(anyLong(), anyLong())).thenReturn(loan);
        librarianService.checkoutBookForMember(1L, 1L);
        Mockito.verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    void testReturnBookForMember() {
        librarianService.returnBookForMember(1L);
        Mockito.verify(loanService).returnBook(1L);
    }
}
