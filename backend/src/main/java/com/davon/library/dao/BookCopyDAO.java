package com.davon.library.dao;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Book;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for BookCopy entities.
 * Follows the DAO pattern for data layer abstraction.
 */
public interface BookCopyDAO extends BaseDAO<BookCopy, Long> {

    /**
     * Find all copies of a specific book.
     * 
     * @param book the book
     * @return list of book copies
     */
    List<BookCopy> findByBook(Book book);

    /**
     * Find available copies of a specific book.
     * 
     * @param book the book
     * @return list of available book copies
     */
    List<BookCopy> findAvailableByBook(Book book);

    /**
     * Find copies by status.
     * 
     * @param status the copy status
     * @return list of book copies with the specified status
     */
    List<BookCopy> findByStatus(BookCopy.CopyStatus status);

    /**
     * Find copy by barcode.
     * 
     * @param barcode the barcode
     * @return optional book copy
     */
    Optional<BookCopy> findByBarcode(String barcode);

    /**
     * Count available copies for a book.
     * 
     * @param book the book
     * @return number of available copies
     */
    long countAvailableByBook(Book book);

    /**
     * Find copies by location.
     * 
     * @param location the location
     * @return list of book copies at the specified location
     */
    List<BookCopy> findByLocation(String location);
}