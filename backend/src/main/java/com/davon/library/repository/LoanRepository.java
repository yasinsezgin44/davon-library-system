package com.davon.library.repository;

import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Loan entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based LoanDAO implementation.
 */
@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {

    /**
     * Finds loans by member.
     * 
     * @param member the member
     * @return list of loans for the member
     */
    public List<Loan> findByMember(Member member) {
        return find("member", member).list();
    }

    /**
     * Finds active loans by member.
     * 
     * @param member the member
     * @return list of active loans for the member
     */
    public List<Loan> findActiveLoansByMember(Member member) {
        return find("member = ?1 AND status = ?2", member, Loan.LoanStatus.ACTIVE).list();
    }

    /**
     * Finds loans by book copy.
     * 
     * @param bookCopy the book copy
     * @return list of loans for the book copy
     */
    public List<Loan> findByBookCopy(BookCopy bookCopy) {
        return find("bookCopy", bookCopy).list();
    }

    /**
     * Finds loans by status.
     * 
     * @param status the loan status
     * @return list of loans with the given status
     */
    public List<Loan> findByStatus(Loan.LoanStatus status) {
        return find("status", status).list();
    }

    /**
     * Finds overdue loans.
     * 
     * @return list of overdue loans
     */
    public List<Loan> findOverdueLoans() {
        return find("dueDate < ?1 AND (status = ?2 OR status = ?3)",
                LocalDate.now(), Loan.LoanStatus.ACTIVE, Loan.LoanStatus.OVERDUE).list();
    }

    /**
     * Finds loans due today.
     * 
     * @return list of loans due today
     */
    public List<Loan> findLoansDueToday() {
        return find("dueDate = ?1 AND status = ?2", LocalDate.now(), Loan.LoanStatus.ACTIVE).list();
    }

    /**
     * Finds loans due in a specific number of days.
     * 
     * @param days number of days from today
     * @return list of loans due in the specified days
     */
    public List<Loan> findLoansDueInDays(int days) {
        LocalDate dueDate = LocalDate.now().plusDays(days);
        return find("dueDate = ?1 AND status = ?2", dueDate, Loan.LoanStatus.ACTIVE).list();
    }

    /**
     * Finds loans checked out between two dates.
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of loans checked out in the date range
     */
    public List<Loan> findLoansCheckedOutBetween(LocalDate startDate, LocalDate endDate) {
        return find("checkoutDate >= ?1 AND checkoutDate <= ?2", startDate, endDate).list();
    }

    /**
     * Finds loans returned between two dates.
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of loans returned in the date range
     */
    public List<Loan> findLoansReturnedBetween(LocalDate startDate, LocalDate endDate) {
        return find("returnDate >= ?1 AND returnDate <= ?2 AND status = ?3",
                startDate, endDate, Loan.LoanStatus.RETURNED).list();
    }

    /**
     * Counts active loans for a member.
     * 
     * @param member the member
     * @return count of active loans
     */
    public long countActiveLoansByMember(Member member) {
        return find("member = ?1 AND status = ?2", member, Loan.LoanStatus.ACTIVE).count();
    }

    /**
     * Finds the current active loan for a book copy.
     * 
     * @param bookCopy the book copy
     * @return the active loan if found, empty otherwise
     */
    public Optional<Loan> findActiveByBookCopy(BookCopy bookCopy) {
        return find("bookCopy = ?1 AND status = ?2", bookCopy, Loan.LoanStatus.ACTIVE).firstResultOptional();
    }
}