package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

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
    private Member member;
    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("member@example.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);

        overdueLoan = new Loan();
        overdueLoan.setId(1L);
        overdueLoan.setBookCopy(bookCopy);
        overdueLoan.setMember(member);
        overdueLoan.setCheckoutDate(LocalDate.now().minusDays(30));
        overdueLoan.setDueDate(LocalDate.now().minusDays(1));
        overdueLoan.setStatus(Loan.LoanStatus.ACTIVE);

        notOverdueLoan = new Loan();
        notOverdueLoan.setId(2L);
        notOverdueLoan.setBookCopy(bookCopy);
        notOverdueLoan.setMember(member);
        notOverdueLoan.setCheckoutDate(LocalDate.now().minusDays(5));
        notOverdueLoan.setDueDate(LocalDate.now().plusDays(5));
        notOverdueLoan.setStatus(Loan.LoanStatus.ACTIVE);

        fine = new Fine();
        fine.setId(1L);
        fine.setAmount(5.00);
        fine.setReason(Fine.FineReason.OVERDUE);
        fine.setStatus(Fine.FineStatus.PENDING);
    }

    @Test
    void processOverdueItems_shouldProcessOverdueLoan() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.singletonList(overdueLoan));
        when(fineService.calculateOverdueFine(overdueLoan)).thenReturn(fine);

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        assertEquals(Loan.LoanStatus.OVERDUE, overdueLoan.getStatus());
        verify(loanRepository, times(1)).save(overdueLoan);
        verify(fineService, times(1)).calculateOverdueFine(overdueLoan);
        verify(notificationService, times(1)).sendOverdueNotice(overdueLoan);
    }

    @Test
    void processOverdueItems_shouldNotProcessAlreadyOverdueLoanAgain() {
        // Arrange
        overdueLoan.setStatus(Loan.LoanStatus.OVERDUE);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.singletonList(overdueLoan));

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        verify(loanRepository, never()).save(overdueLoan);
        verify(fineService, never()).calculateOverdueFine(any(Loan.class));
        verify(notificationService, never()).sendOverdueNotice(any(Loan.class));
    }

    @Test
    void processOverdueItems_shouldNotProcessNotOverdueLoan() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        assertEquals(Loan.LoanStatus.ACTIVE, notOverdueLoan.getStatus());
        verify(loanRepository, never()).save(notOverdueLoan);
        verify(fineService, never()).calculateOverdueFine(notOverdueLoan);
        verify(notificationService, never()).sendOverdueNotice(notOverdueLoan);
    }

    @Test
    void processOverdueItems_shouldHandleNoOverdueLoans() {
        // Arrange
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.emptyList());

        // Act
        overdueProcessingService.processOverdueItems();

        // Assert
        verify(loanRepository, never()).save(any(Loan.class));
        verify(fineService, never()).calculateOverdueFine(any(Loan.class));
        verify(notificationService, never()).sendOverdueNotice(any(Loan.class));
    }
}