package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibrarianServiceTest {

    @Mock
    private CatalogingService catalogingService;

    @Mock
    private UserService userService;

    @Mock
    private LoanManager loanManager;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private LibrarianService librarianService;

    private Book testBook;
    private Member testMember;
    private BookCopy testBookCopy;
    private Loan testLoan;
    private Report testReport;
    @Mock
    private Transaction testTransaction;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("9781234567890")
                .build();

        // Create Member with proper properties
        testMember = Member.builder()
                .id(1L)
                .username("jdoe")
                .fullName("John Doe") // Use fullName from User class
                .email("john.doe@example.com")
                .build();

        testBookCopy = BookCopy.builder()
                .id(1L)
                .book(testBook)
                .status(BookCopy.CopyStatus.AVAILABLE) // Set status directly
                .build();

        testLoan = Loan.builder()
                .id(1L)
                .member(testMember)
                .bookCopy(testBookCopy)
                .build();

        testReport = new Report();
        testReport.setId(1L);
        testReport.setTitle("Test Report");

        testReceipt = Receipt.builder()
                .transactionId(1L)
                .total(10.0)
                .build();
    }

    @Test
    void testAddBookToCatalog() {
        // Arrange
        when(catalogingService.catalogNewBook(testBook)).thenReturn(testBook);

        // Act
        Book result = librarianService.addBookToCatalog(testBook);

        // Assert
        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(catalogingService).catalogNewBook(testBook);
    }

    @Test
    void testUpdateBookCatalog() {
        // Act
        Book result = librarianService.updateBookCatalog(1L, testBook);

        // Assert
        assertNotNull(result);
        assertEquals(testBook, result);
    }

    @Test
    void testRemoveBookFromCatalog() {
        // Arrange
        when(inventoryService.removeBook(1L)).thenReturn(true);

        // Act
        boolean result = librarianService.removeBookFromCatalog(1L);

        // Assert
        assertTrue(result);
        verify(inventoryService).removeBook(1L);
    }

    @Test
    void testRegisterMember() {
        // Arrange
        when(userService.createUser(testMember)).thenReturn(testMember);

        // Act
        Member result = librarianService.registerMember(testMember);

        // Assert
        assertNotNull(result);
        assertEquals(testMember, result);
        verify(userService).createUser(testMember);
    }

    @Test
    void testUpdateMemberInfo() {
        // Arrange
        when(userService.updateUser(1L, testMember)).thenReturn(testMember);

        // Act
        Member result = librarianService.updateMemberInfo(1L, testMember);

        // Assert
        assertNotNull(result);
        assertEquals(testMember, result);
        verify(userService).updateUser(1L, testMember);
    }

    @Test
    void testDeactivateMemberAccount() {
        // Arrange
        when(userService.deactivateUser(1L)).thenReturn(true);

        // Act
        boolean result = librarianService.deactivateMemberAccount(1L);

        // Assert
        assertTrue(result);
        verify(userService).deactivateUser(1L);
    }

    @Test
    void testCheckoutBookForMember_Success() {
        // Arrange
        Set<User> users = new HashSet<>();
        users.add(testMember);
        when(userService.getUsers()).thenReturn(users);
        when(inventoryService.getCopiesForBook(1L)).thenReturn(Collections.singletonList(testBookCopy));
        when(loanManager.checkOutBook(testMember, testBookCopy)).thenReturn(testLoan);

        // Act
        Loan result = librarianService.checkoutBookForMember(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testLoan, result);
        verify(loanManager).checkOutBook(testMember, testBookCopy);
    }

    @Test
    void testCheckoutBookForMember_MemberNotFound() {
        // Arrange
        when(userService.getUsers()).thenReturn(new HashSet<>());

        // Act
        Loan result = librarianService.checkoutBookForMember(1L, 1L);

        // Assert
        assertNull(result);
        verify(loanManager, never()).checkOutBook(any(), any());
    }

    @Test
    void testReturnBookFromMember() {
        // Act
        librarianService.returnBookFromMember(1L);

        // Assert
        verify(loanManager).returnBook(1L);
    }

    @Test
    void testRenewLoanForMember() {
        // Arrange
        when(loanManager.renewLoan(1L)).thenReturn(true);

        // Act
        boolean result = librarianService.renewLoanForMember(1L);

        // Assert
        assertTrue(result);
        verify(loanManager).renewLoan(1L);
    }

    @Test
    void testGenerateDailyCirculationReport() {
        // Arrange
        when(reportService.generateDailyCirculation()).thenReturn(testReport);

        // Act
        Report result = librarianService.generateDailyCirculationReport();

        // Assert
        assertNotNull(result);
        assertEquals(testReport, result);
        verify(reportService).generateDailyCirculation();
    }

    @Test
    void testPerformInventoryCheck() {
        // Arrange
        Map<Long, String> bookCopyStatuses = new HashMap<>();
        bookCopyStatuses.put(1L, "LOST");

        // Act
        librarianService.performInventoryCheck(bookCopyStatuses);

        // Assert
        // Using the correct enum value from BookCopy.CopyStatus
        verify(inventoryService).updateBookStatus(eq(1L), eq(BookCopy.CopyStatus.valueOf("LOST")));
    }

    @Test
    void testUpdateBookLocation() {
        // Act
        librarianService.updateBookLocation(1L, "A1.B2.C3");

        // Assert
        verify(inventoryService).updateBookCopyLocation(1L, "A1.B2.C3");
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        when(transactionManager.processPayment(10.0, "CASH", "Fine payment")).thenReturn(testTransaction);
        when(testTransaction.generateReceipt()).thenReturn(testReceipt);
        lenient().when(testTransaction.getId()).thenReturn(1L);
        lenient().when(testTransaction.getAmount()).thenReturn(10.0);
        lenient().when(testTransaction.getDescription()).thenReturn("Fine payment");

        // Act
        Receipt result = librarianService.processPayment(10.0, "CASH", "Fine payment", 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReceipt, result);
        verify(transactionManager).processPayment(10.0, "CASH", "Fine payment");
    }
}