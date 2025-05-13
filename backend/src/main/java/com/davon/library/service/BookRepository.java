package com.davon.library.service;

import com.davon.library.model.Book;

/**
 * Repository interface for Book entity.
 */
public interface BookRepository {
    Book save(Book book);

    void delete(Book book);

    Book findById(Long id);
}