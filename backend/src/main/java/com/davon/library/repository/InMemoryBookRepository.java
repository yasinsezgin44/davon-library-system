package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.service.BookRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory implementation of the BookRepository interface.
 */
public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> books = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(nextId++);
        }
        books.put(book.getId(), book);
        return book;
    }

    @Override
    public void delete(Book book) {
        books.remove(book.getId());
    }

    @Override
    public Book findById(Long id) {
        return books.get(id);
    }
}