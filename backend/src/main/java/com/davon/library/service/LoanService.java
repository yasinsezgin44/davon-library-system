package com.davon.library.service;

import com.davon.library.model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Service for handling book loans (checkout and return).
 */
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;

    public LoanService(LoanRepository loanRepository, BookCopyRepository bookCopyRepository) {
        this.loanRepository = loanRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    /**
     * Checks out a book copy to a member.
     * 
     * @return the created Loan, or null if not available
     */
    public Loan checkoutBook(Member member, BookCopy bookCopy, int loanPeriodDays) {
        if (member == null || bookCopy == null) {
            throw new IllegalArgumentException("Member and BookCopy cannot be null");
        }

        if (!member.isActive()) {
            throw new IllegalStateException("Member account is not active");
        }

        if (bookCopy.getStatus() != BookCopy.CopyStatus.AVAILABLE) {
            return null;
        }
        Loan loan = Loan.builder()
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(loanPeriodDays))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();

        if (member.getLoans() != null) {
            member.getLoans().add(loan);
        }

        bookCopy.setStatus(BookCopy.CopyStatus.CHECKED_OUT);
        bookCopyRepository.save(bookCopy);
        return loanRepository.save(loan);
    }

    /**
     * Returns a book copy for a member.
     * 
     * @return the updated Loan, or null if not found
     */
    public Loan returnBook(Member member, BookCopy bookCopy) {
        Optional<Loan> loanOpt = loanRepository.findActiveLoanByMemberAndBookCopy(member, bookCopy);

        if (loanOpt.isEmpty()) {
            return null;
        }

        Loan loan = loanOpt.get();
        loan.returnBook();
        bookCopy.checkIn(); // bookCopy status is now AVAILABLE

        loanRepository.save(loan);
        bookCopyRepository.save(bookCopy); // Save the updated BookCopy status
        return loan;
    }
}