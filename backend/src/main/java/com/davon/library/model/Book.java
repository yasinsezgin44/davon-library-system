package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
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
    private String ISBN;
    private int publicationYear;
    private String description;
    private String coverImage;
    private int pages;
    private Set<Author> authors = new HashSet<>();
    private Publisher publisher;
    private Category category;

    // Methods from diagram
    public int getAvailableCopies() {
        // This would typically query BookCopy or Inventory
        return 0; // Placeholder
    }

    public BookDetails getDetails() {
        // Return a details object (to be defined)
        return null; // Placeholder
    }

    public boolean validateISBN() {
        // Implement ISBN validation logic
        return ISBN != null && (ISBN.length() == 10 || ISBN.length() == 13);
    }

    public boolean isAvailable() {
        return getAvailableCopies() > 0;
    }

    public boolean validateMetadata() {
        // Implement metadata validation logic
        return title != null && !title.isEmpty() && publicationYear > 0;
    }

    // Placeholder for BookDetails class
    @Data
    @AllArgsConstructor
    public static class BookDetails {
        private String title;
        private String ISBN;
        private int publicationYear;
        private String description;
        private String coverImage;
        private int pages;
        private Set<Author> authors;
        private Publisher publisher;
        private Category category;
    }
}