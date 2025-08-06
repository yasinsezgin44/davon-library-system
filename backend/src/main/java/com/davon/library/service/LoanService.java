package com.davon.library.service;

import com.davon.library.repository.LoanRepository;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.FineRepository;
import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class LoanService {

    private static final Logger logger = Logger.getLogger(LoanService.class.getName());
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final int MAX_LOANS_PER_MEMBER = 5;

    @Inject
    LoanRepository loanRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    FineRepository fineRepository;

    @Inject
    UserService userService;

    @Inject
    BookService bookService;

    @Inject
    NotificationService notificationService;

    @Inject
    ReceiptService receiptService;

    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) throws BusinessException {
        try {
            Member member = validateMemberForCheckout(memberId);
            BookCopy bookCopy = findAvailableBookCopy(bookId);
            Loan loan = createLoan(member, bookCopy);

            bookCopy.setStatus("CHECKED_OUT");
            bookCopyRepository.persist(bookCopy);

            loanRepository.persist(loan);

            notificationService.sendCheckoutNotification(member, loan);

            logger.info("Book checked out successfully - Loan ID: " + loan.getId());
            return loan;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database error during checkout", e);
            throw new BusinessException("Failed to checkout book due to system error");
        }
    }

    @Transactional
    public void returnBook(Long loanId) throws BusinessException {
        try {
            Loan loan = validateLoanForReturn(loanId);
            Fine fine = null;
            if (loan.getDueDate().isBefore(LocalDate.now())) {
                fine = calculateAndCreateLateFine(loan);
                fineRepository.persist(fine);
                Member member = loan.getMember();
                member.setFineBalance(member.getFineBalance().add(fine.getAmount()));
                userService.updateUser(member.getId(), member.getUser());
            }

            loan.setStatus("RETURNED");
            loan.setReturnDate(LocalDate.now());
            loanRepository.persist(loan);

            BookCopy bookCopy = loan.getBookCopy();
            bookCopy.setStatus("AVAILABLE");
            bookCopyRepository.persist(bookCopy);

            notificationService.sendReturnNotification(loan.getMember(), loan);

            logger.info("Book returned successfully - Loan ID: " + loan.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during book return", e);
            throw new BusinessException("Failed to return book due to system error");
        }
    }

    public List<Loan> getMemberLoans(Long memberId) {
        return loanRepository.list("member.id", memberId);
    }

    public List<Loan> getMemberActiveLoans(Long memberId) {
        return loanRepository.list("member.id = ?1 and status = 'ACTIVE'", memberId);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    @Transactional
    public Loan renewLoan(Long loanId) throws BusinessException {
        try {
            Loan loan = loanRepository.findById(loanId);
            if (loan == null) {
                throw new BusinessException("Loan not found with ID: " + loanId);
            }
            if (!"ACTIVE".equals(loan.getStatus())) {
                throw new BusinessException("Only active loans can be renewed");
            }

            Member member = loan.getMember();
            if (member.getFineBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("Member has outstanding fines of $" + member.getFineBalance());
            }

            loan.setDueDate(loan.getDueDate().plusDays(DEFAULT_LOAN_PERIOD_DAYS));
            loanRepository.persist(loan);
            notificationService.sendRenewalNotification(loan.getMember(), loan);
            logger.info("Loan renewed successfully - Loan ID: " + loan.getId());
            return loan;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during loan renewal", e);
            throw new BusinessException("Failed to renew loan due to system error");
        }
    }

    private Member validateMemberForCheckout(Long memberId) throws BusinessException {
        User user = userService.findById(memberId);
        if (user == null || user.getMember() == null) {
            throw new BusinessException("Member not found with ID: " + memberId);
        }
        Member member = user.getMember();
        if (member.getFineBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Member has outstanding fines of $" + member.getFineBalance());
        }
        if (loanRepository.count("member.id = ?1 and status = 'ACTIVE'", memberId) >= MAX_LOANS_PER_MEMBER) {
            throw new BusinessException("Member has reached maximum loan limit of " + MAX_LOANS_PER_MEMBER);
        }
        return member;
    }

    private BookCopy findAvailableBookCopy(Long bookId) throws BusinessException {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BusinessException("Book not found with ID: " + bookId);
        }
        return bookCopyRepository.find("book.id = ?1 and status = 'AVAILABLE'", bookId).firstResultOptional()
                .orElseThrow(() -> new BusinessException("No available copies of book: " + book.getTitle()));
    }

    private Loan createLoan(Member member, BookCopy bookCopy) {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(DEFAULT_LOAN_PERIOD_DAYS));
        loan.setStatus("ACTIVE");
        loan.setRenewalCount(0);
        return loan;
    }

    private Loan validateLoanForReturn(Long loanId) throws BusinessException {
        Loan loan = loanRepository.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Loan not found with ID: " + loanId);
        }
        if (!"ACTIVE".equals(loan.getStatus())) {
            throw new BusinessException("Only active loans can be returned");
        }
        return loan;
    }

    private Fine calculateAndCreateLateFine(Loan loan) {
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        BigDecimal fineAmount = new BigDecimal(overdueDays).multiply(new BigDecimal("0.25"));

        Fine fine = new Fine();
        fine.setMember(loan.getMember());
        fine.setLoan(loan);
        fine.setAmount(fineAmount);
        fine.setReason("OVERDUE");
        fine.setIssueDate(LocalDate.now());
        fine.setStatus("PENDING");
        return fine;
    }
}
