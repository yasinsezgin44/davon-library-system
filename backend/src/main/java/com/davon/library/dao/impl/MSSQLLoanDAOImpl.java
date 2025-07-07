package com.davon.library.dao.impl;

import com.davon.library.dao.LoanDAO;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLLoanDAOImpl implements LoanDAO {
    @Override
    public Optional<Loan> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Loan save(Loan loan) {
        return loan;
    }

    @Override
    public List<Loan> findByMember(Member member) {
        return List.of();
    }

    @Override
    public List<Loan> findActiveLoansByMember(Member member) {
        return List.of();
    }

    @Override
    public Optional<Loan> findByBookCopyAndMember(BookCopy bookCopy, Member member) {
        return Optional.empty();
    }

    @Override
    public Optional<Loan> findActiveByBookCopy(BookCopy bookCopy) {
        return Optional.empty();
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDate date) {
        return List.of();
    }

    @Override
    public List<Loan> findLoansDueOn(LocalDate date) {
        return List.of();
    }

    @Override
    public List<Loan> findByStatus(Loan.LoanStatus status) {
        return List.of();
    }

    @Override
    public long countActiveLoansByMember(Member member) {
        return 0;
    }

    @Override
    public Loan update(Loan loan) {
        return loan;
    }

    @Override
    public void deleteById(Long id) {
        // No-op for now
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<Loan> findAll() {
        return List.of();
    }
}