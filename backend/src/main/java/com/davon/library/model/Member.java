package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a library member who can borrow books and make reservations.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends User {
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String address;
    private Set<Loan> loans = new HashSet<>();
    private Set<Reservation> reservations = new HashSet<>();
}