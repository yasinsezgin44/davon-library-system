package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class BorrowingService {
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final BookCopyRepository bookCopyRepository;
    private final FineService fineService;
    private final NotificationService notificationService;
    private final ReceiptService receiptService;

    // Check out a book with authentication
    public Loan checkOutBook(Member member, BookCopy bookCopy, int loanPeriodDays, User requestingUser) {
        // Check permissions
        if (!hasCheckoutPermission(requestingUser, member)) {
            throw new SecurityException("Insufficient permissions");
        }

        // Check if book is available
        if (bookCopy.getStatus() != BookCopy.CopyStatus.AVAILABLE) {
            return null;
        }

        // Check if member has exceeded limits
        if (hasExceededBorrowingLimit(member)) {
            return null;
        }

        Loan loan = createLoan(member, bookCopy, loanPeriodDays);
        notificationService.sendCheckoutConfirmation(member, bookCopy);
        return loan;
    }

    public Loan renewLoan(Long loanId, User requestingUser) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        // Check permissions
        if (!hasRenewPermission(requestingUser, loan.getMember())) {
            throw new SecurityException("Insufficient permissions");
        }

        // Check renewal eligibility
        if (loan.getRenewalCount() >= 2) {
            return null; // Maximum renewals reached
        }

        // Renew the loan
        loan.renew();
        return loanRepository.save(loan);
    }

    public List<Loan> getLoanHistory(Member member, User requestingUser) {
        // Check permissions
        if (!hasViewHistoryPermission(requestingUser, member)) {
            throw new SecurityException("Insufficient permissions");
        }

        return loanRepository.findAllByMember(member);
    }

    public Receipt payFine(Fine fine, String paymentMethod, User requestingUser) {
        // Check permissions
        if (!hasPayFinePermission(requestingUser, fine)) {
            throw new SecurityException("Insufficient permissions");
        }

        // Process payment
        Transaction transaction = new Transaction();
        transaction.setAmount(fine.getAmount());
        transaction.setType(Transaction.TransactionType.FINE_PAYMENT);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setDate(LocalDate.now());

        fine.setStatus(Fine.FineStatus.PAID);

        return receiptService.createReceipt(transaction);
    }

    // Helper methods for permission checks
    private boolean hasCheckoutPermission(User requestingUser, Member member) {
        return requestingUser.equals(member) || requestingUser instanceof Librarian;
    }

    private boolean hasRenewPermission(User requestingUser, Member member) {
        return requestingUser.equals(member) || requestingUser instanceof Librarian;
    }

    private boolean hasViewHistoryPermission(User requestingUser, Member member) {
        return requestingUser.equals(member) || requestingUser instanceof Librarian;
    }

    private boolean hasPayFinePermission(User requestingUser, Fine fine) {
        // Implement appropriate permission logic
        return true;
    }

    private boolean hasExceededBorrowingLimit(Member member) {
        long activeLoans = member.getLoans().stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE ||
                        loan.getStatus() == Loan.LoanStatus.OVERDUE)
                .count();

        return activeLoans >= 5; // Example limit
    }

    private Loan createLoan(Member member, BookCopy bookCopy, int loanPeriodDays) {
        Loan loan = Loan.builder()
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(loanPeriodDays))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();

        bookCopy.setStatus(BookCopy.CopyStatus.CHECKED_OUT);
        bookCopyRepository.save(bookCopy);

        if (member.getLoans() != null) {
            member.getLoans().add(loan);
        }

        return loanRepository.save(loan);
    }
}
