package com.davon.library.service;

import com.davon.library.dao.LoanDAO;
import com.davon.library.dao.BookCopyDAO;
import com.davon.library.dao.FineDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for managing book loans (checkout and return processes).
 * Follows SOLID principles and uses DAO pattern for data access.
 */
@ApplicationScoped
public class LoanService {

    private static final Logger logger = Logger.getLogger(LoanService.class.getName());
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final double DAILY_FINE_RATE = 0.25; // $0.25 per day
    private static final int MAX_LOANS_PER_MEMBER = 5;

    private final LoanDAO loanDAO;
    private final BookCopyDAO bookCopyDAO;
    private final FineDAO fineDAO;
    private final UserService userService;
    private final BookService bookService;
    private final NotificationService notificationService;
    private final ReceiptService receiptService;

    @Inject
    public LoanService(LoanDAO loanDAO, BookCopyDAO bookCopyDAO, FineDAO fineDAO,
            UserService userService, BookService bookService,
            NotificationService notificationService, ReceiptService receiptService) {
        this.loanDAO = loanDAO;
        this.bookCopyDAO = bookCopyDAO;
        this.fineDAO = fineDAO;
        this.userService = userService;
        this.bookService = bookService;
        this.notificationService = notificationService;
        this.receiptService = receiptService;
    }

