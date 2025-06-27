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
@EqualsAndHashCode(callSuper = true, exclude = { "authors" })
@ToString(callSuper = true, exclude = { "authors" })
public class Book extends BaseEntity {
    @jakarta.validation.constraints.NotBlank
    private String title;

    /**
     * ISBN-10 or ISBN-13 allowed (10 or 13 digits). No hyphens/spaces for
     * simplicity.
     */
    @jakarta.validation.constraints.Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
    private String ISBN;

    @jakarta.validation.constraints.Min(value = 1, message = "Publication year must be positive")
    private int publicationYear;
    private String description;
    private String coverImage;
    private int pages;
    @lombok.Builder.Default
    private Set<Author> authors = new HashSet<>();
    private Publisher publisher;
    private Category category;

    // Methods from diagram
    public int getAvailableCopies() {
        // This would typically query BookCopy or Inventory
        return 0; // Placeholder
    }

    public BookDetails getDetails() {
        return new BookDetails(
                this.title,
                this.ISBN,
                this.publicationYear,
                this.description,
                this.coverImage,
                this.pages,
                this.authors,
                this.publisher,
                this.category);
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