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
public class Loan extends BaseEntity {
    private Long id;
    private Member member;
    private BookCopy bookCopy;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private int renewalCount;

    public double calculateLateFees() {
        // Placeholder: implement fee calculation logic
        return 0.0;
    }

    public boolean renew() {
        // Placeholder: implement renewal logic
        return false;
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