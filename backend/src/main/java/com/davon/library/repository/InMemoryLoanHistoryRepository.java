package com.davon.library.repository;

import com.davon.library.model.*;
import com.davon.library.repository.LoanHistoryRepository;
import java.util.*;
import java.time.LocalDate;

public class InMemoryLoanHistoryRepository implements LoanHistoryRepository {
    private final Map<Long, LoanHistory> loanHistories = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public LoanHistory save(LoanHistory loanHistory) {
        if (loanHistory.getId() == null) {
            loanHistory.setId(nextId++);
        }
        loanHistories.put(loanHistory.getId(), loanHistory);
        return loanHistory;
    }

    @Override
    public List<LoanHistory> findByMember(Member member) {
        return loanHistories.values().stream()
                .filter(history -> history.getMember().equals(member))
                .toList();
    }

    @Override
    public List<Loan> findActiveLoans(Member member) {
        // Implementation
        return new ArrayList<>();
    }

    @Override
    public List<Loan> findPastLoans(Member member) {
        // Implementation
        return new ArrayList<>();
    }

    @Override
    public float calculateAverageReturnTime(Member member) {
        // Implementation
        return 0f;
    }
}
