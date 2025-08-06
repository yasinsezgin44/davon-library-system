package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.model.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends PanacheRepository<Book> {

    default Optional<Book> findByIsbn(String isbn) {
        return find("isbn", isbn).firstResultOptional();
    }

    default List<Book> findByTitleContaining(String title) {
        return list("LOWER(title) LIKE LOWER(?1)", "%" + title + "%");
    }

    default List<Book> findByCategory(Category category) {
        return list("category", category);
    }

    default List<Book> findByPublicationYear(int year) {
        return list("publicationYear", year);
    }

    default List<Book> findAvailableBooks() {
        return list("SELECT b FROM Book b JOIN b.copies c WHERE c.status = 'AVAILABLE'");
    }

    default List<Book> search(String query) {
        return list("LOWER(title) LIKE :query OR LOWER(isbn) LIKE :query OR authors.name LIKE :query",
                Parameters.with("query", "%" + query.toLowerCase() + "%"));
    }
}
