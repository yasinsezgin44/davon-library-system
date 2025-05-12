package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    private LoanService loanService;
    private Member member;
    private Book book;
    private BookCopy copy;

    @BeforeEach
    void setUp() {
        loanService = new LoanService();
        member = Member.builder().id(1L).fullName("Test Member").build();
        book = Book.builder().id(1L).title("Test Book").ISBN("1234567890").build();
        copy = BookCopy.builder().id(1L).book(book).status(BookCopy.CopyStatus.AVAILABLE).build();
    }

    @Test
    void testCheckoutBookSuccess() {
        Loan loan = loanService.checkoutBook(member, copy, 14);
        assertNotNull(loan);
        assertEquals(Loan.LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(BookCopy.CopyStatus.CHECKED_OUT, copy.getStatus());
        assertEquals(member, loan.getMember());
        assertEquals(copy, loan.getBookCopy());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
    }

    @Test
    void testCheckoutBookNotAvailable() {
        copy.setStatus(BookCopy.CopyStatus.LOST);
        Loan loan = loanService.checkoutBook(member, copy, 14);
        assertNull(loan);
    }

    @Test
    void testReturnBookSuccess() {
        loanService.checkoutBook(member, copy, 14);
        Loan returned = loanService.returnBook(member, copy);
        assertNotNull(returned);
        assertEquals(Loan.LoanStatus.RETURNED, returned.getStatus());
        assertEquals(BookCopy.CopyStatus.AVAILABLE, copy.getStatus());
        assertEquals(LocalDate.now(), returned.getReturnDate());
    }

    @Test
    void testReturnBookNotFound() {
        Loan returned = loanService.returnBook(member, copy);
        assertNull(returned);
    }
}