package com.davon.library.repository;

import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

    public List<Fine> findByMember(Member member) {
        return find("member", member).list();
    }

    public Fine findByLoan(Loan loan) {
        return find("loan", loan).firstResult();
    }

    public List<Fine> findPendingByMember(Member member) {
        return find("member = ?1 AND status = ?2", member, "PENDING").list();
    }

    public List<Fine> findByStatus(String status) {
        return find("status", status).list();
    }

    public List<Fine> findOverdueFines() {
        return find("dueDate < ?1 AND status = ?2", LocalDate.now(), "PENDING").list();
    }

    public List<Fine> findByIssueDateBetween(LocalDate startDate, LocalDate endDate) {
        return find("issueDate >= ?1 AND issueDate <= ?2", startDate, endDate).list();
    }

    public Double getTotalOutstandingAmount(Member member) {
        return getEntityManager().createQuery(
                "SELECT SUM(f.amount) FROM Fine f WHERE f.member = :member AND f.status = :status", Double.class)
                .setParameter("member", member)
                .setParameter("status", "PENDING")
                .getSingleResult();
    }

    public List<Fine> findByReason(String reason) {
        return find("reason", reason).list();
    }
}
