package com.davon.library.mapper;

import com.davon.library.dto.AuthorDTO;
import com.davon.library.model.Author;

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
}
