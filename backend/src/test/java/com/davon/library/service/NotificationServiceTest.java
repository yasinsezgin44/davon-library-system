package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@QuarkusTest
class NotificationServiceTest {

    @Inject
    NotificationService notificationService;

    @InjectMock
    EmailService emailService;

    private Member member;
    private Loan loan;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        member = new Member();
        member.setUser(user);

        Book book = new Book();
        book.setTitle("Test Book");

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);

        loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setDueDate(LocalDate.now().plusDays(14));
    }

    @Test
    void testSendCheckoutNotification() {
        notificationService.sendCheckoutNotification(member, loan);
        Mockito.verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendReturnNotification() {
        notificationService.sendReturnNotification(member, loan);
        Mockito.verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendOverdueNotice() {
        notificationService.sendOverdueNotice(loan);
        Mockito.verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }
}
