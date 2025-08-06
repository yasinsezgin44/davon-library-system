package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.enums.CopyStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public interface BookCopyRepository extends PanacheRepository<BookCopy> {

    default Optional<BookCopy> findAvailableByBook(Book book) {
        return find("book = ?1 and status = ?2", book, CopyStatus.AVAILABLE).firstResultOptional();
    }

    default List<BookCopy> findByBook(Book book) {
        return list("book", book);
    }
}
