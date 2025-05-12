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

    public void pay() {
    }

    public void adjustAmount(double newAmount) {
        this.amount = newAmount;
    }

    public boolean dispute(String reason) {
        return false;
    }

    public enum FineReason {
        OVERDUE, DAMAGED_ITEM, LOST_ITEM, ADMINISTRATIVE
    }

    public enum FineStatus {
        PENDING, PAID, WAIVED, DISPUTED
    }
}