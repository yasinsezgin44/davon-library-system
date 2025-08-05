package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;
import com.davon.library.service.LibrarianService.LibrarianServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private LibrarianService librarianService;

    private Member testMember;

    @Mock
    private Book testBook;

    private Loan testLoan;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .username("johndoe")
                .passwordHash("hashedpassword123")
                .email("test@library.com")
                .name("John Doe")
                .fineBalance(0.0)
                .active(true)
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
        assertEquals(testLoan.getId(), result.getId());
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(testBook).isAvailable();
        verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when member not found")
    void testCheckoutBookMemberNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertEquals("Member not found", exception.getMessage());
        verify(userService).findById(1L);
        verify(bookService, never()).getBookById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when member has outstanding fines")
    void testCheckoutBookMemberHasFines() throws Exception {
        // Arrange
        testMember.setFineBalance(10.0);
        when(userService.findById(1L)).thenReturn(testMember);

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertEquals("outstanding fines", exception.getMessage());
        verify(userService).findById(1L);
        verify(bookService, never()).getBookById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void testCheckoutBookNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(null);

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertEquals("Book not found", exception.getMessage());
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
    }

    @Test
    @DisplayName("Should throw exception when book not available")
    void testCheckoutBookNotAvailable() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(testBook.isAvailable()).thenReturn(false);

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertEquals("Book not available", exception.getMessage());
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(testBook).isAvailable();
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
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.checkoutBook(1L, 1L));

        assertEquals("Loan service error", exception.getMessage());
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(testBook).isAvailable();
        verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    @DisplayName("Should successfully return book")
    void testReturnBookSuccess() throws Exception {
        // Arrange
        Receipt mockReceipt = mock(Receipt.class);
        when(loanService.returnBook(1L)).thenReturn(mockReceipt);

        // Act
        librarianService.returnBook(1L);

        // Assert
        verify(loanService).returnBook(1L);
    }

    @Test
    @DisplayName("Should propagate LoanService exception during return")
    void testReturnBookLoanServiceException() throws Exception {
        // Arrange
        when(loanService.returnBook(1L)).thenThrow(new BusinessException("Return service error"));

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.returnBook(1L));

        assertEquals("Return service error", exception.getMessage());
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
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.getOverdueLoans());

        assertTrue(exception.getMessage().equals("Failed to get overdue loans: Service error"));
        verify(loanService).getOverdueLoans();
    }

    @Test
    @DisplayName("Should successfully renew loan")
    void testRenewLoanSuccess() throws Exception {
        // Arrange
        when(loanService.renewLoan(1L)).thenReturn(testLoan);

        // Act
        librarianService.renewLoan(1L);

        // Assert
        verify(loanService).renewLoan(1L);
    }

    @Test
    @DisplayName("Should propagate LoanService exception during renewal")
    void testRenewLoanLoanServiceException() throws Exception {
        // Arrange
        when(loanService.renewLoan(1L)).thenThrow(new BusinessException("Renewal not allowed"));

        // Act & Assert
        LibrarianServiceException exception = assertThrows(
                LibrarianServiceException.class,
                () -> librarianService.renewLoan(1L));

        assertEquals("Renewal not allowed", exception.getMessage());
        verify(loanService).renewLoan(1L);
    }
}