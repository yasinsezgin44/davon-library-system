package com.davon.library.repository;

import com.davon.library.model.*;
import java.util.List;
import java.time.LocalDate;

public interface LoanHistoryRepository {
    LoanHistory save(LoanHistory loanHistory);

    List<LoanHistory> findByMember(Member member);

    List<Loan> findActiveLoans(Member member);

    List<Loan> findPastLoans(Member member);

    float calculateAverageReturnTime(Member member);
}
