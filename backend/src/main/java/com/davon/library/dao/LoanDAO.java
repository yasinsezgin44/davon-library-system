package com.davon.library.dao;

import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Loan entities.
 * Follows the DAO pattern for data layer abstraction.
 */
public interface LoanDAO extends BaseDAO<Loan, Long> {

    /**
     * Find all loans for a specific member.
     * 
     * @param member the member
     * @return list of loans for the member
     */
    List<Loan> findByMember(Member member);

    /**
     * Find all active loans for a specific member.
     * 
     * @param member the member
     * @return list of active loans for the member
     */
    List<Loan> findActiveLoansByMember(Member member);

    /**
     * Find loan by book copy and member.
     * 
     * @param bookCopy the book copy
     * @param member   the member
     * @return optional loan
     */
    Optional<Loan> findByBookCopyAndMember(BookCopy bookCopy, Member member);

    /**
     * Find active loan by book copy.
     * 
     * @param bookCopy the book copy
     * @return optional active loan
     */
    Optional<Loan> findActiveByBookCopy(BookCopy bookCopy);

    /**
     * Find all overdue loans.
     * 
     * @param currentDate the current date
     * @return list of overdue loans
     */
    List<Loan> findOverdueLoans(LocalDate currentDate);

    /**
     * Find all loans due on a specific date.
     * 
     * @param dueDate the due date
     * @return list of loans due on the date
     */
    List<Loan> findLoansDueOn(LocalDate dueDate);

    /**
     * Find loans by status.
     * 
     * @param status the loan status
     * @return list of loans with the specified status
     */
    List<Loan> findByStatus(Loan.LoanStatus status);

    /**
     * Count active loans for a member.
     * 
     * @param member the member
     * @return number of active loans
     */
    long countActiveLoansByMember(Member member);
}