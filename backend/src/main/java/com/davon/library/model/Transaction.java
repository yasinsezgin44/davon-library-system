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
public class Transaction extends BaseEntity {
    private LocalDate date;
    private double amount;
    private TransactionType type;
    private String description;
    private String paymentMethod;

    public Receipt generateReceipt() {
        return null;
    }

    public boolean voidTransaction() {
        return false;
    }

    public boolean recoverFailedTransaction() {
        return false;
    }

    public enum TransactionType {
        FINE_PAYMENT, MEMBERSHIP_FEE, LOST_ITEM_FEE, RESERVATION_FEE, REFUND
    }
}