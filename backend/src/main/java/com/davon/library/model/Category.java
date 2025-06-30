package com.davon.library.model;

import lombok.*;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a category for books in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "books" })
@ToString(callSuper = true, exclude = { "books" })
public class Category extends BaseEntity {
    private String name;
    private String description;

    @Builder.Default
    private Set<Book> books = new HashSet<>();

    public Set<Book> getBooks() {
        return books;
    }
}