package com.davon.library.service;

import com.davon.library.model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Service for handling book loans (checkout and return).
 */
public class LoanService {
    private final Set<Loan> loans = new HashSet<>();

    /**
     * Checks out a book copy to a member.
     * 
     * @return the created Loan, or null if not available
     */
    public Loan checkoutBook(Member member, BookCopy bookCopy, int loanPeriodDays) {
        if (bookCopy.getStatus() != BookCopy.CopyStatus.AVAILABLE) {
            return null; // Not available
        }
        Loan loan = Loan.builder()
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(loanPeriodDays))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();
        loans.add(loan);
        member.getLoans().add(loan);
        bookCopy.setStatus(BookCopy.CopyStatus.CHECKED_OUT);
        return loan;
    }

    /**
     * Returns a book copy for a member.
     * 
     * @return the updated Loan, or null if not found
     */
    public Loan returnBook(Member member, BookCopy bookCopy) {
        Optional<Loan> loanOpt = loans.stream()
                .filter(l -> l.getMember().equals(member)
                        && l.getBookCopy().equals(bookCopy)
                        && l.getStatus() == Loan.LoanStatus.ACTIVE)
                .findFirst();
        if (loanOpt.isEmpty()) {
            return null;
        }
        Loan loan = loanOpt.get();
        loan.returnBook();
        bookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        return loan;
    }

    // Additional helper methods can be added as needed
    public Set<Loan> getLoans() {
        return loans;
    }
}