package com.davon.library.dao.impl;

import com.davon.library.dao.BookCopyDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MSSQLBookCopyDAOImplTest {

    @Inject
    BookCopyDAO bookCopyDAO;

    @Inject
    DatabaseConnectionManager connectionManager;

    private Book testBook;
    private BookCopy testBookCopy;

    @BeforeEach
    void setUp() throws SQLException {
        // Clean up test data
        cleanUpTestData();

        // Set up test book (assuming it exists in DB with id=1)
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .isbn("978-0134685991")
                .publicationYear(2023)
                .build();

        // Set up test book copy
        testBookCopy = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Good")
                .status(BookCopy.CopyStatus.AVAILABLE)
                .location("A1-01")
                .build();
    }

    private void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up book_copies table for testing
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM book_copies WHERE book_id = 1")) {
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testSaveBookCopy() throws DAOException {
        // Act
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);

        // Assert
        assertNotNull(savedBookCopy);
        assertNotNull(savedBookCopy.getId());
        assertEquals(testBookCopy.getBook().getId(), savedBookCopy.getBook().getId());
        assertEquals(testBookCopy.getAcquisitionDate(), savedBookCopy.getAcquisitionDate());
        assertEquals(testBookCopy.getCondition(), savedBookCopy.getCondition());
        assertEquals(testBookCopy.getStatus(), savedBookCopy.getStatus());
        assertEquals(testBookCopy.getLocation(), savedBookCopy.getLocation());
        assertNotNull(savedBookCopy.getCreatedAt());
        assertNotNull(savedBookCopy.getUpdatedAt());
    }

    @Test
    void testFindById() throws DAOException {
        // Arrange
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);

        // Act
        Optional<BookCopy> foundBookCopy = bookCopyDAO.findById(savedBookCopy.getId());

        // Assert
        assertTrue(foundBookCopy.isPresent());
        BookCopy bookCopy = foundBookCopy.get();
        assertEquals(savedBookCopy.getId(), bookCopy.getId());
        assertEquals(savedBookCopy.getBook().getId(), bookCopy.getBook().getId());
        assertEquals(savedBookCopy.getAcquisitionDate(), bookCopy.getAcquisitionDate());
        assertEquals(savedBookCopy.getCondition(), bookCopy.getCondition());
        assertEquals(savedBookCopy.getStatus(), bookCopy.getStatus());
        assertEquals(savedBookCopy.getLocation(), bookCopy.getLocation());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Optional<BookCopy> foundBookCopy = bookCopyDAO.findById(999999L);

        // Assert
        assertFalse(foundBookCopy.isPresent());
    }

    @Test
    void testUpdate() throws DAOException {
        // Arrange
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);
        String newCondition = "Excellent";
        BookCopy.CopyStatus newStatus = BookCopy.CopyStatus.CHECKED_OUT;
        String newLocation = "B2-05";

        // Act
        savedBookCopy.setCondition(newCondition);
        savedBookCopy.setStatus(newStatus);
        savedBookCopy.setLocation(newLocation);
        BookCopy updatedBookCopy = bookCopyDAO.update(savedBookCopy);

        // Assert
        assertNotNull(updatedBookCopy);
        assertEquals(savedBookCopy.getId(), updatedBookCopy.getId());
        assertEquals(newCondition, updatedBookCopy.getCondition());
        assertEquals(newStatus, updatedBookCopy.getStatus());
        assertEquals(newLocation, updatedBookCopy.getLocation());

        // Verify in database
        Optional<BookCopy> foundBookCopy = bookCopyDAO.findById(savedBookCopy.getId());
        assertTrue(foundBookCopy.isPresent());
        assertEquals(newCondition, foundBookCopy.get().getCondition());
        assertEquals(newStatus, foundBookCopy.get().getStatus());
        assertEquals(newLocation, foundBookCopy.get().getLocation());
    }

    @Test
    void testDelete() throws DAOException {
        // Arrange
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);
        Long bookCopyId = savedBookCopy.getId();

        // Verify it exists
        assertTrue(bookCopyDAO.existsById(bookCopyId));

        // Act
        bookCopyDAO.delete(savedBookCopy);

        // Assert
        assertFalse(bookCopyDAO.existsById(bookCopyId));
        Optional<BookCopy> foundBookCopy = bookCopyDAO.findById(bookCopyId);
        assertFalse(foundBookCopy.isPresent());
    }

    @Test
    void testDeleteById() throws DAOException {
        // Arrange
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);
        Long bookCopyId = savedBookCopy.getId();

        // Verify it exists
        assertTrue(bookCopyDAO.existsById(bookCopyId));

        // Act
        bookCopyDAO.deleteById(bookCopyId);

        // Assert
        assertFalse(bookCopyDAO.existsById(bookCopyId));
        Optional<BookCopy> foundBookCopy = bookCopyDAO.findById(bookCopyId);
        assertFalse(foundBookCopy.isPresent());
    }

    @Test
    void testExistsById() throws DAOException {
        // Arrange
        BookCopy savedBookCopy = bookCopyDAO.save(testBookCopy);

        // Act & Assert
        assertTrue(bookCopyDAO.existsById(savedBookCopy.getId()));
        assertFalse(bookCopyDAO.existsById(999999L));
    }

    @Test
    void testFindAll() throws DAOException {
        // Arrange
        BookCopy bookCopy1 = bookCopyDAO.save(testBookCopy);

        BookCopy testBookCopy2 = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Fair")
                .status(BookCopy.CopyStatus.IN_REPAIR)
                .location("A1-02")
                .build();
        BookCopy bookCopy2 = bookCopyDAO.save(testBookCopy2);

        // Act
        List<BookCopy> allBookCopies = bookCopyDAO.findAll();

        // Assert
        assertNotNull(allBookCopies);
        assertTrue(allBookCopies.size() >= 2);
        assertTrue(allBookCopies.stream().anyMatch(bc -> bc.getId().equals(bookCopy1.getId())));
        assertTrue(allBookCopies.stream().anyMatch(bc -> bc.getId().equals(bookCopy2.getId())));
    }

    @Test
    void testCount() throws DAOException {
        // Arrange
        long initialCount = bookCopyDAO.count();
        bookCopyDAO.save(testBookCopy);

        // Act
        long newCount = bookCopyDAO.count();

        // Assert
        assertEquals(initialCount + 1, newCount);
    }

    @Test
    void testFindAvailableByBook() throws DAOException {
        // Arrange
        BookCopy availableCopy = bookCopyDAO.save(testBookCopy);

        BookCopy checkedOutCopy = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Good")
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .location("A1-03")
                .build();
        bookCopyDAO.save(checkedOutCopy);

        // Act
        List<BookCopy> availableCopies = bookCopyDAO.findAvailableByBook(testBook);

        // Assert
        assertNotNull(availableCopies);
        assertTrue(availableCopies.size() >= 1);
        assertTrue(availableCopies.stream().anyMatch(bc -> bc.getId().equals(availableCopy.getId())));
        assertTrue(availableCopies.stream().allMatch(bc -> bc.getStatus() == BookCopy.CopyStatus.AVAILABLE));
    }

    @Test
    void testFindByStatus() throws DAOException {
        // Arrange
        BookCopy availableCopy = bookCopyDAO.save(testBookCopy);

        BookCopy inRepairCopy = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Poor")
                .status(BookCopy.CopyStatus.IN_REPAIR)
                .location("A1-04")
                .build();
        bookCopyDAO.save(inRepairCopy);

        // Act
        List<BookCopy> availableCopies = bookCopyDAO.findByStatus(BookCopy.CopyStatus.AVAILABLE);
        List<BookCopy> inRepairCopies = bookCopyDAO.findByStatus(BookCopy.CopyStatus.IN_REPAIR);

        // Assert
        assertNotNull(availableCopies);
        assertNotNull(inRepairCopies);
        assertTrue(availableCopies.stream().anyMatch(bc -> bc.getId().equals(availableCopy.getId())));
        assertTrue(inRepairCopies.stream().anyMatch(bc -> bc.getId().equals(inRepairCopy.getId())));
        assertTrue(availableCopies.stream().allMatch(bc -> bc.getStatus() == BookCopy.CopyStatus.AVAILABLE));
        assertTrue(inRepairCopies.stream().allMatch(bc -> bc.getStatus() == BookCopy.CopyStatus.IN_REPAIR));
    }

    @Test
    void testCountAvailableByBook() throws DAOException {
        // Arrange
        long initialAvailableCount = bookCopyDAO.countAvailableByBook(testBook);

        bookCopyDAO.save(testBookCopy); // Available copy

        BookCopy checkedOutCopy = BookCopy.builder()
                .book(testBook)
                .acquisitionDate(LocalDate.now())
                .condition("Good")
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .location("A1-05")
                .build();
        bookCopyDAO.save(checkedOutCopy);

        // Act
        long availableCount = bookCopyDAO.countAvailableByBook(testBook);

        // Assert
        assertEquals(initialAvailableCount + 1, availableCount);
    }

    @Test
    void testFindByLocation() throws DAOException {
        // Arrange
        String testLocation = "TEST-LOCATION";
        testBookCopy.setLocation(testLocation);
        BookCopy savedCopy = bookCopyDAO.save(testBookCopy);

        // Act
        List<BookCopy> copiesAtLocation = bookCopyDAO.findByLocation(testLocation);

        // Assert
        assertNotNull(copiesAtLocation);
        assertTrue(copiesAtLocation.size() >= 1);
        assertTrue(copiesAtLocation.stream().anyMatch(bc -> bc.getId().equals(savedCopy.getId())));
        assertTrue(copiesAtLocation.stream().allMatch(bc -> testLocation.equals(bc.getLocation())));
    }

    @Test
    void testIsAvailable() throws DAOException {
        // Test available copy
        testBookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        assertTrue(testBookCopy.isAvailable());

        // Test checked out copy
        testBookCopy.setStatus(BookCopy.CopyStatus.CHECKED_OUT);
        assertFalse(testBookCopy.isAvailable());

        // Test in repair copy
        testBookCopy.setStatus(BookCopy.CopyStatus.IN_REPAIR);
        assertFalse(testBookCopy.isAvailable());
    }

    @Test
    void testCheckOutAndCheckIn() throws DAOException {
        // Test check out
        testBookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        testBookCopy.checkOut();
        assertEquals(BookCopy.CopyStatus.CHECKED_OUT, testBookCopy.getStatus());

        // Test check in
        testBookCopy.checkIn();
        assertEquals(BookCopy.CopyStatus.AVAILABLE, testBookCopy.getStatus());
    }

    @Test
    void testUpdateCondition() {
        // Act
        boolean result = testBookCopy.updateCondition("Excellent", "Recently cleaned");

        // Assert
        assertTrue(result);
        assertEquals("Excellent", testBookCopy.getCondition());
    }

    @Test
    void testSaveWithNullBook() {
        // Arrange
        testBookCopy.setBook(null);

        // Act & Assert
        assertThrows(DAOException.class, () -> bookCopyDAO.save(testBookCopy));
    }

    @Test
    void testUpdateNonExistentBookCopy() {
        // Arrange
        testBookCopy.setId(999999L);

        // Act & Assert
        assertThrows(DAOException.class, () -> bookCopyDAO.update(testBookCopy));
    }

    @Test
    void testDeleteNonExistentBookCopy() {
        // Act & Assert
        assertThrows(DAOException.class, () -> bookCopyDAO.deleteById(999999L));
    }
}