package com.davon.library.service;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    List<Loan> findAllByMember(Member member);

    Optional<Loan> findActiveLoanByMemberAndBookCopy(Member member, BookCopy copy);

    List<Loan> findOverdueLoans(LocalDate date);
}