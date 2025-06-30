package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibrarianService Checkout/Return Tests")
class LibrarianServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanService loanService;

    @Mock
    private CatalogingService catalogingService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private TransactionManager transactionManager;

    private LibrarianService librarianService;
    private Member testMember;
    private Book testBook;
    private Loan testLoan;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        librarianService = new LibrarianService();

        // Use reflection or setters to inject mocks if needed
        // For now, assuming constructor injection or field injection works
        try {
            java.lang.reflect.Field userServiceField = LibrarianService.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(librarianService, userService);

            java.lang.reflect.Field bookServiceField = LibrarianService.class.getDeclaredField("bookService");
            bookServiceField.setAccessible(true);
            bookServiceField.set(librarianService, bookService);

            java.lang.reflect.Field loanServiceField = LibrarianService.class.getDeclaredField("loanService");
            loanServiceField.setAccessible(true);
            loanServiceField.set(librarianService, loanService);
        } catch (Exception e) {
            // Handle reflection exceptions
        }

        testMember = Member.builder()
                .id(1L)
                .email("test@library.com")
                .firstName("John")
                .lastName("Doe")
                .fineBalance(0.0)
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        testLoan = Loan.builder()
                .id(1L)
                .member(testMember)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .build();

        testReceipt = Receipt.builder()
                .transactionId(1L)
                .issueDate(LocalDate.now())
                .total(0.0)
                .build();
    }

    @Test
    @DisplayName("Should successfully checkout book when member and book are valid")
    void testCheckoutBookSuccess() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(testBook.isAvailable()).thenReturn(true);
        when(loanService.checkoutBook(1L, 1L)).thenReturn(testLoan);

        // Act
        Loan result = librarianService.checkoutBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getMember().getId());

        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when member not found during checkout")
    void testCheckoutBookMemberNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Member not found"));
        verify(userService).findById(1L);
        verifyNoInteractions(bookService, loanService);
    }

    @Test
    @DisplayName("Should throw exception when member has outstanding fines during checkout")
    void testCheckoutBookMemberHasFines() throws Exception {
        // Arrange
        testMember.setFineBalance(10.0);
        when(userService.findById(1L)).thenReturn(testMember);

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("outstanding fines"));
        verify(userService).findById(1L);
        verifyNoInteractions(bookService, loanService);
    }

    @Test
    @DisplayName("Should throw exception when book not found during checkout")
    void testCheckoutBookNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(null);

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Book not found"));
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verifyNoInteractions(loanService);
    }

    @Test
    @DisplayName("Should throw exception when book not available during checkout")
    void testCheckoutBookNotAvailable() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(testBook.isAvailable()).thenReturn(false);

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("not available"));
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verifyNoInteractions(loanService);
    }

    @Test
    @DisplayName("Should propagate LoanService exception during checkout")
    void testCheckoutBookLoanServiceException() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(testBook.isAvailable()).thenReturn(true);
        when(loanService.checkoutBook(1L, 1L)).thenThrow(new BusinessException("Loan service error"));

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Failed to checkout book"));
        verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    @DisplayName("Should successfully return book")
    void testReturnBookSuccess() throws Exception {
        // Arrange
        when(loanService.returnBook(1L)).thenReturn(testReceipt);

        // Act
        Receipt result = librarianService.returnBook(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReceipt.getTransactionId(), result.getTransactionId());

        verify(loanService).returnBook(1L);
    }

    @Test
    @DisplayName("Should propagate LoanService exception during return")
    void testReturnBookLoanServiceException() throws Exception {
        // Arrange
        when(loanService.returnBook(1L)).thenThrow(new BusinessException("Return service error"));

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.returnBook(1L));

        assertTrue(exception.getMessage().contains("Failed to return book"));
        verify(loanService).returnBook(1L);
    }

    @Test
    @DisplayName("Should successfully get overdue loans")
    void testGetOverdueLoansSuccess() throws Exception {
        // Arrange
        List<Loan> overdueLoans = Arrays.asList(testLoan);
        when(loanService.getOverdueLoans()).thenReturn(overdueLoans);

        // Act
        List<Loan> result = librarianService.getOverdueLoans();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLoan.getId(), result.get(0).getId());

        verify(loanService).getOverdueLoans();
    }

    @Test
    @DisplayName("Should propagate exception when getting overdue loans fails")
    void testGetOverdueLoansException() throws Exception {
        // Arrange
        when(loanService.getOverdueLoans()).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.getOverdueLoans());

        assertTrue(exception.getMessage().contains("Failed to get overdue loans"));
        verify(loanService).getOverdueLoans();
    }

    @Test
    @DisplayName("Should successfully renew loan")
    void testRenewLoanSuccess() throws Exception {
        // Arrange
        Loan renewedLoan = Loan.builder()
                .id(1L)
                .member(testMember)
                .dueDate(LocalDate.now().plusDays(28)) // Extended
                .renewalCount(1)
                .build();
        when(loanService.renewLoan(1L)).thenReturn(renewedLoan);

        // Act
        Loan result = librarianService.renewLoan(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRenewalCount());

        verify(loanService).renewLoan(1L);
    }

    @Test
    @DisplayName("Should propagate LoanService exception during renewal")
    void testRenewLoanLoanServiceException() throws Exception {
        // Arrange
        when(loanService.renewLoan(1L)).thenThrow(new BusinessException("Renewal not allowed"));

        // Act & Assert
        LibrarianService.LibrarianServiceException exception = assertThrows(
                LibrarianService.LibrarianServiceException.class,
                () -> librarianService.renewLoan(1L));

        assertTrue(exception.getMessage().contains("Failed to renew loan"));
        verify(loanService).renewLoan(1L);
    }
}