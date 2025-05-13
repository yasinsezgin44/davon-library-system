package com.davon.library.service;

import com.davon.library.model.BookCopy;
import java.util.Optional;

public interface BookCopyRepository {
    BookCopy save(BookCopy copy);

    Optional<BookCopy> findById(Long id);
}