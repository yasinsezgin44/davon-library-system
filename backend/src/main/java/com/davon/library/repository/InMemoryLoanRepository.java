package com.davon.library.repository;

import com.davon.library.model.*;
import com.davon.library.service.LoanRepository;
import java.util.*;

public class InMemoryLoanRepository implements LoanRepository {
    private final Map<Long, Loan> loans = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Loan save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(nextId++);
        }
        loans.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public Optional<Loan> findActiveLoanByMemberAndBookCopy(Member member, BookCopy copy) {
        return loans.values().stream()
                .filter(loan -> loan.getMember().equals(member)
                        && loan.getBookCopy().equals(copy)
                        && loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .findFirst();
    }

    public List<Loan> findOverdueLoans(LocalDate date) {
        return loans.values().stream()
                .filter(loan -> loan.getDueDate().isBefore(date)
                        && loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .toList();
    }
}
