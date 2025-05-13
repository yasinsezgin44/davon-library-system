package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.service.BookCopyRepository;
import com.davon.library.service.BookRepository;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BookLabelService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    // Generate labels based on book copy information
    public String generateLabel(Long bookCopyId) {
        BookCopy copy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy not found"));
        Book book = copy.getBook();

        return String.format("%s\n%s\n%s\nID: %d\nLoc: %s",
                book.getTitle(),
                book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")),
                book.getISBN(),
                copy.getId(),
                copy.getLocation());
    }
}
