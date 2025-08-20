package com.davon.library.service;

import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.FineReason;
import com.davon.library.model.enums.FineStatus;
import com.davon.library.repository.FineRepository;
import com.davon.library.repository.MemberRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FineService {

    private static final Logger log = LoggerFactory.getLogger(FineService.class);

    @Inject
    FineRepository fineRepository;

    @Inject
    MemberRepository memberRepository;

    @Transactional
    public Fine createFine(Fine fine) {
        log.info("Creating fine for member {}", fine.getMember().getId());
        fineRepository.persist(fine);
        return fine;
    }

    @Transactional
    public Fine payFine(Long fineId) {
        log.info("Processing payment for fine {}", fineId);
        Fine fine = fineRepository.findByIdOptional(fineId)
                .orElseThrow(() -> new NotFoundException("Fine not found"));

        fine.setStatus(FineStatus.PAID);

        Member member = fine.getMember();
        BigDecimal currentBalance = member.getFineBalance() == null ? BigDecimal.ZERO : member.getFineBalance();
        member.setFineBalance(currentBalance.subtract(fine.getAmount()));

        return fine;
    }

    public List<Fine> getFinesForMember(Long memberId) {
        Member member = memberRepository.findByIdOptional(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        return fineRepository.findByMember(member);
    }

    @Transactional
    public Fine createOverdueFine(Loan loan) {
        Fine fine = new Fine();
        fine.setMember(loan.getMember());
        fine.setLoan(loan);
        fine.setAmount(new BigDecimal("25.00")); // Or some calculated amount
        fine.setReason(FineReason.OVERDUE);
        fine.setIssueDate(LocalDate.now());
        fine.setDueDate(LocalDate.now().plusDays(30));
        fine.setStatus(FineStatus.PENDING);
        return createFine(fine);
    }
}
