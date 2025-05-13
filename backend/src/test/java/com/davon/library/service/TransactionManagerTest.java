package com.davon.library.service;

import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import com.davon.library.model.Report;
import com.davon.library.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionManagerTest {

    @Mock
    private FineService fineService;

    @Mock
    private ReceiptService receiptService;

    @Mock
    private Member mockMember;

    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionManager = new TransactionManager(fineService, receiptService);
    }

    @Test
    void testProcessPayment() {
        // Arrange
        double amount = 15.50;
        String method = "CREDIT_CARD";
        String description = "Payment for overdue book";

        // Act
        Transaction transaction = transactionManager.processPayment(amount, method, description);

        // Assert
        assertNotNull(transaction);
        assertEquals(amount, transaction.getAmount());
        assertEquals(method, transaction.getPaymentMethod());
        assertEquals(description, transaction.getDescription());
        assertEquals(Transaction.TransactionType.FINE_PAYMENT, transaction.getType());
        assertEquals(LocalDate.now(), transaction.getDate());
    }

    @Test
    void testRecordFine_Overdue() {
        // Arrange
        String reason = "OVERDUE";
        double amount = 5.00;

        // Act
        Fine fine = transactionManager.recordFine(mockMember, reason, amount);

        // Assert
        assertNotNull(fine);
        assertEquals(amount, fine.getAmount());
        assertEquals(Fine.FineReason.OVERDUE, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
        assertEquals(LocalDate.now(), fine.getIssueDate());
        assertEquals(LocalDate.now().plusDays(14), fine.getDueDate());
    }

    @Test
    void testRecordFine_DamagedItem() {
        // Arrange
        String reason = "DAMAGED_ITEM";
        double amount = 20.00;

        // Act
        Fine fine = transactionManager.recordFine(mockMember, reason, amount);

        // Assert
        assertNotNull(fine);
        assertEquals(amount, fine.getAmount());
        assertEquals(Fine.FineReason.DAMAGED_ITEM, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
    }

    @Test
    void testRecordFine_LostItem() {
        // Arrange
        String reason = "LOST_ITEM";
        double amount = 35.00;

        // Act
        Fine fine = transactionManager.recordFine(mockMember, reason, amount);

        // Assert
        assertNotNull(fine);
        assertEquals(amount, fine.getAmount());
        assertEquals(Fine.FineReason.LOST_ITEM, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
    }

    @Test
    void testRecordFine_UnknownReason() {
        // Arrange
        String reason = "UNKNOWN_REASON";
        double amount = 10.00;

        // Act
        Fine fine = transactionManager.recordFine(mockMember, reason, amount);

        // Assert
        assertNotNull(fine);
        assertEquals(amount, fine.getAmount());
        assertEquals(Fine.FineReason.ADMINISTRATIVE, fine.getReason());
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());
    }

    @Test
    void testGenerateReport() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        // Act
        Report report = transactionManager.generateReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals("Transaction Report", report.getTitle());
        assertEquals(LocalDate.now(), report.getDateGenerated());
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
    }

    @Test
    void testGetOutstandingFines() {
        // Arrange
        when(mockMember.getId()).thenReturn(1L);

        // Act
        List<Fine> fines = transactionManager.getOutstandingFines(mockMember);

        // Assert
        assertNotNull(fines);
        // This is a placeholder implementation that returns an empty list
        assertTrue(fines.isEmpty());
    }

    @Test
    void testHandleFailedPayment() {
        // Arrange
        Long transactionId = 123L;

        // Act
        boolean result = transactionManager.handleFailedPayment(transactionId);

        // Assert
        assertFalse(result); // The placeholder implementation returns false
    }
}