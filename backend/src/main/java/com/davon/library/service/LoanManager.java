package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class LoanManager {
    private final LoanRepository loanRepository;
    private final LoanHistoryRepository loanHistoryRepository;

    /**
     * Check out a book to a member
     * 
     * @return The created Loan object
     */
    public Loan checkOutBook(Member member, BookCopy bookCopy) {
        if (!bookCopy.isAvailable()) {
            return null;
        }

        Loan loan = Loan.builder()
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();

        bookCopy.checkOut();

        // Save loan and update loan history
        recordLoanHistory(member, loan, "CHECKOUT");

        return loanRepository.save(loan);
    }

    /**
     * Return a book
     */
    public void returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan == null)
            return;

        loan.returnBook();
        loan.getBookCopy().checkIn();

        loanRepository.save(loan);
        recordLoanHistory(loan.getMember(), loan, "RETURN");
    }

    /**
     * Renew a loan
     * 
     * @return True if renewal was successful
     */
    public boolean renewLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan == null)
            return false;

        boolean success = loan.renew();
        if (success) {
            loanRepository.save(loan);
            recordLoanHistory(loan.getMember(), loan, "RENEWAL");
        }

        return success;
    }

    /**
     * Get overdue loans for a member
     * 
     * @return List of overdue loans
     */
    public List<Loan> getOverdueLoans(Member member) {
        // In a real implementation, query loan repository
        return List.of(); // Placeholder
    }

    /**
     * Send overdue notices for all overdue loans
     */
    public void sendOverdueNotices() {
        // Implementation omitted for brevity
    }

    /**
     * Handle transaction errors
     * 
     * @return True if error was handled successfully
     */
    public boolean handleTransactionError(String errorType, String details, Long transactionId) {
        // Logic to handle error
        return false; // Placeholder
    }

    private void recordLoanHistory(Member member, Loan loan, String action) {
        LoanHistory history = LoanHistory.builder()
                .member(member)
                .loan(loan)
                .book(loan.getBookCopy().getBook())
                .actionDate(LocalDate.now())
                .action(action)
                .build();

        // In a real implementation, save to repository
    }
}
