package com.davon.library.service;

import com.davon.library.model.BookCopy;

public interface BookCopyRepository {
    BookCopy save(BookCopy copy);
}