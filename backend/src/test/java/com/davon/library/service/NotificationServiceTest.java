package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.model.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
        private NotificationService notificationService;
        private User user;
        private Member member;
        private Book book;
        private BookCopy bookCopy;
        private Loan loan;
        private Reservation reservation;

        private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        private final PrintStream originalOut = System.out;

        @BeforeEach
        void setUp() {
                System.setOut(new PrintStream(outContent));
                notificationService = new NotificationService();

                user = new User();
                user.setEmail("member@test.com");

                member = new Member();
                member.setId(1L);
                member.setUser(user);
                member.setFineBalance(BigDecimal.ZERO);

                book = new Book();
                book.setId(1L);
                book.setTitle("Test Book");
                book.setIsbn("1234567890");

                bookCopy = new BookCopy();
                bookCopy.setId(1L);
                bookCopy.setBook(book);
                bookCopy.setStatus(CopyStatus.CHECKED_OUT);

                loan = new Loan();
                loan.setId(1L);
                loan.setMember(member);
                loan.setBookCopy(bookCopy);
                loan.setCheckoutDate(LocalDate.now().minusDays(20));
                loan.setDueDate(LocalDate.now().minusDays(6));
                loan.setStatus(LoanStatus.OVERDUE);

                reservation = new Reservation();
                reservation.setId(1L);
                reservation.setMember(member);
                reservation.setBook(book);
                reservation.setStatus(ReservationStatus.PENDING);
        }

        @AfterEach
        void tearDown() {
                System.setOut(originalOut);
        }

        @Test
        void testSendCheckoutConfirmation() {
                notificationService.sendCheckoutConfirmation(member, bookCopy);
                String output = outContent.toString();
                assertTrue(output.contains("Notification to member@test.com"));
                assertTrue(output.contains("You have checked out: Test Book"));
                assertTrue(output.contains("Due date: " + LocalDate.now().plusDays(14).toString()));
        }

        @Test
        void testSendOverdueNotice() {
                notificationService.sendOverdueNotice(loan);
                String output = outContent.toString();
                assertTrue(output.contains("Overdue Notice to member@test.com"));
                assertTrue(output.contains("The book 'Test Book' is overdue"));
        }

        @Test
        void testSendReservationNotification() {
                notificationService.sendReservationNotification(reservation);
                String output = outContent.toString();
                assertTrue(output.contains("Reservation Notice to member@test.com"));
                assertTrue(output.contains("The book 'Test Book' you reserved is now available"));
                assertTrue(output.contains("Please pick it up within 3 days"));
        }
}
