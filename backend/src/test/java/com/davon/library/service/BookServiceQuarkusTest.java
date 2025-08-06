package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.repository.BookRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BookServiceQuarkusTest {

    @Inject
    BookService bookService;

    @Inject
    BookRepository bookRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateBook() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn(generateUniqueISBN());
        bookService.createBook(book);
        assertNotNull(book.getId());
        assertEquals(1, bookRepository.count());
    }

    @Test
    @Transactional
    void testGetBookById() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn(generateUniqueISBN());
        bookRepository.persist(book);

        Book found = bookService.getBookById(book.getId());
        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
    }

    @Test
    @Transactional
    void testGetBookByIsbn() {
        String isbn = generateUniqueISBN();
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn(isbn);
        bookRepository.persist(book);

        Book found = bookService.getBookByISBN(isbn);
        assertNotNull(found);
        assertEquals(isbn, found.getIsbn());
    }

    @Test
    @Transactional
    void testSearchBooks() {
        String uniquePrefix = "TestSearch" + System.nanoTime();
        Book book1 = new Book();
        book1.setTitle(uniquePrefix + " Java Programming");
        book1.setIsbn(generateUniqueISBN());
        bookRepository.persist(book1);

        Book book2 = new Book();
        book2.setTitle(uniquePrefix + " Python Guide");
        book2.setIsbn(generateUniqueISBN());
        bookRepository.persist(book2);

        List<Book> results = bookService.searchBooks(uniquePrefix);
        assertEquals(2, results.size());
    }

    @Test
    @Transactional
    void testUpdateBook() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("Original Title");
        book.setIsbn(generateUniqueISBN());
        bookRepository.persist(book);

        book.setTitle("Updated Title");
        bookService.updateBook(book.getId(), book);

        Book updated = bookRepository.findById(book.getId());
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    @Transactional
    void testDeleteBook() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("To Be Deleted");
        book.setIsbn(generateUniqueISBN());
        bookRepository.persist(book);

        bookService.deleteBook(book.getId());
        assertNull(bookRepository.findById(book.getId()));
    }

    private String generateUniqueISBN() {
        long timestamp = System.nanoTime();
        return "978" + String.format("%010d", Math.abs(timestamp % 10000000000L));
    }
}
