package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.repository.AuthorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AuthorService {

    @Inject
    AuthorRepository authorRepository;

    @Transactional
    public Author createAuthor(Author author) {
        authorRepository.persist(author);
        return author;
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.listAll();
    }
}
