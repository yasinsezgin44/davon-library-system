package com.davon.library.repository;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * Repository for BookCopy entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based BookCopyDAO implementation.
 */
@ApplicationScoped
public class BookCopyRepository implements PanacheRepository<BookCopy> {

    /**
     * Finds all copies of a specific book.
     * 
     * @param book the book
     * @return list of copies for the book
     */
    public List<BookCopy> findByBook(Book book) {
        return find("book", book).list();
    }

    /**
     * Finds available copies of a specific book.
     * 
     * @param book the book
     * @return list of available copies
     */
    public List<BookCopy> findAvailableByBook(Book book) {
        return find("book = ?1 AND status = ?2", book, BookCopy.CopyStatus.AVAILABLE).list();
    }

    /**
     * Finds copies by status.
     * 
     * @param status the copy status
     * @return list of copies with the given status
     */
    public List<BookCopy> findByStatus(BookCopy.CopyStatus status) {
        return find("status", status).list();
    }

    /**
     * Finds copies by location.
     * 
     * @param location the location
     * @return list of copies at the given location
     */
    public List<BookCopy> findByLocation(String location) {
        return find("location", location).list();
    }

    /**
     * Finds the first available copy of a book.
     * 
     * @param book the book
     * @return first available copy if found, empty otherwise
     */
    public Optional<BookCopy> findFirstAvailableByBook(Book book) {
        return find("book = ?1 AND status = ?2", book, BookCopy.CopyStatus.AVAILABLE)
                .firstResultOptional();
    }

    /**
     * Counts available copies of a book.
     * 
     * @param book the book
     * @return count of available copies
     */
    public long countAvailableByBook(Book book) {
        return find("book = ?1 AND status = ?2", book, BookCopy.CopyStatus.AVAILABLE).count();
    }

    /**
     * Counts total copies of a book.
     * 
     * @param book the book
     * @return total count of copies
     */
    public long countByBook(Book book) {
        return find("book", book).count();
    }
}