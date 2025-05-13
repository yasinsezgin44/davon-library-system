package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a fine in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Fine extends BaseEntity {
    private double amount;
    private FineReason reason;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private FineStatus status;

    public Transaction pay() {
        this.status = FineStatus.PAID;

        Transaction transaction = Transaction.builder()
                .amount(this.amount)
                .date(LocalDate.now())
                .type(Transaction.TransactionType.FINE_PAYMENT)
                .description("Payment for fine ID " + this.getId())
                .paymentMethod("Cash") // Default
                .build();

        return transaction;
    }

    public void waive() {
        this.status = FineStatus.WAIVED;
    }

    public void adjustAmount(double newAmount) {
        this.amount = newAmount;
    }

    public boolean disputeFine(String reason) {
        // Logic to handle dispute
        this.status = FineStatus.DISPUTED;
        return true;
    }

    public enum FineReason {
        OVERDUE, DAMAGED_ITEM, LOST_ITEM, ADMINISTRATIVE
    }

    public enum FineStatus {
        PENDING, PAID, WAIVED, DISPUTED
    }
}