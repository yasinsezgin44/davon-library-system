package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a fine in the library system.
 */
@Entity
@Table(name = "fines")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "member" })
@ToString(callSuper = true, exclude = { "member" })
public class Fine extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private FineReason reason;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
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