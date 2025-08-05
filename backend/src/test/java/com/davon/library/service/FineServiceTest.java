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
                member = Member.builder()
                                .id(1L)
                                .fullName("Test Member")
                                .active(true)
                                .build();

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

                // Create an overdue loan with proper member reference
                // Due 10 days ago
                overdueLoan = Loan.builder()
                                .id(1L)
                                .member(member)
                                .bookCopy(bookCopy)
                                .checkoutDate(LocalDate.now().minusDays(30))
                                .dueDate(LocalDate.now().minusDays(10))
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
                                .member(member)
                                .status(Loan.LoanStatus.ACTIVE)
                                .checkoutDate(LocalDate.now().minusDays(5))
                                .dueDate(LocalDate.now().plusDays(5)) // Due in 5 days
                                .build();

                Fine noFine = fineService.calculateOverdueFine(activeLoan);
                assertNull(noFine);

                // Test with null loan
                assertNull(fineService.calculateOverdueFine(null));

                // Test with loan having null due date
                Loan loanWithNullDueDate = Loan.builder()
                                .member(member)
                                .status(Loan.LoanStatus.ACTIVE)
                                .checkoutDate(LocalDate.now())
                                .build();
                assertNull(fineService.calculateOverdueFine(loanWithNullDueDate));
        }

        @Test
        void testCreateDamageFine() {
                String damageDescription = "Water damage on pages 10-20";
                Fine fine = fineService.createDamageFine(member, bookCopy, damageDescription);

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
                Fine fine = fineService.createLostItemFine(member, bookCopy);

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

        @Test
        void testOverdueFineAcrossMonths() {
                // Create a loan that is 36 days overdue
                Loan crossMonthLoan = Loan.builder()
                                .id(3L)
                                .member(member)
                                .bookCopy(bookCopy)
                                .checkoutDate(LocalDate.now().minusDays(51))
                                .dueDate(LocalDate.now().minusDays(36))
                                .status(Loan.LoanStatus.OVERDUE)
                                .build();

                Fine fine = fineService.calculateOverdueFine(crossMonthLoan);

                assertNotNull(fine);
                assertEquals(18.00, fine.getAmount(),
                                "Fine should be $18.00 for 36 days overdue, but got $" + fine.getAmount());
        }

        @Test
        void testNullInputHandling() {
                // Test null member
                assertNull(fineService.createDamageFine(null, bookCopy, "damage"));
                assertNull(fineService.createLostItemFine(null, bookCopy));

                // Test null bookCopy
                assertNull(fineService.createDamageFine(member, null, "damage"));
                assertNull(fineService.createLostItemFine(member, null));

                // Test loan with null member
                Loan loanWithNullMember = Loan.builder()
                                .id(1L)
                                .bookCopy(bookCopy)
                                .checkoutDate(LocalDate.now().minusDays(30))
                                .dueDate(LocalDate.now().minusDays(10))
                                .status(Loan.LoanStatus.OVERDUE)
                                .build();
                assertNull(fineService.calculateOverdueFine(loanWithNullMember));
        }
}