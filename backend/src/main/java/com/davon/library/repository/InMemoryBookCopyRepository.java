package com.davon.library.repository;

import com.davon.library.model.BookCopy;
import com.davon.library.service.BookCopyRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBookCopyRepository implements BookCopyRepository {
    private final Map<Long, BookCopy> copies = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public BookCopy save(BookCopy copy) {
        if (copy.getId() == null) {
            copy.setId(nextId++);
        }
        copies.put(copy.getId(), copy);
        return copy;
    }
}
