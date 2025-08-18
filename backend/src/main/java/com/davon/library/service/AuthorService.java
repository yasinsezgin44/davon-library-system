package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.repository.AuthorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import com.davon.library.dto.AuthorDTO;
import com.davon.library.mapper.AuthorMapper;

@ApplicationScoped
public class AuthorService {

    @Inject
    AuthorRepository authorRepository;

    @Transactional
    public Author createAuthor(AuthorDTO authorDTO) {
        var author = AuthorMapper.toEntity(authorDTO);
        authorRepository.persist(author);
        return author;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.listAll();
    }
}
