package com.davon.library.service;

import com.davon.library.mapper.LoanMapper;
import com.davon.library.dto.LoanResponseDTO;
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
import jakarta.ws.rs.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final int MAX_LOANS_PER_MEMBER = 3;
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
    public LoanResponseDTO borrowBook(Long bookId, String username) {
        log.info("Member {} attempting to borrow book {}", username, bookId);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Member not found with username: " + username));

        return checkoutBook(bookId, member.getId());
    }

    @Transactional
    public LoanResponseDTO checkoutBook(Long bookId, Long userId) {
        log.info("Attempting to check out book {} for user {}", bookId, userId);

        Member member = memberRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("Member not found for the given user"));

        if (member.getUser() == null) {
            log.error("Data inconsistency: Member record found for ID {}, but it has no associated User.", userId);
            throw new InternalServerErrorException("Could not process loan due to a data inconsistency issue.");
        }

        log.info("Found member: {}", member.getUser().getUsername());

        if (loanRepository.countActiveLoansByMember(member) >= (long) MAX_LOANS_PER_MEMBER) {
            log.warn("Member {} has reached the maximum number of active loans.", userId);
            throw new BadRequestException("Member has reached the maximum number of active loans.");
        }

        if (loanRepository.existsActiveLoanForMemberAndBook(member, bookId)) {
            log.warn("Member {} already has an active loan for book {}.", userId, bookId);
            throw new BadRequestException("Member already has an active loan for this book.");
        }

        if (member.getFineBalance() != null && member.getFineBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Member {} has outstanding fines.", userId);
            throw new BadRequestException("Member has outstanding fines.");
        }

        BookCopy bookCopy = bookCopyRepository.findAvailableByBookId(bookId)
                .orElseThrow(() -> new NotFoundException("No available copies for this book."));
        log.info("Found available book copy: {}", bookCopy.getId());

        bookCopy.setStatus(CopyStatus.CHECKED_OUT);

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.persist(loan);

        log.info(
                "{} borrowed '{}'",
                member.getUser().getFullName(),
                bookCopy.getBook().getTitle());
        log.info("Book checked out successfully. Loan ID: {}", loan.getId());
        return LoanMapper.toResponseDTO(loan);
    }

    public List<LoanResponseDTO> getCurrentLoansByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Member not found with username: " + username));
        return loanRepository.findActiveLoansByMember(member).stream()
                .map(LoanMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDTO> getLoanHistoryByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Member not found with username: " + username));
        return loanRepository.findReturnedLoansByMember(member).stream()
                .map(LoanMapper::toResponseDTO)
                .collect(Collectors.toList());
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
