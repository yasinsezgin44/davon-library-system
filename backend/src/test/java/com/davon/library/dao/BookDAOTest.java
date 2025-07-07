package com.davon.library.dao;

import com.davon.library.dao.impl.MSSQLBookDAOImpl;
import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Test class for BookDAO implementation.
 * Demonstrates that the new DAO pattern works correctly and follows SOLID
 * principles.
 */
@QuarkusTest
public class BookDAOTest {

    @Inject
    BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        // This method is empty as the DAO is injected
    }

    @Test
    void testSaveAndFindById() throws DAOException {
        // Given
        Book book = createTestBook("Test Title", "1234567890123");

        // When
        Book savedBook = bookDAO.save(book);
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());

        // Then
        assertNotNull(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Test Title", foundBook.get().getTitle());
        assertEquals("1234567890123", foundBook.get().getISBN());
    }

    @Test
    void testFindByISBN() throws DAOException {
        // Given
        String isbn = "1234567890123";
        Book book = createTestBook("Test Title", isbn);
        bookDAO.save(book);

        // When
        Optional<Book> foundBook = bookDAO.findByISBN(isbn);

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(isbn, foundBook.get().getISBN());
    }

    @Test
    void testSearchBooks() throws DAOException {
        // Given
        Book book1 = createTestBook("Java Programming", "1111111111111");
        Book book2 = createTestBook("Python Guide", "2222222222222");
        Book book3 = createTestBook("Advanced Java", "3333333333333");

        bookDAO.save(book1);
        bookDAO.save(book2);
        bookDAO.save(book3);

        // When
        List<Book> results = bookDAO.searchBooks("Java");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Java Programming")));
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Advanced Java")));
    }

    @Test
    void testUpdateBook() throws DAOException {
        // Given
        Book book = createTestBook("Original Title", "1234567890123");
        Book savedBook = bookDAO.save(book);

        // When
        savedBook.setTitle("Updated Title");
        Book updatedBook = bookDAO.update(savedBook);

        // Then
        assertEquals("Updated Title", updatedBook.getTitle());
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Updated Title", foundBook.get().getTitle());
    }

    @Test
    void testDeleteBook() throws DAOException {
        // Given
        Book book = createTestBook("Test Title", "1234567890123");
        Book savedBook = bookDAO.save(book);

        // When
        bookDAO.deleteById(savedBook.getId());

        // Then
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());
        assertFalse(foundBook.isPresent());
    }

    @Test
    void testExistsByISBN() throws DAOException {
        // Given
        String isbn = "1234567890123";
        Book book = createTestBook("Test Title", isbn);
        bookDAO.save(book);

        // When & Then
        assertTrue(bookDAO.existsByISBN(isbn));
        assertFalse(bookDAO.existsByISBN("9999999999999"));
    }

    @Test
    void testDuplicateISBNValidation() throws DAOException {
        // Given
        String isbn = "1234567890123";
        Book book1 = createTestBook("Title 1", isbn);
        Book book2 = createTestBook("Title 2", isbn);

        bookDAO.save(book1);

        // When & Then
        assertThrows(DAOException.class, () -> bookDAO.save(book2));
    }

    @Test
    void testFindByCategory() throws DAOException {
        // Given
        Category fiction = Category.builder().name("Fiction").build();
        Category nonFiction = Category.builder().name("Non-Fiction").build();

        Book book1 = createTestBook("Fiction Book", "1111111111111");
        book1.setCategory(fiction);

        Book book2 = createTestBook("Non-Fiction Book", "2222222222222");
        book2.setCategory(nonFiction);

        bookDAO.save(book1);
        bookDAO.save(book2);

        // When
        List<Book> fictionBooks = bookDAO.findByCategory(fiction);

        // Then
        assertEquals(1, fictionBooks.size());
        assertEquals("Fiction Book", fictionBooks.get(0).getTitle());
    }

    @Test
    void testFindByAuthor() throws DAOException {
        // Given
        Author author1 = Author.builder().name("John Doe").build();
        Author author2 = Author.builder().name("Jane Smith").build();

        Set<Author> authors1 = new HashSet<>();
        authors1.add(author1);

        Set<Author> authors2 = new HashSet<>();
        authors2.add(author2);

        Book book1 = createTestBook("Book by John", "1111111111111");
        book1.setAuthors(authors1);

        Book book2 = createTestBook("Book by Jane", "2222222222222");
        book2.setAuthors(authors2);

        bookDAO.save(book1);
        bookDAO.save(book2);

        // When
        List<Book> johnBooks = bookDAO.findByAuthor(author1);

        // Then
        assertEquals(1, johnBooks.size());
        assertEquals("Book by John", johnBooks.get(0).getTitle());
    }

    private Book createTestBook(String title, String isbn) {
        return Book.builder()
                .title(title)
                .ISBN(isbn)
                .publicationYear(2023)
                .description("Test description")
                .pages(100)
                .build();
    }
}