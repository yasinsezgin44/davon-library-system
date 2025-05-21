package com.davon.library.repository;

import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLoanRepositoryTest {
    private InMemoryLoanRepository repository;
    private Member member;
    private BookCopy bookCopy;
    private Loan loan;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLoanRepository();
        member = new Member();
        member.setId(1L);
        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now().minusDays(10));
        loan.setDueDate(LocalDate.now().minusDays(2));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
    }

    @Test
    void testSaveAndFindById() {
        Loan saved = repository.save(loan);
        assertNotNull(saved.getId());
        Optional<Loan> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved, found.get());
    }

    @Test
    void testFindActiveLoanByMemberAndBookCopy() {
        repository.save(loan);
        Optional<Loan> found = repository.findActiveLoanByMemberAndBookCopy(member, bookCopy);
        assertTrue(found.isPresent());
        assertEquals(loan, found.get());
    }

    @Test
    void testFindAllByMember() {
        repository.save(loan);
        List<Loan> loans = repository.findAllByMember(member);
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
    }

    @Test
    void testFindOverdueLoans() {
        repository.save(loan);
        List<Loan> overdue = repository.findOverdueLoans(LocalDate.now());
        assertEquals(1, overdue.size());
        assertEquals(loan, overdue.get(0));
    }

    @Test
    void testFindOverdueLoansNone() {
        loan.setDueDate(LocalDate.now().plusDays(2));
        repository.save(loan);
        List<Loan> overdue = repository.findOverdueLoans(LocalDate.now());
        assertTrue(overdue.isEmpty());
    }
}