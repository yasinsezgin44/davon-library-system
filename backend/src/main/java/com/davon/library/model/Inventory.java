package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents the inventory record for a book in the library.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {
    private Book book;
    private int totalCopies;
    private int availableCopies;
    private String location; // e.g., shelf or section
}