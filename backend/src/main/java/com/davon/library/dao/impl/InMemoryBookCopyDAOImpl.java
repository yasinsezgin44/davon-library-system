package com.davon.library.dao.impl;

import com.davon.library.dao.BookCopyDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.BookCopy;
import com.davon.library.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookCopyDAO.
 * Thread-safe implementation using ConcurrentHashMap.
 */
@ApplicationScoped
public class InMemoryBookCopyDAOImpl extends AbstractInMemoryDAO<BookCopy> implements BookCopyDAO {

    @Override
    protected String getEntityName() {
        return "BookCopy";
    }

    @Override
    protected BookCopy cloneEntity(BookCopy entity) {
        if (entity == null) {
            return null;
        }

        return BookCopy.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .book(entity.getBook()) // Shallow copy for now
                .acquisitionDate(entity.getAcquisitionDate())
                .condition(entity.getCondition())
                .status(entity.getStatus())
                .location(entity.getLocation())
                .build();
    }

    @Override
    protected void validateEntity(BookCopy entity) throws DAOException {
        super.validateEntity(entity);

        if (entity.getBook() == null) {
            throw new DAOException("BookCopy must have a book", "validate", getEntityName());
        }

        if (entity.getStatus() == null) {
            throw new DAOException("BookCopy must have a status", "validate", getEntityName());
        }
    }

    @Override
    public List<BookCopy> findByBook(Book book) {
        if (book == null || book.getId() == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(copy -> copy.getBook() != null &&
                        book.getId().equals(copy.getBook().getId()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopy> findAvailableByBook(Book book) {
        return findByBook(book).stream()
                .filter(copy -> copy.getStatus() == BookCopy.CopyStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookCopy> findByStatus(BookCopy.CopyStatus status) {
        if (status == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(copy -> status.equals(copy.getStatus()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookCopy> findByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return Optional.empty();
        }

        // Note: BookCopy model doesn't have barcode field yet
        // This implementation assumes it might be added later
        // For now, we'll use the ID as a barcode substitute
        try {
            Long id = Long.parseLong(barcode);
            return findById(id);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public long countAvailableByBook(Book book) {
        return findAvailableByBook(book).size();
    }

    @Override
    public List<BookCopy> findByLocation(String location) {
        if (location == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(copy -> location.equals(copy.getLocation()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }
}