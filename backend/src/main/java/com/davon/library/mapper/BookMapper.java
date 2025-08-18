package com.davon.library.mapper;

import com.davon.library.dto.BookResponseDTO;
import com.davon.library.model.Book;
import com.davon.library.dto.BookRequestDTO;
import com.davon.library.model.Author;

public class BookMapper {

    public static Book toEntity(BookRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(dto.title());
        book.setIsbn(dto.isbn());
        book.setPublicationDate(dto.publicationDate());
        book.setGenre(dto.genre());
        book.setLanguage(dto.language());
        book.setCoverImageUrl(dto.coverImageUrl());

        return book;
    }

    public static BookResponseDTO toResponseDTO(Book book) {
        if (book == null) {
            return null;
        }
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublisher().getName(),
                book.getPublicationDate(),
                book.getGenre(),
                book.getLanguage(),
                book.getCoverImageUrl());
    }
}
