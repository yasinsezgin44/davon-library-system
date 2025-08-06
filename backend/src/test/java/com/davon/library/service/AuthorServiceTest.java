package com.davon.library.service;

import com.davon.library.model.Author;
import com.davon.library.repository.AuthorRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AuthorServiceTest {

    @Inject
    AuthorService authorService;

    @Inject
    AuthorRepository authorRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateAuthor() {
        Author author = new Author();
        author.setName("Test Author");
        authorService.createAuthor(author);
        assertNotNull(author.getId());
        assertEquals(1, authorRepository.count());
    }

    @Test
    @Transactional
    void testGetAuthorById() {
        Author author = new Author();
        author.setName("Test Author");
        authorRepository.persist(author);

        Author found = authorService.getAuthorById(author.getId());
        assertNotNull(found);
        assertEquals("Test Author", found.getName());
    }
}
