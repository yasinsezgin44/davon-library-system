package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    PublisherRepository publisherRepository;

    @Inject
    CategoryRepository categoryRepository;

    private Author author;
    private Publisher publisher;
    private Category category;
    private Book testBook1;

    @BeforeEach
    @Transactional
    void setUp() {
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        categoryRepository.deleteAll();

        author = new Author();
        author.setName("Test Author");
        authorRepository.persist(author);

        publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherRepository.persist(publisher);

        category = new Category();
        category.setName("Test Category");
        categoryRepository.persist(category);

        Set<Author> authors = new HashSet<>();
        authors.add(author);

        testBook1 = new Book();
        testBook1.setTitle("Java Programming");
        testBook1.setIsbn("1234567890");
        testBook1.setPublicationYear(2022);
        testBook1.setAuthors(authors);
        testBook1.setPublisher(publisher);
        testBook1.setCategory(category);
        bookRepository.persist(testBook1);
    }

    @Test
    @Transactional
    void testAddBookCopy() {
        BookCopy newCopy = new BookCopy();
        newCopy.setBook(testBook1);
        newCopy.setStatus("AVAILABLE");
        newCopy.setLocation("Shelf B2");
        inventoryService.addBookCopy(newCopy);

        List<BookCopy> copies = inventoryService.getCopiesForBook(testBook1.getId());
        assertEquals(1, copies.size());
    }
}
