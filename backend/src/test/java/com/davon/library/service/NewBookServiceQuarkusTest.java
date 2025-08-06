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
class NewBookServiceQuarkusTest {

    @Inject
    BookService bookService;

    @Inject
    BookRepository bookRepository;

    private static final String VALID_ISBN = "978-3-16-148410-0";
    private static final String ANOTHER_VALID_ISBN = "978-1-56619-909-4";

    @BeforeEach
    @Transactional
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateBook_Success() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn(VALID_ISBN);
        book.setPublicationYear(2023);
        book.setPages(100);

        bookService.createBook(book);

        assertNotNull(book.getId(), "Book ID should not be null after creation");
        assertEquals(1, bookRepository.count(), "Should have one book in the repository");

        Book savedBook = bookRepository.findById(book.getId());
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals(VALID_ISBN, savedBook.getIsbn());
        assertEquals(2023, savedBook.getPublicationYear());
        assertEquals(100, savedBook.getPages());
    }

    @Test
    @Transactional
    void testCreateBook_DuplicateISBN_ShouldThrowException() throws BookService.BookServiceException {
        Book book1 = new Book();
        book1.setTitle("First Book");
        book1.setIsbn(VALID_ISBN);
        bookService.createBook(book1);

        Book book2 = new Book();
        book2.setTitle("Second Book");
        book2.setIsbn(VALID_ISBN);

        BookService.BookServiceException exception = assertThrows(
                BookService.BookServiceException.class,
                () -> bookService.createBook(book2),
                "Expected BookServiceException for duplicate ISBN"
        );
        assertEquals("Book with ISBN " + VALID_ISBN + " already exists.", exception.getMessage());
    }

    @Test
    @Transactional
    void testGetBookById_Found() {
        Book book = new Book();
        book.setTitle("Find Me");
        book.setIsbn(VALID_ISBN);
        bookRepository.persist(book);

        Book found = bookService.getBookById(book.getId());

        assertNotNull(found);
        assertEquals("Find Me", found.getTitle());
    }

    @Test
    @Transactional
    void testGetBookById_NotFound_ShouldReturnNull() {
        assertNull(bookService.getBookById(999L), "Should return null for non-existent book ID");
    }

    @Test
    @Transactional
    void testGetBookByIsbn_Found() {
        Book book = new Book();
        book.setTitle("Find Me By ISBN");
        book.setIsbn(VALID_ISBN);
        bookRepository.persist(book);

        Book found = bookService.getBookByISBN(VALID_ISBN);

        assertNotNull(found);
        assertEquals(VALID_ISBN, found.getIsbn());
    }

    @Test
    @Transactional
    void testGetBookByIsbn_NotFound_ShouldReturnNull() {
        assertNull(bookService.getBookByISBN("non-existent-isbn"), "Should return null for non-existent ISBN");
    }

    @Test
    @Transactional
    void testSearchBooks_ByTitle() {
        Book book1 = new Book();
        book1.setTitle("UniqueTitle Java");
        book1.setIsbn(VALID_ISBN);
        bookRepository.persist(book1);

        Book book2 = new Book();
        book2.setTitle("UniqueTitle Python");
        book2.setIsbn(ANOTHER_VALID_ISBN);
        bookRepository.persist(book2);

        List<Book> results = bookService.searchBooks("UniqueTitle");

        assertEquals(2, results.size(), "Should find two books with the search query");
    }

    @Test
    @Transactional
    void testUpdateBook_Success() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("Original Title");
        book.setIsbn(VALID_ISBN);
        bookRepository.persist(book);

        Book updatedDetails = new Book();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setPublicationYear(2024);

        bookService.updateBook(book.getId(), updatedDetails);

        Book updatedBook = bookRepository.findById(book.getId());
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals(2024, updatedBook.getPublicationYear());
    }

    @Test
    @Transactional
    void testUpdateBook_NotFound_ShouldThrowException() {
        Book updatedDetails = new Book();
        updatedDetails.setTitle("Updated Title");

        BookService.BookServiceException exception = assertThrows(
                BookService.BookServiceException.class,
                () -> bookService.updateBook(999L, updatedDetails),
                "Expected BookServiceException for non-existent book"
        );
        assertEquals("Book with id 999 not found.", exception.getMessage());
    }

    @Test
    @Transactional
    void testDeleteBook_Success() throws BookService.BookServiceException {
        Book book = new Book();
        book.setTitle("To Be Deleted");
        book.setIsbn(VALID_ISBN);
        bookRepository.persist(book);

        bookService.deleteBook(book.getId());

        assertNull(bookRepository.findById(book.getId()), "Book should be deleted");
    }

    @Test
    @Transactional
    void testDeleteBook_NotFound_ShouldThrowException() {
        BookService.BookServiceException exception = assertThrows(
                BookService.BookServiceException.class,
                () -> bookService.deleteBook(999L),
                "Expected BookServiceException for non-existent book"
        );
        assertEquals("Book with id 999 not found.", exception.getMessage());
    }
}
