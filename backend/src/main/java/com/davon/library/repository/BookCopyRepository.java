package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookCopyRepository implements PanacheRepository<BookCopy> {

    public Optional<BookCopy> findAvailableByBook(Book book) {
        return find("book = ?1 and status = 'AVAILABLE'", book).firstResultOptional();
    }

    public List<BookCopy> findByBook(Book book) {
        return find("book", book).list();
    }
}
