package com.davon.library.model;

import lombok.*;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an author in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "books" })
@ToString(callSuper = true, exclude = { "books" })
public class Author extends BaseEntity {
    private String name;
    private String biography;
    private LocalDate birthDate;

    @Builder.Default
    private Set<Book> books = new HashSet<>();

    public Set<Book> getBooks() {
        return books;
    }
}