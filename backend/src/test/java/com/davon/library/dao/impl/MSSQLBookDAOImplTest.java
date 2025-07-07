package com.davon.library.dao.impl;

import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MSSQLBookDAOImplTest {

    @Inject
    BookDAO bookDAO;

    @Inject
    DatabaseConnectionManager connectionManager;

    private Book testBook;

    @BeforeEach
    void setUp() throws SQLException {
        // Clean up test data
        cleanUpTestData();

        testBook = Book.builder()
                .title("Test Book Title")
                .ISBN("978-0123456789")
                .publicationYear(2023)
                .description("A test book for unit testing")
                .pages(250)
                .coverImage("test-cover.jpg")
                .build();
    }

    private void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up books table for testing
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE isbn LIKE '978-0123%'")) {
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            // Log but don't fail - test database might not be ready
            System.out.println("Warning: Could not clean up test data: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @Transactional
    void testSaveBook() throws DAOException {
        // Given
        assertNull(testBook.getId());

        // When
        Book savedBook = bookDAO.save(testBook);

        // Then
        assertNotNull(savedBook.getId());
        assertNotNull(savedBook.getCreatedAt());
        assertNotNull(savedBook.getUpdatedAt());
        assertEquals(testBook.getTitle(), savedBook.getTitle());
        assertEquals(testBook.getISBN(), savedBook.getISBN());
        assertEquals(testBook.getPublicationYear(), savedBook.getPublicationYear());
        assertEquals(testBook.getDescription(), savedBook.getDescription());
        assertEquals(testBook.getPages(), savedBook.getPages());
        assertEquals(testBook.getCoverImage(), savedBook.getCoverImage());
    }

    @Test
    @Order(2)
    @Transactional
    void testFindById() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);

        // When
        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.getId(), foundBook.get().getId());
        assertEquals(savedBook.getTitle(), foundBook.get().getTitle());
        assertEquals(savedBook.getISBN(), foundBook.get().getISBN());
    }

    @Test
    @Order(3)
    void testFindByIdNotFound() {
        // When
        Optional<Book> foundBook = bookDAO.findById(999999L);

        // Then
        assertFalse(foundBook.isPresent());
    }

    @Test
    @Order(4)
    @Transactional
    void testFindByISBN() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);

        // When
        Optional<Book> foundBook = bookDAO.findByISBN(testBook.getISBN());

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.getId(), foundBook.get().getId());
        assertEquals(testBook.getISBN(), foundBook.get().getISBN());
    }

    @Test
    @Order(5)
    void testFindByISBNNotFound() {
        // When
        Optional<Book> foundBook = bookDAO.findByISBN("999-9999999999");

        // Then
        assertFalse(foundBook.isPresent());
    }

    @Test
    @Order(6)
    @Transactional
    void testExistsByISBN() throws DAOException {
        // Given
        bookDAO.save(testBook);

        // When & Then
        assertTrue(bookDAO.existsByISBN(testBook.getISBN()));
        assertFalse(bookDAO.existsByISBN("999-9999999999"));
    }

    @Test
    @Order(7)
    @Transactional
    void testFindByTitleContaining() throws DAOException {
        // Given
        Book book1 = Book.builder()
                .title("Java Programming")
                .ISBN("978-0111111111")
                .publicationYear(2022)
                .build();

        Book book2 = Book.builder()
                .title("Python Programming")
                .ISBN("978-0222222222")
                .publicationYear(2023)
                .build();

        bookDAO.save(book1);
        bookDAO.save(book2);

        // When
        List<Book> programmingBooks = bookDAO.findByTitleContaining("Programming");
        List<Book> javaBooks = bookDAO.findByTitleContaining("Java");
        List<Book> notFoundBooks = bookDAO.findByTitleContaining("NonExistent");

        // Then
        assertEquals(2, programmingBooks.size());
        assertEquals(1, javaBooks.size());
        assertEquals(0, notFoundBooks.size());
        assertEquals("Java Programming", javaBooks.get(0).getTitle());
    }

    @Test
    @Order(8)
    @Transactional
    void testSearchBooks() throws DAOException {
        // Given
        Book book1 = Book.builder()
                .title("Advanced Java")
                .ISBN("978-0333333333")
                .description("Comprehensive guide to Java programming")
                .publicationYear(2022)
                .build();

        bookDAO.save(book1);

        // When
        List<Book> titleResults = bookDAO.searchBooks("Java");
        List<Book> isbnResults = bookDAO.searchBooks("978-0333333333");
        List<Book> descriptionResults = bookDAO.searchBooks("Comprehensive");
        List<Book> notFoundResults = bookDAO.searchBooks("NonExistent");

        // Then
        assertEquals(1, titleResults.size());
        assertEquals(1, isbnResults.size());
        assertEquals(1, descriptionResults.size());
        assertEquals(0, notFoundResults.size());
    }

    @Test
    @Order(9)
    @Transactional
    void testUpdateBook() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);
        Long originalId = savedBook.getId();
        LocalDateTime originalCreatedAt = savedBook.getCreatedAt();

        // When
        savedBook.setTitle("Updated Title");
        savedBook.setDescription("Updated description");
        savedBook.setPages(300);

        Book updatedBook = bookDAO.update(savedBook);

        // Then
        assertEquals(originalId, updatedBook.getId());
        assertEquals(originalCreatedAt, updatedBook.getCreatedAt());
        assertTrue(updatedBook.getUpdatedAt().isAfter(originalCreatedAt));
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated description", updatedBook.getDescription());
        assertEquals(300, updatedBook.getPages());
    }

    @Test
    @Order(10)
    @Transactional
    void testExistsById() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);

        // When & Then
        assertTrue(bookDAO.existsById(savedBook.getId()));
        assertFalse(bookDAO.existsById(999999L));
    }

    @Test
    @Order(11)
    @Transactional
    void testCount() throws DAOException {
        // Given
        long initialCount = bookDAO.count();

        bookDAO.save(testBook);

        Book book2 = Book.builder()
                .title("Another Book")
                .ISBN("978-0444444444")
                .publicationYear(2023)
                .build();
        bookDAO.save(book2);

        // When
        long newCount = bookDAO.count();

        // Then
        assertEquals(initialCount + 2, newCount);
    }

    @Test
    @Order(12)
    @Transactional
    void testFindAll() throws DAOException {
        // Given
        bookDAO.save(testBook);

        Book book2 = Book.builder()
                .title("Second Book")
                .ISBN("978-0555555555")
                .publicationYear(2023)
                .build();
        bookDAO.save(book2);

        // When
        List<Book> allBooks = bookDAO.findAll();

        // Then
        assertTrue(allBooks.size() >= 2);
        assertTrue(allBooks.stream().anyMatch(b -> b.getTitle().equals("Test Book Title")));
        assertTrue(allBooks.stream().anyMatch(b -> b.getTitle().equals("Second Book")));
    }

    @Test
    @Order(13)
    @Transactional
    void testFindByPublicationYear() throws DAOException {
        // Given
        Book book2022 = Book.builder()
                .title("Book from 2022")
                .ISBN("978-0666666666")
                .publicationYear(2022)
                .build();

        Book book2023 = Book.builder()
                .title("Book from 2023")
                .ISBN("978-0777777777")
                .publicationYear(2023)
                .build();

        bookDAO.save(book2022);
        bookDAO.save(book2023);

        // When
        List<Book> books2022 = bookDAO.findByPublicationYear(2022);
        List<Book> books2023 = bookDAO.findByPublicationYear(2023);
        List<Book> books2021 = bookDAO.findByPublicationYear(2021);

        // Then
        assertEquals(1, books2022.size());
        assertTrue(books2023.size() >= 1);
        assertEquals(0, books2021.size());
        assertEquals("Book from 2022", books2022.get(0).getTitle());
    }

    @Test
    @Order(14)
    @Transactional
    void testFindByPublicationYearBetween() throws DAOException {
        // Given
        Book book2020 = Book.builder()
                .title("Book from 2020")
                .ISBN("978-0888888888")
                .publicationYear(2020)
                .build();

        Book book2022 = Book.builder()
                .title("Book from 2022")
                .ISBN("978-0999999999")
                .publicationYear(2022)
                .build();

        Book book2024 = Book.builder()
                .title("Book from 2024")
                .ISBN("978-1000000000")
                .publicationYear(2024)
                .build();

        bookDAO.save(book2020);
        bookDAO.save(book2022);
        bookDAO.save(book2024);

        // When
        List<Book> booksInRange = bookDAO.findByPublicationYearBetween(2021, 2023);

        // Then
        assertTrue(booksInRange.size() >= 1);
        assertTrue(booksInRange.stream().anyMatch(b -> b.getPublicationYear() == 2022));
        assertFalse(booksInRange.stream().anyMatch(b -> b.getPublicationYear() == 2020));
        assertFalse(booksInRange.stream().anyMatch(b -> b.getPublicationYear() == 2024));
    }

    @Test
    @Order(15)
    @Transactional
    void testDeleteById() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);
        Long bookId = savedBook.getId();

        assertTrue(bookDAO.existsById(bookId));

        // When
        bookDAO.deleteById(bookId);

        // Then
        assertFalse(bookDAO.existsById(bookId));
        assertFalse(bookDAO.findById(bookId).isPresent());
    }

    @Test
    @Order(16)
    @Transactional
    void testDelete() throws DAOException {
        // Given
        Book savedBook = bookDAO.save(testBook);
        Long bookId = savedBook.getId();

        assertTrue(bookDAO.existsById(bookId));

        // When
        bookDAO.delete(savedBook);

        // Then
        assertFalse(bookDAO.existsById(bookId));
        assertFalse(bookDAO.findById(bookId).isPresent());
    }

    @Test
    @Order(17)
    void testDeleteNonExistentBook() {
        // When & Then
        assertThrows(DAOException.class, () -> {
            bookDAO.deleteById(999999L);
        });
    }

    @Test
    @Order(18)
    void testUpdateNonExistentBook() throws DAOException {
        // Given
        Book nonExistentBook = Book.builder()
                .id(999999L)
                .title("Non-existent Book")
                .ISBN("978-9999999999")
                .publicationYear(2023)
                .build();

        // When & Then
        assertThrows(DAOException.class, () -> {
            bookDAO.update(nonExistentBook);
        });
    }

    @Test
    @Order(19)
    @Transactional
    void testSaveBookWithNullValues() throws DAOException {
        // Given
        Book bookWithNulls = Book.builder()
                .title("Minimal Book")
                .ISBN("978-1111111111")
                .publicationYear(2023)
                .description(null)
                .coverImage(null)
                .pages(0)
                .build();

        // When
        Book savedBook = bookDAO.save(bookWithNulls);

        // Then
        assertNotNull(savedBook.getId());
        assertEquals("Minimal Book", savedBook.getTitle());
        assertEquals("978-1111111111", savedBook.getISBN());
        assertNull(savedBook.getDescription());
        assertNull(savedBook.getCoverImage());
    }

    @Test
    @Order(20)
    @Transactional
    void testClearAll() throws DAOException {
        // Given
        bookDAO.save(testBook);

        Book book2 = Book.builder()
                .title("Book to be cleared")
                .ISBN("978-2222222222")
                .publicationYear(2023)
                .build();
        bookDAO.save(book2);

        assertTrue(bookDAO.count() >= 2);

        // When
        bookDAO.clearAll();

        // Then
        assertEquals(0, bookDAO.count());
        assertEquals(0, bookDAO.findAll().size());
    }
}