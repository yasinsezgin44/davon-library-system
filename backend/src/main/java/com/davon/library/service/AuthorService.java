package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.repository.AuthorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AuthorService {

    @Inject
    AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.listAll();
    }
}
