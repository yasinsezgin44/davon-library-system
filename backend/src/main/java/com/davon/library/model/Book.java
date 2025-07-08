package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a book in the library system.
 */
@Entity
@Table(name = "books")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "authors", "copies" })
@ToString(callSuper = true, exclude = { "authors", "copies" })
public class Book extends BaseEntity {
    @jakarta.validation.constraints.NotBlank
    private String title;

    /**
     * ISBN-10 or ISBN-13 allowed (10 or 13 digits). No hyphens/spaces for
     * simplicity.
     */
    @jakarta.validation.constraints.Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
    @Column(name = "isbn", unique = true, nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("ISBN")
    private String ISBN;

    @jakarta.validation.constraints.Min(value = 1, message = "Publication year must be positive")
    private int publicationYear;
    private String description;
    private String coverImage;
    private int pages;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @JsonIgnoreProperties("books") // Ignore the books property of Author to prevent circular reference
    @lombok.Builder.Default
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular reference in JSON serialization
    @lombok.Builder.Default
    private Set<BookCopy> copies = new HashSet<>();

    // Methods from diagram
    public int getAvailableCopies() {
        // This would typically query BookCopy or Inventory
        return 0; // Placeholder
    }

    @JsonIgnore // Prevent Jackson from calling this method during serialization
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
        @JsonIgnore // Prevent circular reference in nested object
        private Set<Author> authors;
        private Publisher publisher;
        private Category category;
    }
}