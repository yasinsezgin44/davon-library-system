package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {
    private FineService fineService;
    private Loan overdueLoan;
    private BookCopy bookCopy;
    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        fineService = new FineService();

        // Create test objects
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        bookCopy = BookCopy.builder()
                .id(1L)
                .book(book)
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .condition("Good")
                .build();

        member = Member.builder()
                .id(1L)
                .fullName("Test Member")
                .active(true)
                .build();

        // Create an overdue loan
        overdueLoan = Loan.builder()
                .id(1L)
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now().minusDays(30)) // Checked out 30 days ago
                .dueDate(LocalDate.now().minusDays(10)) // Due 10 days ago
                .status(Loan.LoanStatus.OVERDUE)
                .build();
    }

    @Test
    void testCalculateOverdueFine() {
        // Test with overdue loan
        Fine fine = fineService.calculateOverdueFine(overdueLoan);

        assertNotNull(fine);
        assertEquals(Fine.FineReason.OVERDUE, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
        assertEquals(LocalDate.now(), fine.getIssueDate());
        assertEquals(LocalDate.now().plusDays(14), fine.getDueDate());

        // 10 days overdue at $0.50 per day = $5.00
        assertEquals(5.00, fine.getAmount());

        // Test with non-overdue loan
        Loan activeLoan = Loan.builder()
                .status(Loan.LoanStatus.ACTIVE)
                .build();

        Fine noFine = fineService.calculateOverdueFine(activeLoan);
        assertNull(noFine);
    }

    @Test
    void testCreateDamageFine() {
        String damageDescription = "Water damage on pages 10-20";
        Fine fine = fineService.createDamageFine(bookCopy, damageDescription);

        assertNotNull(fine);
        assertEquals(Fine.FineReason.DAMAGED_ITEM, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
        assertEquals(LocalDate.now(), fine.getIssueDate());
        assertEquals(LocalDate.now().plusDays(14), fine.getDueDate());

        // Default amount from service is $10.00
        assertEquals(10.00, fine.getAmount());
    }

    @Test
    void testCreateLostItemFine() {
        Fine fine = fineService.createLostItemFine(bookCopy);

        assertNotNull(fine);
        assertEquals(Fine.FineReason.LOST_ITEM, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
        assertEquals(LocalDate.now(), fine.getIssueDate());
        assertEquals(LocalDate.now().plusDays(14), fine.getDueDate());

        // Default replacement cost from service is $25.00
        assertEquals(25.00, fine.getAmount());
    }

    @Test
    void testMaxFineLimit() {
        // Create a loan that's 100 days overdue (which would exceed the max fine)
        Loan veryOverdueLoan = Loan.builder()
                .id(2L)
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now().minusDays(120))
                .dueDate(LocalDate.now().minusDays(100))
                .status(Loan.LoanStatus.OVERDUE)
                .build();

        Fine fine = fineService.calculateOverdueFine(veryOverdueLoan);

        assertNotNull(fine);
        // 100 days * $0.50 = $50, but max is $25
        assertEquals(25.00, fine.getAmount());
    }
}