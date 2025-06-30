package com.davon.library.dao;

import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Fine entities.
 * Follows the DAO pattern for data layer abstraction.
 */
public interface FineDAO extends BaseDAO<Fine, Long> {

    /**
     * Find all fines for a specific member.
     * 
     * @param member the member
     * @return list of fines for the member
     */
    List<Fine> findByMember(Member member);

    /**
     * Find unpaid fines for a specific member.
     * 
     * @param member the member
     * @return list of unpaid fines for the member
     */
    List<Fine> findUnpaidFinesByMember(Member member);

    /**
     * Find fines by status.
     * 
     * @param status the fine status
     * @return list of fines with the specified status
     */
    List<Fine> findByStatus(Fine.FineStatus status);

    /**
     * Find fines by reason.
     * 
     * @param reason the fine reason
     * @return list of fines with the specified reason
     */
    List<Fine> findByReason(Fine.FineReason reason);

    /**
     * Find overdue fines.
     * 
     * @param currentDate the current date
     * @return list of overdue fines
     */
    List<Fine> findOverdueFines(LocalDate currentDate);

    /**
     * Calculate total unpaid amount for a member.
     * 
     * @param member the member
     * @return total unpaid fine amount
     */
    double getTotalUnpaidAmount(Member member);
}