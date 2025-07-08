package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class FineService {
    private static final double DAILY_RATE = 0.50; // 50 cents per day
    private static final double MAX_FINE = 25.00; // Maximum fine amount

    public Fine calculateOverdueFine(Loan loan) {
        if (loan == null || loan.getDueDate() == null || loan.getMember() == null) {
            return null;
        }

        LocalDate dueDate = loan.getDueDate();
        LocalDate currentDate = LocalDate.now();

        // If not actually overdue, return null
        if (!currentDate.isAfter(dueDate)) {
            return null;
        }

        // Calculate overdue days from the due date to the current date
        int overdueDays = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
        double amount = Math.min(overdueDays * DAILY_RATE, MAX_FINE);

        return Fine.builder()
                .member(loan.getMember())
                .amount(amount)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(currentDate)
                .dueDate(currentDate.plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    public Fine createDamageFine(Member member, BookCopy bookCopy, String damageDescription) {
        if (member == null || bookCopy == null) {
            return null;
        }

        double amount = calculateDamageAmount(bookCopy.getCondition(), damageDescription);
        LocalDate currentDate = LocalDate.now();

        return Fine.builder()
                .member(member)
                .amount(amount)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(currentDate)
                .dueDate(currentDate.plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    public Fine createLostItemFine(Member member, BookCopy bookCopy) {
        if (member == null || bookCopy == null) {
            return null;
        }

        // Calculate replacement cost based on book value
        double replacementCost = 25.00; // Example default value
        LocalDate currentDate = LocalDate.now();

        return Fine.builder()
                .member(member)
                .amount(replacementCost)
                .reason(Fine.FineReason.LOST_ITEM)
                .issueDate(currentDate)
                .dueDate(currentDate.plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    private double calculateDamageAmount(String condition, String damageDescription) {
        // Implement logic to determine fine amount based on damage severity
        return 10.00; // Default amount
    }
}
