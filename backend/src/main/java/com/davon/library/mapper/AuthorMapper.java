package com.davon.library.mapper;

import com.davon.library.dto.AuthorResponseDTO;
import com.davon.library.model.Author;

public class AuthorMapper {

    public static AuthorResponseDTO toResponseDTO(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                author.getBirthDate(),
                author.getCreatedAt(),
                author.getUpdatedAt()
        );
    }
}
