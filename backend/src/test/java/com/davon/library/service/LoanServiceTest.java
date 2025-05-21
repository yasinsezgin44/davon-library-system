package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    private LoanService loanService;
    private Member member;
    private Book book;
    private BookCopy copy;
    private TestLoanRepository loanRepository;
    private TestBookCopyRepository bookCopyRepository;

    @BeforeEach
    void setUp() {
        loanRepository = new TestLoanRepository();
        bookCopyRepository = new TestBookCopyRepository();
        loanService = new LoanService(loanRepository, bookCopyRepository);

        member = Member.builder().id(1L).fullName("Test Member").active(true).build();
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
        if (loan != null) {
            fail("Loan should be null but was: " + loan.toString());
        }
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

    // Simple test implementations of repositories
    static class TestLoanRepository implements LoanRepository {
        private List<Loan> loans = new ArrayList<>();

        @Override
        public Loan save(Loan loan) {
            loans.removeIf(l -> l.getId() != null && l.getId().equals(loan.getId()));
            loans.add(loan);
            return loan;
        }

        @Override
        public Optional<Loan> findById(Long id) {
            return loans.stream().filter(l -> l.getId() != null && l.getId().equals(id)).findFirst();
        }

        @Override
        public List<Loan> findAllByMember(Member member) {
            return loans.stream().filter(l -> l.getMember().equals(member)).collect(Collectors.toList());
        }

        @Override
        public Optional<Loan> findActiveLoanByMemberAndBookCopy(Member member, BookCopy copy) {
            return loans.stream()
                    .filter(l -> l.getMember().equals(member) &&
                            l.getBookCopy().equals(copy) &&
                            l.getStatus() == Loan.LoanStatus.ACTIVE)
                    .findFirst();
        }

        @Override
        public List<Loan> findOverdueLoans(LocalDate date) {
            return loans.stream()
                    .filter(l -> l.getDueDate().isBefore(date) && l.getStatus() == Loan.LoanStatus.ACTIVE)
                    .collect(Collectors.toList());
        }
    }

    static class TestBookCopyRepository implements BookCopyRepository {
        private List<BookCopy> copies = new ArrayList<>();

        @Override
        public BookCopy save(BookCopy copyToSave) {
            copies.removeIf(c -> c.getId() != null && c.getId().equals(copyToSave.getId()));
            copies.add(copyToSave);
            return copyToSave;
        }

        @Override
        public Optional<BookCopy> findById(Long id) {
            return copies.stream().filter(c -> c.getId() != null && c.getId().equals(id)).findFirst();
        }
    }
}