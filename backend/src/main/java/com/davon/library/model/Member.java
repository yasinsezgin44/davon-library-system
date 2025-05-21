package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Represents a library member who can borrow books and make reservations.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = { "loans", "reservations", "fines" })
public class Member extends User {
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String address;
    @lombok.Builder.Default
    private Set<Loan> loans = new HashSet<>();
    @lombok.Builder.Default
    private Set<Reservation> reservations = new HashSet<>();
    @lombok.Builder.Default
    private double fineBalance = 0.0;
    @lombok.Builder.Default
    private Set<Fine> fines = new HashSet<>();

    public boolean borrowBooks(List<Long> bookIds) {
        // Implementation
        return true;
    }

    public boolean renewMembership(int durationMonths) {
        this.membershipEndDate = this.membershipEndDate.plusMonths(durationMonths);
        return true;
    }

    public boolean payFines(double amount) {
        if (amount <= 0 || amount > fineBalance) {
            return false;
        }
        fineBalance -= amount;
        return true;
    }

    public void addFine(double amount) {
        if (amount > 0) {
            fineBalance += amount;
        }
    }

    public double getFineBalance() {
        return fineBalance;
    }

    public Set<Fine> getFines() {
        return fines;
    }
}