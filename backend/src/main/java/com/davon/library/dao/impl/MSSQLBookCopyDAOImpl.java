package com.davon.library.dao.impl;

import com.davon.library.dao.BookCopyDAO;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLBookCopyDAOImpl implements BookCopyDAO {
    @Override
    public Optional<BookCopy> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        return bookCopy;
    }

    @Override
    public List<BookCopy> findByLocation(String location) {
        return List.of();
    }

    @Override
    public List<BookCopy> findByStatus(BookCopy.CopyStatus status) {
        return List.of();
    }

    @Override
    public BookCopy update(BookCopy bookCopy) {
        return bookCopy;
    }

    @Override
    public void deleteById(Long id) {
        // No-op for now
    }

    @Override
    public long countAvailableByBook(Book book) {
        return 0;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Optional<BookCopy> findByBarcode(String barcode) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<BookCopy> findAvailableByBook(Book book) {
        return List.of();
    }

    @Override
    public List<BookCopy> findAll() {
        return List.of();
    }

    @Override
    public List<BookCopy> findByBook(Book book) {
        return List.of();
    }

    @Override
    public void delete(BookCopy bookCopy) {
        // No-op for now
    }
}