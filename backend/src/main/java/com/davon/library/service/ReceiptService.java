package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class ReceiptService {
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
        String description = "Book Return: " + loan.getBookCopy().getBook().getTitle();
        System.out.println("DEBUG Receipt: Fine object = " + fine);
        System.out.println("DEBUG Receipt: Fine amount = " + (fine != null ? fine.getAmount() : "NULL"));
        double amount = fine != null ? fine.getAmount() : 0.0;
        System.out.println("DEBUG Receipt: Setting receipt total to = " + amount);

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

        return receipt;
    }
}
