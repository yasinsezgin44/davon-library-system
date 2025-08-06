package com.davon.library.service;

import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.repository.FineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class FineService {

    @Inject
    FineRepository fineRepository;

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");

    @Transactional
    public Fine createOverdueFine(Loan loan) {
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        if (overdueDays > 0) {
            BigDecimal amount = DAILY_FINE_RATE.multiply(new BigDecimal(overdueDays));
            Fine fine = new Fine();
            fine.setMember(loan.getMember());
            fine.setLoan(loan);
            fine.setAmount(amount);
            fine.setReason("OVERDUE");
            fine.setIssueDate(LocalDate.now());
            fine.setStatus("PENDING");
            fineRepository.persist(fine);
            return fine;
        }
        return null;
    }

    public Fine getFineById(Long id) {
        return fineRepository.findById(id);
    }

    public List<Fine> getFinesByMember(Long memberId) {
        return fineRepository.list("member.id", memberId);
    }
}
