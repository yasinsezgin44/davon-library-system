package com.davon.library.repository;

import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.FineStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

    public List<Fine> findByMember(Member member) {
        return list("member", member);
    }

    public Optional<Fine> findByLoan(Loan loan) {
        return find("loan", loan).firstResultOptional();
    }

    public List<Fine> findPendingByMember(Member member) {
        return list("member = ?1 AND status = ?2", member, FineStatus.PENDING);
    }

    public List<Fine> findOverdueFines() {
        return list("dueDate < ?1 AND status = ?2", LocalDate.now(), FineStatus.PENDING);
    }

    public List<Fine> findByIssueDateBetween(LocalDate startDate, LocalDate endDate) {
        return list("issueDate >= ?1 and issueDate <= ?2", startDate, endDate);
    }
}
