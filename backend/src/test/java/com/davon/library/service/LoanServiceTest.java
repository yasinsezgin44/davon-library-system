package com.davon.library.service;

import com.davon.library.repository.*;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Tests")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private FineRepository fineRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReceiptService receiptService;

    private LoanService loanService;
    private Member testMember;
    private Book testBook;
    private BookCopy testBookCopy;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookCopyRepository, fineRepository, userService,
                bookService, notificationService, receiptService);

        testMember = Member.builder()
                .id(1L)
                .email("test@library.com")
                .fullName("John Doe")
                .fineBalance(0.0)
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        testBookCopy = BookCopy.builder()
                .id(1L)
                .book(testBook)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .build();

        testLoan = Loan.builder()
                .id(1L)
                .member(testMember)
                .bookCopy(testBookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();
    }

    @Test
    @DisplayName("Should successfully checkout book when all conditions are met")
    void testCheckoutBookSuccess() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(bookCopyRepository.findAvailableByBook(testBook)).thenReturn(Optional.of(testBookCopy));
        when(loanRepository.countActiveLoansByMember(testMember)).thenReturn(2L);
        doNothing().when(loanRepository).persist(any(Loan.class));
        doNothing().when(bookCopyRepository).persist(any(BookCopy.class));

        // Act
        Loan result = loanService.checkoutBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMember.getId(), result.getMember().getId());
        assertEquals(testBookCopy.getId(), result.getBookCopy().getId());

        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(bookCopyRepository).findAvailableByBook(testBook);
        verify(loanRepository).persist(any(Loan.class));
        verify(bookCopyRepository).persist(any(BookCopy.class));
        verify(notificationService).sendCheckoutNotification(eq(testMember), any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when member has outstanding fines")
    void testCheckoutBookWithFines() {
        // Arrange
        testMember.setFineBalance(10.0);
        when(userService.findById(1L)).thenReturn(testMember);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Member has outstanding fines of $10.0"));
        verify(userService).findById(1L);
        verifyNoInteractions(bookService, loanRepository, bookCopyRepository);
    }

    @Test
    @DisplayName("Should throw exception when member not found")
    void testCheckoutBookMemberNotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Member not found with ID: 1"));
    }

    @Test
    @DisplayName("Should throw exception when user is not a member")
    void testCheckoutBookUserNotMember() {
        // Arrange
        User nonMember = new User() {
        }; // Anonymous subclass for testing
        nonMember.setId(1L);
        when(userService.findById(1L)).thenReturn(nonMember);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("User is not a member"));
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void testCheckoutBookNotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("Book not found with ID: 1"));
    }

    @Test
    @DisplayName("Should throw exception when no available copies")
    void testCheckoutBookNoAvailableCopies() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(bookCopyRepository.findAvailableByBook(testBook)).thenReturn(Optional.empty());
        when(loanRepository.countActiveLoansByMember(testMember)).thenReturn(2L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("No available copies of book: Test Book"));
    }

    @Test
    @DisplayName("Should throw exception when member reaches loan limit")
    void testCheckoutBookLoanLimitReached() {
        // Arrange
        when(userService.findById(1L)).thenReturn(testMember);
        when(loanRepository.countActiveLoansByMember(testMember)).thenReturn(5L); // Max limit

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(1L, 1L));

        assertTrue(exception.getMessage().contains("maximum loan limit"));
    }

    @Test
    @DisplayName("Should successfully return book without late fee")
    void testReturnBookSuccess() throws Exception {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(testLoan);
        doNothing().when(loanRepository).persist(any(Loan.class));
        doNothing().when(bookCopyRepository).persist(any(BookCopy.class));
        when(receiptService.generateReturnReceipt(any(Loan.class), any())).thenReturn(new Receipt());

        // Act
        Receipt result = loanService.returnBook(1L);

        // Assert
        assertNotNull(result);
        verify(loanRepository).findById(1L);
        verify(loanRepository).persist(any(Loan.class));
        verify(bookCopyRepository).persist(any(BookCopy.class));
        verify(receiptService).generateReturnReceipt(any(Loan.class), isNull());
        verify(notificationService).sendReturnNotification(eq(testMember), any(Loan.class));
        verifyNoInteractions(fineRepository); // No fine since not overdue
    }

    @Test
    @DisplayName("Should successfully return overdue book with late fee")
    void testReturnOverdueBookWithFine() throws Exception {
        // Arrange
        testLoan.setDueDate(LocalDate.now().minusDays(3)); // Overdue
        Fine expectedFine = Fine.builder()
                .member(testMember)
                .amount(0.75) // 3 days * $0.25
                .reason(Fine.FineReason.OVERDUE)
                .status(Fine.FineStatus.PENDING)
                .build();

        when(loanRepository.findById(1L)).thenReturn(testLoan);
        doNothing().when(fineRepository).persist(any(Fine.class));
        when(userService.updateUser(eq(1L), any(Member.class))).thenReturn(testMember);
        doNothing().when(loanRepository).persist(any(Loan.class));
        doNothing().when(bookCopyRepository).persist(any(BookCopy.class));
        when(receiptService.generateReturnReceipt(any(Loan.class), any(Fine.class))).thenReturn(new Receipt());

        // Act
        Receipt result = loanService.returnBook(1L);

        // Assert
        assertNotNull(result);
        verify(fineRepository).persist(any(Fine.class));
        verify(userService).updateUser(eq(1L), any(Member.class));
        verify(receiptService).generateReturnReceipt(any(Loan.class), any(Fine.class));
    }

    @Test
    @DisplayName("Should throw exception when loan not found for return")
    void testReturnBookLoanNotFound() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.returnBook(1L));

        assertTrue(exception.getMessage().contains("Loan not found"));
    }

    @Test
    @DisplayName("Should throw exception when trying to return non-active loan")
    void testReturnBookNotActive() {
        // Arrange
        testLoan.setStatus(Loan.LoanStatus.RETURNED);
        when(loanRepository.findById(1L)).thenReturn(testLoan);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.returnBook(1L));

        assertTrue(exception.getMessage().contains("Only active loans can be returned"));
    }

    @Test
    @DisplayName("Should successfully renew loan when conditions are met")
    void testRenewLoanSuccess() throws Exception {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(testLoan);
        doNothing().when(loanRepository).persist(any(Loan.class));

        // Act
        Loan result = loanService.renewLoan(1L);

        // Assert
        assertNotNull(result);
        verify(loanRepository).findById(1L);
        verify(loanRepository).persist(any(Loan.class));
        verify(notificationService).sendRenewalNotification(eq(testMember), any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when renewing loan with outstanding fines")
    void testRenewLoanWithFines() {
        // Arrange
        testMember.setFineBalance(10.0);
        when(loanRepository.findById(1L)).thenReturn(testLoan);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> loanService.renewLoan(1L));

        assertEquals("Member has outstanding fines of $10.0", exception.getMessage());
        verify(loanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when maximum renewals reached")
    void testRenewLoanMaxRenewalsReached() {
        // Arrange
        testLoan.setRenewalCount(2); // Max renewals
        when(loanRepository.findById(1L)).thenReturn(testLoan);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.renewLoan(1L));

        assertTrue(exception.getMessage().contains("Loan renewal not allowed"));
    }

    @Test
    @DisplayName("Should get member loans successfully")
    void testGetMemberLoans() {
        // Arrange
        List<Loan> expectedLoans = Arrays.asList(testLoan);
        when(loanRepository.findByMember(any(Member.class))).thenReturn(expectedLoans);

        // Act
        List<Loan> result = loanService.getMemberLoans(1L);

        // Assert
        assertEquals(1, result.size());
        verify(loanRepository).findByMember(any(Member.class));
    }

    @Test
    @DisplayName("Should get member active loans successfully")
    void testGetMemberActiveLoans() {
        // Arrange
        List<Loan> expectedLoans = Arrays.asList(testLoan);
        when(loanRepository.findActiveLoansByMember(any(Member.class))).thenReturn(expectedLoans);

        // Act
        List<Loan> result = loanService.getMemberActiveLoans(1L);

        // Assert
        assertEquals(1, result.size());
        verify(loanRepository).findActiveLoansByMember(any(Member.class));
    }

    @Test
    @DisplayName("Should get overdue loans successfully")
    void testGetOverdueLoans() {
        // Arrange
        List<Loan> expectedLoans = Arrays.asList(testLoan);
        when(loanRepository.findOverdueLoans()).thenReturn(expectedLoans);

        // Act
        List<Loan> result = loanService.getOverdueLoans();

        // Assert
        assertEquals(1, result.size());
        verify(loanRepository).findOverdueLoans();
    }
}