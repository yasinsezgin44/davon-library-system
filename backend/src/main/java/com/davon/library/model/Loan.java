package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a loan (borrowing transaction) in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Loan extends BaseEntity {
    private Member member;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
}