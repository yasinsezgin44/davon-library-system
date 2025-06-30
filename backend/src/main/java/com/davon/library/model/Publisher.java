package com.davon.library.model;

import lombok.*;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a publisher in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "publishedBooks" })
@ToString(callSuper = true, exclude = { "publishedBooks" })
public class Publisher extends BaseEntity {
    private String name;
    private String address;
    private String contact;

    @Builder.Default
    private Set<Book> publishedBooks = new HashSet<>();

    public Set<Book> getPublishedBooks() {
        return publishedBooks;
    }
}