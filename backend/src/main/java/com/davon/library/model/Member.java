package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a library member who can borrow books.
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
}