package com.davon.library.service;

import com.davon.library.model.*;
import java.time.LocalDate;
import java.util.List;

public class NotificationService {
    // Could connect to email service, SMS service, etc.
    
    public void sendCheckoutConfirmation(Member member, BookCopy bookCopy) {
        // Send checkout confirmation
        String message = String.format("You have checked out: %s. Due date: %s",
            bookCopy.getBook().getTitle(),
            LocalDate.now().plusDays(14).toString());
            
        // Send notification logic
        System.out.println("Notification to " + member.getEmail() + ": " + message);
    }
    
    public void sendOverdueNotice(Loan loan) {
        Member member = loan.getMember();
        BookCopy bookCopy = loan.getBookCopy();
        
        String message = String.format("The book '%s' is overdue. Please return it as soon as possible.",
            bookCopy.getBook().getTitle());
            
        // Send notification logic
        System.out.println("Overdue Notice to " + member.getEmail() + ": " + message);
    }
    
    public void sendReservationNotification(Reservation reservation) {
        Member member = reservation.getMember();
        Book book = reservation.getBook();
        
        String message = String.format("The book '%s' you reserved is now available. Please pick it up within 3 days.",
            book.getTitle());
            
        // Send notification logic
        System.out.println("Reservation Notice to " + member.getEmail() + ": " + message);
    }
}
