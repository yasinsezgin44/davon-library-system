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
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService receiptService;

    private Transaction testTransaction;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setDescription("Fine Payment");
        testTransaction.setAmount(new BigDecimal("10.50"));
        testTransaction.setDate(LocalDate.now());
    }

    @Test
    void testCreateReceipt() {
        Receipt receipt = receiptService.createReceipt(testTransaction);
        assertNotNull(receipt);
        assertEquals(testTransaction, receipt.getTransaction());
        assertEquals(LocalDate.now(), receipt.getIssueDate());
        assertEquals(testTransaction.getAmount(), receipt.getTotal());
        assertEquals(testTransaction.getDescription(), receipt.getItems());
    }

    @Test
    void testEmailReceipt() {
        Receipt receipt = receiptService.createReceipt(testTransaction);
        String email = "test@example.com";
        receiptService.emailReceipt(receipt, email);
        String expectedOutput = "Emailing receipt " + receipt.getId() + " to " + email;
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));
    }

    @Test
    void testPrintReceipt() {
        Receipt receipt = receiptService.createReceipt(testTransaction);
        receiptService.printReceipt(receipt);
        String expectedOutput = "Printing receipt " + receipt.getId();
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));
    }
}
