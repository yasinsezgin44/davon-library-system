package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a loan (borrowing transaction) in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = { "member", "bookCopy" })
public class Loan extends BaseEntity {
    private Member member;
    private BookCopy bookCopy;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private int renewalCount;

    private static final double DAILY_FINE_RATE = 0.25; // $0.25 per day
    private static final int MAX_RENEWAL_COUNT = 2;

    public double calculateLateFees() {
        int overdueDays = getOverdueDays();
        if (overdueDays <= 0) {
            return 0.0;
        }
        return overdueDays * DAILY_FINE_RATE;
    }

    public boolean renew() {
        // Check if renewal is allowed
        if (this.status != LoanStatus.ACTIVE) {
            return false; // Only active loans can be renewed
        }

        if (this.renewalCount >= MAX_RENEWAL_COUNT) {
            return false; // Maximum renewals reached
        }

        if (this.member != null && this.member.getFineBalance() > 0) {
            return false; // Cannot renew with outstanding fines
        }

        // Check if the loan is not overdue
        if (LocalDate.now().isAfter(this.dueDate)) {
            return false; // Cannot renew overdue loans
        }

        // Perform renewal
        this.dueDate = this.dueDate.plusDays(14); // Extend by 14 days
        this.renewalCount++;

        return true;
    }

    public void returnBook() {
        this.status = LoanStatus.RETURNED;
        this.returnDate = LocalDate.now();
    }

    public int getOverdueDays() {
        if (returnDate != null && dueDate != null && returnDate.isAfter(dueDate)) {
            return (int) (returnDate.toEpochDay() - dueDate.toEpochDay());
        }
        if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            return (int) (LocalDate.now().toEpochDay() - dueDate.toEpochDay());
        }
        return 0;
    }

    public enum LoanStatus {
        ACTIVE,
        OVERDUE,
        RETURNED,
        LOST
    }
}