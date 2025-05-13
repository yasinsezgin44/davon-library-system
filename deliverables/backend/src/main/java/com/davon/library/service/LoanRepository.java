package com.davon.library.service;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;

import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);

    Optional<Loan> findActiveLoanByMemberAndBookCopy(Member member, BookCopy copy);
}