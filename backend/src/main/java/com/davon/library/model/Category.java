package com.davon.library.model;

import lombok.*;
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
public class Category extends BaseEntity {
    private String name;
    private String description;
    private Set<Book> books = new HashSet<>();

    public Set<Book> getBooks() {
        return books;
    }
}