    /**
     * Checks out a book to a member.
     * 
     * @param bookId   the ID of the book to checkout
     * @param memberId the ID of the member
     * @return the created loan
     * @throws BusinessException if checkout is not allowed
     */
    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) throws BusinessException {
        try {
            // 1. Validate member can borrow
            Member member = validateMemberForCheckout(memberId);

            // 2. Find and validate book availability
            BookCopy bookCopy = findAvailableBookCopy(bookId);

            // 3. Create loan record
            Loan loan = createLoan(member, bookCopy);

            // 4. Update book copy status
            bookCopy.checkOut();
            bookCopyDAO.update(bookCopy);

            // 5. Save loan record
            loan = loanDAO.save(loan);

            // 6. Send notification
            notificationService.sendCheckoutNotification(member, loan);

            logger.info("Book checked out successfully - Loan ID: " + loan.getId());
            return loan;

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Database error during checkout", e);
            throw new BusinessException("Failed to checkout book due to system error");
        }
    }

    /**
     * Returns a book and calculates any fines.
     * 
     * @param loanId the ID of the loan to return
     * @return the receipt for the return transaction
     * @throws BusinessException if return is not allowed
     */
    @Transactional
    public Receipt returnBook(Long loanId) throws BusinessException {
        try {
            // 1. Find active loan
            Loan loan = validateLoanForReturn(loanId);

            logger.info("DEBUG: Returning loan ID: " + loanId + ", Due Date: " + loan.getDueDate() + ", Today: "
                    + LocalDate.now());

            // 2. Calculate any fines
            Fine fine = null;
            if (loan.getDueDate().isBefore(LocalDate.now())) {
                logger.info("DEBUG: Loan is overdue, calculating fine");
                fine = calculateAndCreateLateFine(loan);
                logger.info("DEBUG: Fine created with amount: " + fine.getAmount());
                fineDAO.save(fine);

                // Update member's fine balance
                Member member = loan.getMember();
                member.addFine(fine.getAmount());
                userService.updateUser(member.getId(), member);
            } else {
                logger.info("DEBUG: Loan is not overdue, no fine calculated");
            }

            // 3. Update loan status
            loan.returnBook();
            loanDAO.update(loan);

            // 4. Update book copy availability
            BookCopy bookCopy = loan.getBookCopy();
            bookCopy.checkIn();
            bookCopyDAO.update(bookCopy);

            // 5. Generate receipt
            Receipt receipt = receiptService.generateReturnReceipt(loan, fine);

            // 6. Send notification
            notificationService.sendReturnNotification(loan.getMember(), loan);

            logger.info("Book returned successfully - Loan ID: " + loan.getId());
            return receipt;

        } catch (DAOException | UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Error during book return", e);
            throw new BusinessException("Failed to return book due to system error");
        }
    }

    /**
     * Gets all loans for a member.
     * 
     * @param memberId the member ID
     * @return list of loans
     */
    public List<Loan> getMemberLoans(Long memberId) {
        Member member = Member.builder().id(memberId).build();
        return loanDAO.findByMember(member);
    }

    /**
     * Gets all active loans for a member.
     * 
     * @param memberId the member ID
     * @return list of active loans
     */
    public List<Loan> getMemberActiveLoans(Long memberId) {
        Member member = Member.builder().id(memberId).build();
        return loanDAO.findActiveLoansByMember(member);
    }

    /**
     * Gets all overdue loans.
     * 
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        try {
            return loanDAO.findOverdueLoans(LocalDate.now());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting overdue loans", e);
            throw new RuntimeException("Service error");
        }
    }

    /**
     * Renews a loan if allowed.
     * 
     * @param loanId the loan ID
     * @return the renewed loan
     * @throws BusinessException if renewal is not allowed
     */
    @Transactional
    public Loan renewLoan(Long loanId) throws BusinessException {
        try {
            Optional<Loan> loanOpt = loanDAO.findById(loanId);
            if (loanOpt.isEmpty()) {
                throw new BusinessException("Loan not found with ID: " + loanId);
            }

            Loan loan = loanOpt.get();

            // Check if renewal is allowed
            if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
                throw new BusinessException("Only active loans can be renewed");
            }

            if (loan.getRenewalCount() >= 2) {
                throw new BusinessException("Maximum renewals reached");
            }

            // Check if member has outstanding fines
            Member member = loan.getMember();
            if (member.getFineBalance() > 0) {
                throw new BusinessException("Cannot renew loan with outstanding fines");
            }

            // Extend due date
            loan.setDueDate(loan.getDueDate().plusDays(DEFAULT_LOAN_PERIOD_DAYS));
            loan.setRenewalCount(loan.getRenewalCount() + 1);

            loan = loanDAO.update(loan);

            notificationService.sendRenewalNotification(member, loan);

            logger.info("Loan renewed successfully - Loan ID: " + loan.getId());
            return loan;

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error renewing loan", e);
            throw new BusinessException("Failed to renew loan due to system error");
        }
    }

    // Private helper methods

    private Member validateMemberForCheckout(Long memberId) throws BusinessException {
        User user = userService.findById(memberId);
        if (user == null) {
            throw new BusinessException("Member not found with ID: " + memberId);
        }

        if (!(user instanceof Member)) {
            throw new BusinessException("User is not a member");
        }

        Member member = (Member) user;

        // Check if member has outstanding fines
        if (member.getFineBalance() > 0) {
            throw new BusinessException("Member has outstanding fines of $" + member.getFineBalance());
        }

        // Check loan limit
        long activeLoanCount = loanDAO.countActiveLoansByMember(member);
        if (activeLoanCount >= MAX_LOANS_PER_MEMBER) {
            throw new BusinessException("Member has reached maximum loan limit of " + MAX_LOANS_PER_MEMBER);
        }

        return member;
    }

    private BookCopy findAvailableBookCopy(Long bookId) throws BusinessException {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BusinessException("Book not found with ID: " + bookId);
        }

        List<BookCopy> availableCopies = bookCopyDAO.findAvailableByBook(book);
        if (availableCopies.isEmpty()) {
            throw new BusinessException("No available copies of book: " + book.getTitle());
        }

        return availableCopies.get(0); // Return first available copy
    }

    private Loan createLoan(Member member, BookCopy bookCopy) {
        return Loan.builder()
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(DEFAULT_LOAN_PERIOD_DAYS))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();
    }

    private Loan validateLoanForReturn(Long loanId) throws BusinessException {
        Optional<Loan> loanOpt = loanDAO.findById(loanId);
        if (loanOpt.isEmpty()) {
            throw new BusinessException("Loan not found with ID: " + loanId);
        }

        Loan loan = loanOpt.get();
        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new BusinessException("Only active loans can be returned");
        }

        return loan;
    }

    private Fine calculateAndCreateLateFine(Loan loan) {
        int overdueDays = loan.getOverdueDays();
        double fineAmount = overdueDays * DAILY_FINE_RATE;

        return Fine.builder()
                .member(loan.getMember())
                .amount(fineAmount)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30)) // 30 days to pay fine
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    /**
     * Service-specific exception class.
     */
    public static class LoanServiceException extends Exception {
        public LoanServiceException(String message) {
            super(message);
        }

        public LoanServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}