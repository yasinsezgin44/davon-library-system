package com.davon.library.service;

import com.davon.library.model.Receipt;
import com.davon.library.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService receiptService;

    private Transaction testTransaction;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));

        testTransaction = Transaction.builder()
                .id(1L)
                .description("Fine Payment")
                .amount(10.50)
                .date(LocalDate.now())
                .build();
    }

    @Test
    void testCreateReceipt() {
        // Act
        Receipt receipt = receiptService.createReceipt(testTransaction);

        // Assert
        assertNotNull(receipt);
        assertEquals(testTransaction.getId(), receipt.getTransactionId());
        assertEquals(LocalDate.now(), receipt.getIssueDate());
        assertEquals(testTransaction.getAmount(), receipt.getTotal());

        // Check receipt items
        assertNotNull(receipt.getItems());
        assertEquals(1, receipt.getItems().length);
        assertEquals(testTransaction.getDescription(), receipt.getItems()[0].getDescription());
        assertEquals(testTransaction.getAmount(), receipt.getItems()[0].getAmount());
        assertEquals(1, receipt.getItems()[0].getQuantity());
    }

    @Test
    void testEmailReceipt() {
        // Arrange
        Receipt receipt = receiptService.createReceipt(testTransaction);
        String email = "test@example.com";

        // Act
        receiptService.emailReceipt(receipt, email);

        // Assert - check console output
        String expectedOutput = "Emailing receipt " + receipt.getTransactionId() + " to " + email;
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));
    }

    @Test
    void testPrintReceipt() {
        // Arrange
        Receipt receipt = receiptService.createReceipt(testTransaction);

        // Act
        receiptService.printReceipt(receipt);

        // Assert - check console output
        String expectedOutput = "Printing receipt " + receipt.getTransactionId();
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));
    }
}