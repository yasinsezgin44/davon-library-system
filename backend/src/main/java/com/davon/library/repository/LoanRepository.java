package com.davon.library.repository;

import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.LoanStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public interface LoanRepository extends PanacheRepository<Loan> {

    default List<Loan> findByMember(Member member) {
        return list("member", member);
    }

    default List<Loan> findActiveLoansByMember(Member member) {
        return list("member = ?1 and status = ?2", member, LoanStatus.ACTIVE);
    }

    default long countActiveLoansByMember(Member member) {
        return count("member = ?1 and status = ?2", member, LoanStatus.ACTIVE);
    }

    default List<Loan> findOverdueLoans() {
        return list("dueDate < ?1 and status = ?2", LocalDate.now(), LoanStatus.ACTIVE);
    }

    default List<Loan> findByCheckoutDateBetween(LocalDate startDate, LocalDate endDate) {
        return list("checkoutDate >= ?1 and checkoutDate <= ?2", startDate, endDate);
    }
}
