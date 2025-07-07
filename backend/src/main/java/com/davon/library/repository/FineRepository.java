package com.davon.library.repository;

import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Fine entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based FineDAO implementation.
 */
@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

    /**
     * Finds fines by member.
     * 
     * @param member the member
     * @return list of fines for the member
     */
    public List<Fine> findByMember(Member member) {
        return find("member", member).list();
    }

    /**
     * Finds pending fines by member.
     * 
     * @param member the member
     * @return list of pending fines for the member
     */
    public List<Fine> findPendingByMember(Member member) {
        return find("member = ?1 AND status = ?2", member, Fine.FineStatus.PENDING).list();
    }

    /**
     * Finds fines by status.
     * 
     * @param status the fine status
     * @return list of fines with the given status
     */
    public List<Fine> findByStatus(Fine.FineStatus status) {
        return find("status", status).list();
    }

    /**
     * Finds overdue fines (due date has passed).
     * 
     * @return list of overdue fines
     */
    public List<Fine> findOverdueFines() {
        return find("dueDate < ?1 AND status = ?2", LocalDate.now(), Fine.FineStatus.PENDING).list();
    }

    /**
     * Finds fines issued between two dates.
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of fines issued in the date range
     */
    public List<Fine> findByIssueDateBetween(LocalDate startDate, LocalDate endDate) {
        return find("issueDate >= ?1 AND issueDate <= ?2", startDate, endDate).list();
    }

    /**
     * Calculates total outstanding fine amount for a member.
     * 
     * @param member the member
     * @return total outstanding amount
     */
    public Double getTotalOutstandingAmount(Member member) {
        return find("member = ?1 AND status = ?2", member, Fine.FineStatus.PENDING)
                .project(Double.class)
                .select("sum(amount)")
                .singleResult();
    }

    /**
     * Finds fines by reason.
     * 
     * @param reason the fine reason
     * @return list of fines with the given reason
     */
    public List<Fine> findByReason(Fine.FineReason reason) {
        return find("reason", reason).list();
    }
}