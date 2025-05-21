package com.davon.library.repository;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLoanHistoryRepositoryTest {
    private InMemoryLoanHistoryRepository repository;
    private Member member;
    private Book book;
    private Loan loan;
    private LoanHistory loanHistory;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLoanHistoryRepository();
        member = new Member();
        member.setId(1L);
        book = new Book();
        book.setId(1L);
        loan = new Loan();
        loan.setId(1L);
        loanHistory = new LoanHistory();
        loanHistory.setMember(member);
        loanHistory.setBook(book);
        loanHistory.setLoan(loan);
        loanHistory.setActionDate(LocalDate.now());
        loanHistory.setAction("CHECKOUT");
    }

    @Test
    void testSaveAndFindByMember() {
        LoanHistory saved = repository.save(loanHistory);
        assertNotNull(saved.getId());
        List<LoanHistory> found = repository.findByMember(member);
        assertEquals(1, found.size());
        assertEquals(saved, found.get(0));
    }

    @Test
    void testFindByMemberNone() {
        List<LoanHistory> found = repository.findByMember(member);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindActiveLoansReturnsEmpty() {
        List<Loan> activeLoans = repository.findActiveLoans(member);
        assertTrue(activeLoans.isEmpty());
    }

    @Test
    void testFindPastLoansReturnsEmpty() {
        List<Loan> pastLoans = repository.findPastLoans(member);
        assertTrue(pastLoans.isEmpty());
    }

    @Test
    void testCalculateAverageReturnTimeReturnsZero() {
        float avg = repository.calculateAverageReturnTime(member);
        assertEquals(0f, avg);
    }
}