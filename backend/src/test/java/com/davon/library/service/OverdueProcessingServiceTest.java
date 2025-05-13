package com.davon.library.service;

import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Book;
import com.davon.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OverdueProcessingServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private FineService fineService;

    @InjectMocks
    private OverdueProcessingService overdueProcessingService;

    private Loan overdueLoan;
    private Loan notOverdueLoan;
    private Fine fine;

    @BeforeEach
    void setUp() {
        User user = new User(); // Simplified User
        user.setId("user1");

        Book book = new Book(); // Simplified Book
        book.setId(1L);

        overdueLoan = new Loan();
        overdueLoan.setId("loan1");
        overdueLoan.setBook(book);
        overdueLoan.setUser(user);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(30)); // Example: 30 days ago
        overdueLoan.setDueDate(LocalDate.now().minusDays(1)); // Example: Due yesterday
        overdueLoan.setStatus(Loan.LoanStatus.ACTIVE); // Initially active, should become OVERDUE

        notOverdueLoan = new Loan();
        notOverdueLoan.setId("loan2");
        notOverdueLoan.setBook(book);
        notOverdueLoan.setUser(user);
        notOverdueLoan.setLoanDate(LocalDate.now().minusDays(5));
        notOverdueLoan.setDueDate(LocalDate.now().plusDays(5)); // Due in 5 days
        notOverdueLoan.setStatus(Loan.LoanStatus.ACTIVE);

        fine = new Fine(); // Simplified Fine
        fine.setId("fine1");
        fine.setAmount(5.00);
    }

    @Test
    void processOverdueItems_shouldProcessOverdueLoan() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.singletonList(overdueLoan));
        when(fineService.calculateOverdueFine(overdueLoan)).thenReturn(fine);

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        // Verify loan status is updated and saved
        assertEquals(Loan.LoanStatus.OVERDUE, overdueLoan.getStatus());
        verify(loanRepository, times(1)).save(overdueLoan);

        // Verify fine calculation
        verify(fineService, times(1)).calculateOverdueFine(overdueLoan);

        // Verify notification sending
        verify(notificationService, times(1)).sendOverdueNotice(overdueLoan);
    }

    @Test
    void processOverdueItems_shouldNotProcessAlreadyOverdueLoanAgain() {
        // Arrange
        overdueLoan.setStatus(Loan.LoanStatus.OVERDUE); // Mark as already overdue
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.singletonList(overdueLoan));

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        // Ensure save, fine calculation, and notification are not called again
        verify(loanRepository, never()).save(overdueLoan);
        verify(fineService, never()).calculateOverdueFine(any(Loan.class));
        verify(notificationService, never()).sendOverdueNotice(any(Loan.class));
    }

    @Test
    void processOverdueItems_shouldNotProcessNotOverdueLoan() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(notOverdueLoan));

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        // Ensure loan status is not changed and no actions are performed
        assertEquals(Loan.LoanStatus.ACTIVE, notOverdueLoan.getStatus());
        verify(loanRepository, never()).save(notOverdueLoan);
        verify(fineService, never()).calculateOverdueFine(any(Loan.class));
        verify(notificationService, never()).sendOverdueNotice(any(Loan.class));
    }

    @Test
    void processOverdueItems_shouldHandleNoOverdueLoans() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.emptyList());

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        // Ensure no interactions with services or repository if no overdue loans
        verify(loanRepository, never()).save(any(Loan.class));
        verify(fineService, never()).calculateOverdueFine(any(Loan.class));
        verify(notificationService, never()).sendOverdueNotice(any(Loan.class));
    }
}