package com.davon.library.dao.impl;

import com.davon.library.dao.LoanDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of LoanDAO.
 * Thread-safe implementation using ConcurrentHashMap.
 */
@ApplicationScoped
public class InMemoryLoanDAOImpl extends AbstractInMemoryDAO<Loan> implements LoanDAO {

    @Override
    protected String getEntityName() {
        return "Loan";
    }

    @Override
    protected Loan cloneEntity(Loan entity) {
        if (entity == null) {
            return null;
        }

        return Loan.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .member(entity.getMember()) // Shallow copy for now
                .bookCopy(entity.getBookCopy()) // Shallow copy for now
                .checkoutDate(entity.getCheckoutDate())
                .dueDate(entity.getDueDate())
                .returnDate(entity.getReturnDate())
                .status(entity.getStatus())
                .renewalCount(entity.getRenewalCount())
                .build();
    }

    @Override
    protected void validateEntity(Loan entity) throws DAOException {
        super.validateEntity(entity);

        if (entity.getMember() == null) {
            throw new DAOException("Loan must have a member", "validate", getEntityName());
        }

        if (entity.getBookCopy() == null) {
            throw new DAOException("Loan must have a book copy", "validate", getEntityName());
        }

        if (entity.getCheckoutDate() == null) {
            throw new DAOException("Loan must have a checkout date", "validate", getEntityName());
        }

        if (entity.getDueDate() == null) {
            throw new DAOException("Loan must have a due date", "validate", getEntityName());
        }

        if (entity.getStatus() == null) {
            throw new DAOException("Loan must have a status", "validate", getEntityName());
        }
    }

    @Override
    public List<Loan> findByMember(Member member) {
        if (member == null || member.getId() == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(loan -> loan.getMember() != null &&
                        member.getId().equals(loan.getMember().getId()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findActiveLoansByMember(Member member) {
        return findByMember(member).stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Loan> findByBookCopyAndMember(BookCopy bookCopy, Member member) {
        if (bookCopy == null || bookCopy.getId() == null ||
                member == null || member.getId() == null) {
            return Optional.empty();
        }

        return storage.values().stream()
                .filter(loan -> loan.getBookCopy() != null &&
                        bookCopy.getId().equals(loan.getBookCopy().getId()) &&
                        loan.getMember() != null &&
                        member.getId().equals(loan.getMember().getId()))
                .map(this::cloneEntity)
                .findFirst();
    }

    @Override
    public Optional<Loan> findActiveByBookCopy(BookCopy bookCopy) {
        if (bookCopy == null || bookCopy.getId() == null) {
            return Optional.empty();
        }

        return storage.values().stream()
                .filter(loan -> loan.getBookCopy() != null &&
                        bookCopy.getId().equals(loan.getBookCopy().getId()) &&
                        loan.getStatus() == Loan.LoanStatus.ACTIVE)
                .map(this::cloneEntity)
                .findFirst();
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }

        final LocalDate today = currentDate;
        return storage.values().stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE &&
                        loan.getDueDate() != null &&
                        loan.getDueDate().isBefore(today))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findLoansDueOn(LocalDate dueDate) {
        if (dueDate == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(loan -> dueDate.equals(loan.getDueDate()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findByStatus(Loan.LoanStatus status) {
        if (status == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(loan -> status.equals(loan.getStatus()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveLoansByMember(Member member) {
        return findActiveLoansByMember(member).size();
    }
}