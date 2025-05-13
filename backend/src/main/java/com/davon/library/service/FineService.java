package com.davon.library.service;

import com.davon.library.model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FineService {
    private static final double DAILY_RATE = 0.50; // 50 cents per day
    private static final double MAX_FINE = 25.00; // Maximum fine amount

    public Fine calculateOverdueFine(Loan loan) {
        if (loan.getStatus() != Loan.LoanStatus.OVERDUE) {
            return null;
        }

        int overdueDays = loan.getOverdueDays();
        double amount = Math.min(overdueDays * DAILY_RATE, MAX_FINE);

        return Fine.builder()
                .amount(amount)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    public Fine createDamageFine(BookCopy bookCopy, String damageDescription) {
        double amount = calculateDamageAmount(bookCopy.getCondition(), damageDescription);

        return Fine.builder()
                .amount(amount)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    public Fine createLostItemFine(BookCopy bookCopy) {
        // Calculate replacement cost based on book value
        double replacementCost = 25.00; // Example default value

        return Fine.builder()
                .amount(replacementCost)
                .reason(Fine.FineReason.LOST_ITEM)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    private double calculateDamageAmount(String condition, String damageDescription) {
        // Implement logic to determine fine amount based on damage severity
        return 10.00; // Default amount
    }
}
