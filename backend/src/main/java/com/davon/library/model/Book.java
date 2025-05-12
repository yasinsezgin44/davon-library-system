package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a book in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {
    private String title;
    private String isbn;
    private LocalDate publicationDate;
    private String publisher;
    private int totalCopies;
    private int availableCopies;
    private String genre;
    private String summary;
    private Set<Author> authors = new HashSet<>();
}