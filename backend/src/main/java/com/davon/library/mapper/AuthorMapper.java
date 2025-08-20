package com.davon.library.mapper;

import com.davon.library.dto.AuthorDTO;
import com.davon.library.model.Author;
import com.davon.library.dto.AuthorResponseDTO;

public class AuthorMapper {

    public static AuthorDTO toDTO(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                author.getBirthDate());
    }

    public static Author toEntity(AuthorDTO authorDTO) {
        if (authorDTO == null) {
            return null;
        }
        Author author = new Author();
        author.setId(authorDTO.id);
        author.setName(authorDTO.name);
        author.setBiography(authorDTO.biography);
        author.setBirthDate(authorDTO.dateOfBirth);
        return author;
    }

    public static AuthorResponseDTO toResponseDTO(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorResponseDTO(
                author.getId(),
                author.getName(),
                author.getBiography(),
                author.getBirthDate(),
                null,
                null);
    }
}
