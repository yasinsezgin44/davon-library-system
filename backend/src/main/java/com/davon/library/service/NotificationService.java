package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class NotificationService {

    public void sendCheckoutConfirmation(Member member, BookCopy bookCopy) {
        String message = String.format("You have checked out: %s. Due date: %s",
                bookCopy.getBook().getTitle(),
                LocalDate.now().plusDays(14).toString());

        System.out.println("Notification to " + member.getUser().getEmail() + ": " + message);
    }

    public void sendOverdueNotice(Loan loan) {
        Member member = loan.getMember();
        BookCopy bookCopy = loan.getBookCopy();

        String message = String.format("The book '%s' is overdue. Please return it as soon as possible.",
                bookCopy.getBook().getTitle());

        System.out.println("Overdue Notice to " + member.getUser().getEmail() + ": " + message);
    }

    public void sendReservationNotification(Reservation reservation) {
        Member member = reservation.getMember();
        Book book = reservation.getBook();

        String message = String.format("The book '%s' you reserved is now available. Please pick it up within 3 days.",
                book.getTitle());

        System.out.println("Reservation Notice to " + member.getUser().getEmail() + ": " + message);
    }

    public void sendCheckoutNotification(Member member, Loan loan) {
        String message = String.format("You have checked out: %s. Due date: %s",
                loan.getBookCopy().getBook().getTitle(),
                loan.getDueDate().toString());

        System.out.println("Checkout Notification to " + member.getUser().getEmail() + ": " + message);
    }

    public void sendReturnNotification(Member member, Loan loan) {
        String message = String.format("You have successfully returned: %s. Thank you!",
                loan.getBookCopy().getBook().getTitle());

        System.out.println("Return Notification to " + member.getUser().getEmail() + ": " + message);
    }

    public void sendRenewalNotification(Member member, Loan loan) {
        String message = String.format("You have successfully renewed: %s. New due date: %s",
                loan.getBookCopy().getBook().getTitle(),
                loan.getDueDate().toString());

        System.out.println("Renewal Notification to " + member.getUser().getEmail() + ": " + message);
    }
}
