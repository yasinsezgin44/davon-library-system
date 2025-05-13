package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a payment or fee transaction in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {
    private LocalDate date;
    private double amount;
    private TransactionType type;
    private String description;
    private String paymentMethod;

    public Receipt generateReceipt() {
        Receipt.ReceiptItem[] items = {
                new Receipt.ReceiptItem(this.description, this.amount, 1)
        };

        return Receipt.builder()
                .transactionId(this.getId())
                .issueDate(LocalDate.now())
                .items(items)
                .total(this.amount)
                .build();
    }

    public boolean voidTransaction() {
        // Logic to void transaction
        return true;
    }

    public boolean recoverFailedTransaction() {
        // Logic to recover failed transaction
        return true;
    }

    public enum TransactionType {
        FINE_PAYMENT, MEMBERSHIP_FEE, LOST_ITEM_FEE, RESERVATION_FEE, REFUND
    }
}