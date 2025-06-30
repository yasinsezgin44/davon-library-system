package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a receipt for a transaction in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Receipt extends BaseEntity {
    private long transactionId;
    private LocalDate issueDate;
    private ReceiptItem[] items;
    private double total;

    public void print() {
    }

    public void email() {
    }

    @Data
    @AllArgsConstructor
    public static class ReceiptItem {
        private String description;
        private double amount;
        private int quantity;
    }
}