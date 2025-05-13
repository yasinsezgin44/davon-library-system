package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {
    private InventoryService inventoryService;
    private Book testBook1;
    private Book testBook2;
    private BookCopy availableCopy;
    private BookCopy checkedOutCopy;
    private Author author;
    private Publisher publisher;
    private Category category;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();

        // Create test data
        author = Author.builder()
                .id(1L)
                .name("Test Author")
                .build();

        publisher = Publisher.builder()
                .id(1L)
                .name("Test Publisher")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Set<Author> authors = new HashSet<>();
        authors.add(author);

        testBook1 = Book.builder()
                .id(1L)
                .title("Java Programming")
                .ISBN("1234567890")
                .publicationYear(2022)
                .authors(authors)
                .publisher(publisher)
                .category(category)
                .build();

        testBook2 = Book.builder()
                .id(2L)
                .title("Database Systems")
                .ISBN("0987654321")
                .publicationYear(2021)
                .authors(authors)
                .publisher(publisher)
                .category(category)
                .build();

        // Add books to inventory
        inventoryService.addBook(testBook1);
        inventoryService.addBook(testBook2);

        // Create book copies
        availableCopy = BookCopy.builder()
                .id(1L)
                .book(testBook1)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .location("Shelf A1")
                .build();

        checkedOutCopy = BookCopy.builder()
                .id(2L)
                .book(testBook1)
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .location("Checked Out")
                .build();

        // Add copies to inventory
        inventoryService.addBookCopy(availableCopy);
        inventoryService.addBookCopy(checkedOutCopy);
    }

    @Test
    void testGetTotalBooks() {
        assertEquals(2, inventoryService.getTotalBooks());
    }

    @Test
    void testGetAvailableBooks() {
        List<Book> availableBooks = inventoryService.getAvailableBooks();
        assertEquals(1, availableBooks.size());
        assertEquals("Java Programming", availableBooks.get(0).getTitle());
    }

    @Test
    void testSearchBooks() {
        // Search by title
        List<Book> javaBooks = inventoryService.searchBooks("Java");
        assertEquals(1, javaBooks.size());
        assertEquals("Java Programming", javaBooks.get(0).getTitle());

        // Search by ISBN
        List<Book> isbnBooks = inventoryService.searchBooks("98765");
        assertEquals(1, isbnBooks.size());
        assertEquals("Database Systems", isbnBooks.get(0).getTitle());

        // Search by author
        List<Book> authorBooks = inventoryService.searchBooks("Test Author");
        assertEquals(2, authorBooks.size());

        // Search by publisher
        List<Book> publisherBooks = inventoryService.searchBooks("Test Publisher");
        assertEquals(2, publisherBooks.size());

        // Search by category
        List<Book> categoryBooks = inventoryService.searchBooks("Test Category");
        assertEquals(2, categoryBooks.size());

        // Search with no matches
        List<Book> noMatches = inventoryService.searchBooks("xyz123");
        assertEquals(0, noMatches.size());
    }

    @Test
    void testAddBook() {
        Book newBook = Book.builder()
                .id(3L)
                .title("Python Programming")
                .ISBN("5555555555")
                .build();

        boolean added = inventoryService.addBook(newBook);
        assertTrue(added);
        assertEquals(3, inventoryService.getTotalBooks());

        // Try to add the same book again (should return false as Set doesn't allow
        // duplicates)
        boolean addedAgain = inventoryService.addBook(newBook);
        assertFalse(addedAgain);
    }

    @Test
    void testRemoveBook() {
        boolean removed = inventoryService.removeBook(1L);
        assertTrue(removed);
        assertEquals(1, inventoryService.getTotalBooks());

        // Verify that copies of the book are also removed
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        assertEquals(0, copies.size());

        // Try to remove a non-existent book
        boolean removedNonExistent = inventoryService.removeBook(999L);
        assertFalse(removedNonExistent);
    }

    @Test
    void testAddBookCopy() {
        BookCopy newCopy = BookCopy.builder()
                .id(3L)
                .book(testBook2)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .location("Shelf B2")
                .build();

        boolean added = inventoryService.addBookCopy(newCopy);
        assertTrue(added);

        List<BookCopy> copies = inventoryService.getCopiesForBook(2L);
        assertEquals(1, copies.size());

        // Try to add a copy for a non-existent book
        Book nonExistentBook = Book.builder()
                .id(999L)
                .title("Non-existent Book")
                .build();

        BookCopy invalidCopy = BookCopy.builder()
                .id(4L)
                .book(nonExistentBook)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .build();

        boolean invalidAdded = inventoryService.addBookCopy(invalidCopy);
        assertFalse(invalidAdded);
    }

    @Test
    void testRemoveBookCopy() {
        boolean removed = inventoryService.removeBookCopy(1L);
        assertTrue(removed);

        // Verify the copy was removed
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        assertEquals(1, copies.size()); // Only one copy left
        assertEquals(BookCopy.CopyStatus.CHECKED_OUT, copies.get(0).getStatus());

        // Try to remove a non-existent copy
        boolean removedNonExistent = inventoryService.removeBookCopy(999L);
        assertFalse(removedNonExistent);
    }

    @Test
    void testUpdateBookStatus() {
        // Update all copies of a book to IN_REPAIR
        inventoryService.updateBookStatus(1L, BookCopy.CopyStatus.IN_REPAIR);

        // Verify all copies were updated
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        for (BookCopy copy : copies) {
            assertEquals(BookCopy.CopyStatus.IN_REPAIR, copy.getStatus());
        }
    }

    @Test
    void testUpdateBookCopyLocation() {
        boolean updated = inventoryService.updateBookCopyLocation(1L, "New Location");
        assertTrue(updated);

        // Verify the location was updated
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        BookCopy updatedCopy = copies.stream()
                .filter(c -> c.getId() == 1L)
                .findFirst()
                .orElse(null);

        assertNotNull(updatedCopy);
        assertEquals("New Location", updatedCopy.getLocation());

        // Try to update a non-existent copy
        boolean updatedNonExistent = inventoryService.updateBookCopyLocation(999L, "Invalid Location");
        assertFalse(updatedNonExistent);
    }

    @Test
    void testGetCopiesForBook() {
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        assertEquals(2, copies.size());

        // Verify that we have copies in different states
        Set<BookCopy.CopyStatus> statuses = copies.stream()
                .map(BookCopy::getStatus)
                .collect(Collectors.toSet());

        assertEquals(2, statuses.size());
        assertTrue(statuses.contains(BookCopy.CopyStatus.AVAILABLE));
        assertTrue(statuses.contains(BookCopy.CopyStatus.CHECKED_OUT));

        // Check a book with no copies
        List<BookCopy> noCopies = inventoryService.getCopiesForBook(2L);
        assertEquals(0, noCopies.size());
    }

    @Test
    void testProcessBookDisposal() {
        boolean disposed = inventoryService.processBookDisposal(1L, "Damaged beyond repair");
        assertTrue(disposed);

        // Verify the status was updated
        List<BookCopy> copies = inventoryService.getCopiesForBook(1L);
        BookCopy disposedCopy = copies.stream()
                .filter(c -> c.getId() == 1L)
                .findFirst()
                .orElse(null);

        assertNotNull(disposedCopy);
        assertEquals(BookCopy.CopyStatus.LOST, disposedCopy.getStatus());

        // Try to dispose a non-existent copy
        boolean disposedNonExistent = inventoryService.processBookDisposal(999L, "Invalid");
        assertFalse(disposedNonExistent);
    }
}