package com.davon.library.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "authors", "copies" })
public class Book {

    private static final String DEFAULT_IMAGE_URL = "/images/default_book_image.jpeg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    public String getCoverImageUrl() {
        return (this.coverImage == null || this.coverImage.isBlank()) ? DEFAULT_IMAGE_URL : this.coverImage;
    }

    private Integer pages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    public String getAuthor() {
        if (authors == null || authors.isEmpty()) {
            return "Unknown Author";
        }
        return authors.stream().map(Author::getName).collect(Collectors.joining(", "));
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonIgnoreProperties("book")
    private Set<BookCopy> copies = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Book() {
        this.authors = new HashSet<>();
        this.copies = new HashSet<>();
    }

    public Book(Long id, String title, String isbn, Integer publicationYear, String description,
                String coverImage, Integer pages, Publisher publisher, Category category,
                Set<Author> authors, Set<BookCopy> copies, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.description = description;
        this.coverImage = coverImage;
        this.pages = pages;
        this.publisher = publisher;
        this.category = category;
        this.authors = authors;
        this.copies = copies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
