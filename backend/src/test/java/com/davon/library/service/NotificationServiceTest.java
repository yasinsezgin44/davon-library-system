package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService notificationService;
    private Member member;
    private Book book;
    private BookCopy bookCopy;
    private Loan loan;
    private Reservation reservation;

    // For capturing console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture console output
        System.setOut(new PrintStream(outContent));

        notificationService = new NotificationService();

        // Create test member
        member = Member.builder()
                .id(1L)
                .fullName("Test Member")
                .email("member@test.com")
                .active(true)
                .build();

        // Create test book
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        // Create test book copy
        bookCopy = BookCopy.builder()
                .id(1L)
                .book(book)
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .build();

        // Create test loan
        loan = Loan.builder()
                .id(1L)
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(6))
                .status(Loan.LoanStatus.OVERDUE)
                .build();

        // Create test reservation
        reservation = Reservation.builder()
                .id(1L)
                .member(member)
                .book(book)
                .reservationDate(LocalDate.now().minusDays(3))
                .expirationDate(LocalDate.now().plusDays(4))
                .status(Reservation.ReservationStatus.PENDING)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void testSendCheckoutConfirmation() {
        // Call the method
        notificationService.sendCheckoutConfirmation(member, bookCopy);

        // Check the output contains the expected content
        String output = outContent.toString();

        assertTrue(output.contains("Notification to member@test.com"));
        assertTrue(output.contains("You have checked out: Test Book"));
        assertTrue(output.contains("Due date: " + LocalDate.now().plusDays(14).toString()));
    }

    @Test
    void testSendOverdueNotice() {
        // Call the method
        notificationService.sendOverdueNotice(loan);

        // Check the output contains the expected content
        String output = outContent.toString();

        assertTrue(output.contains("Overdue Notice to member@test.com"));
        assertTrue(output.contains("The book 'Test Book' is overdue"));
    }

    @Test
    void testSendReservationNotification() {
        // Call the method
        notificationService.sendReservationNotification(reservation);

        // Check the output contains the expected content
        String output = outContent.toString();

        assertTrue(output.contains("Reservation Notice to member@test.com"));
        assertTrue(output.contains("The book 'Test Book' you reserved is now available"));
        assertTrue(output.contains("Please pick it up within 3 days"));
    }
}