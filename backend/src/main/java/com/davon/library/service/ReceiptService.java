package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.logging.Logger;

@ApplicationScoped
public class ReceiptService {
    private static final Logger logger = Logger.getLogger(ReceiptService.class.getName());

    public Receipt createReceipt(Transaction transaction) {
        Receipt.ReceiptItem[] items = {
                new Receipt.ReceiptItem(transaction.getDescription(), transaction.getAmount(), 1)
        };

        Receipt receipt = Receipt.builder()
                .transactionId(transaction.getId())
                .issueDate(LocalDate.now())
                .items(items)
                .total(transaction.getAmount())
                .build();

        return receipt;
    }

    public void emailReceipt(Receipt receipt, String email) {
        // Logic to email receipt
        System.out.println("Emailing receipt " + receipt.getTransactionId() + " to " + email);
    }

    public void printReceipt(Receipt receipt) {
        // Logic to print receipt
        System.out.println("Printing receipt " + receipt.getTransactionId());
    }

    public Receipt generateReturnReceipt(Loan loan, Fine fine) {
        logger.info("DEBUG: generateReturnReceipt called - fine: " + (fine != null ? fine.getAmount() : "null"));

        String bookTitle = "Unknown";
        try {
            if (loan != null && loan.getBookCopy() != null && loan.getBookCopy().getBook() != null) {
                bookTitle = loan.getBookCopy().getBook().getTitle();
                if (bookTitle == null)
                    bookTitle = "Unknown";
            }
        } catch (Exception e) {
            logger.warning("Error getting book title: " + e.getMessage());
            bookTitle = "Unknown";
        }

        String description = "Book Return: " + bookTitle;
        double amount = fine != null ? fine.getAmount() : 0.0;

        if (fine != null) {
            description += " (Late Fee: $" + fine.getAmount() + ")";
        }

        Receipt.ReceiptItem[] items = {
                new Receipt.ReceiptItem(description, amount, 1)
        };

        Receipt receipt = Receipt.builder()
                .transactionId(loan.getId()) // Using loan ID as transaction ID
                .issueDate(LocalDate.now())
                .items(items)
                .total(amount)
                .build();

        logger.info("DEBUG: Receipt generated - Total: " + receipt.getTotal() + ", Description: " + description);
        return receipt;
    }
}
