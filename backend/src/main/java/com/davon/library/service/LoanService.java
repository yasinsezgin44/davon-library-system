package com.davon.library.service;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.model.enums.FineReason;
import com.davon.library.model.enums.FineStatus;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.FineRepository;
import com.davon.library.repository.LoanRepository;
import com.davon.library.repository.MemberRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final int MAX_LOANS_PER_MEMBER = 5;
    private static final BigDecimal LATE_FEE_PER_DAY = new BigDecimal("0.25");

    @Inject
    LoanRepository loanRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    FineRepository fineRepository;

    @Inject
    MemberRepository memberRepository;

    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) {
        log.info("Attempting to check out book {} for member {}", bookId, memberId);

        Member member = memberRepository.findByIdOptional(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        if (loanRepository.countActiveLoansByMember(member) >= MAX_LOANS_PER_MEMBER) {
            throw new BadRequestException("Member has reached the maximum number of active loans.");
        }

        if (member.getFineBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Member has outstanding fines.");
        }

        BookCopy bookCopy = bookCopyRepository.findAvailableByBookId(bookId)
                .orElseThrow(() -> new NotFoundException("No available copies for this book."));

        bookCopy.setStatus(CopyStatus.CHECKED_OUT);

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.persist(loan);

        log.info("Book checked out successfully. Loan ID: {}", loan.getId());
        return loan;
    }

    @Transactional
    public void returnBook(Long loanId) {
        log.info("Returning book for loan {}", loanId);
        Loan loan = loanRepository.findByIdOptional(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BadRequestException("Loan is not active.");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(CopyStatus.AVAILABLE);

        if (loan.getDueDate().isBefore(LocalDate.now())) {
            createFineForOverdueLoan(loan);
        }
    }

    private void createFineForOverdueLoan(Loan loan) {
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        if (overdueDays > 0) {
            BigDecimal fineAmount = LATE_FEE_PER_DAY.multiply(new BigDecimal(overdueDays));

            Fine fine = new Fine();
            fine.setMember(loan.getMember());
            fine.setLoan(loan);
            fine.setAmount(fineAmount);
            fine.setReason(FineReason.OVERDUE);
            fine.setIssueDate(LocalDate.now());
            fine.setStatus(FineStatus.PENDING);
            fineRepository.persist(fine);

            Member member = loan.getMember();
            member.setFineBalance(member.getFineBalance().add(fineAmount));
            log.info("Created a fine of {} for member {}", fineAmount, member.getId());
        }
    }

    public List<Loan> getLoansForMember(Long memberId) {
        Member member = memberRepository.findByIdOptional(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        return loanRepository.findByMember(member);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }
}
