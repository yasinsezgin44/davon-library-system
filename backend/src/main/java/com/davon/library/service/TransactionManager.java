package com.davon.library.service;

import com.davon.library.model.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for managing financial transactions and fines.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class TransactionManager {

    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

    @Inject
    private FineService fineService;

    @Inject
    private ReceiptService receiptService;

    /**
     * Process a payment and create a transaction record
     * 
     * @return Transaction record for the payment
     */
    public Transaction processPayment(double amount, String method, String description) {
        Transaction transaction = Transaction.builder()
                .date(LocalDate.now())
                .amount(amount)
                .type(Transaction.TransactionType.FINE_PAYMENT)
                .description(description)
                .paymentMethod(method)
                .build();

        // In a real implementation, save the transaction to repository
        return transaction;
    }

    /**
     * Record a fine for a member
     * 
     * @return The created Fine object
     */
    public Fine recordFine(Member member, String reason, double amount) {
        Fine fine = Fine.builder()
                .amount(amount)
                .reason(getFineReasonFromString(reason))
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Fine.FineStatus.PENDING)
                .build();

        // In a real implementation, save the fine to repository
        return fine;
    }

    /**
     * Generate a report of transactions for a date range
     * 
     * @return Report object with transaction data
     */
    public Report generateReport(LocalDate startDate, LocalDate endDate) {
        // In a real implementation, query transactions for the date range
        // and compile them into a report
        return Report.builder()
                .title("Transaction Report")
                .dateGenerated(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    /**
     * Get all outstanding fines for a member
     * 
     * @return List of unpaid fines
     */
    public List<Fine> getOutstandingFines(Member member) {
        // In a real implementation, query fines repository
        return List.of(); // Placeholder
    }

    /**
     * Handle a failed payment attempt
     * 
     * @return True if recovery was successful
     */
    public boolean handleFailedPayment(Long transactionId) {
        // Logic to handle failed payment
        return false; // Placeholder
    }

    private Fine.FineReason getFineReasonFromString(String reason) {
        switch (reason.toUpperCase()) {
            case "OVERDUE":
                return Fine.FineReason.OVERDUE;
            case "DAMAGED_ITEM":
                return Fine.FineReason.DAMAGED_ITEM;
            case "LOST_ITEM":
                return Fine.FineReason.LOST_ITEM;
            default:
                return Fine.FineReason.ADMINISTRATIVE;
        }
    }
}
