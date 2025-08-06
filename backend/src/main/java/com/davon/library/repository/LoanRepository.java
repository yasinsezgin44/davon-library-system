package com.davon.library.repository;

import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {

    public List<Loan> findByMember(Member member) {
        return find("member", member).list();
    }

    public List<Loan> findActiveLoansByMember(Member member) {
        return find("member = ?1 and status = 'ACTIVE'", member).list();
    }

    public long countActiveLoansByMember(Member member) {
        return find("member = ?1 and status = 'ACTIVE'", member).count();
    }

    public List<Loan> findOverdueLoans() {
        return find("dueDate < current_date and status = 'ACTIVE'").list();
    }

    public List<Loan> findByCheckoutDateBetween(LocalDate startDate, LocalDate endDate) {
        return find("checkoutDate >= ?1 and checkoutDate <= ?2", startDate, endDate).list();
    }
}
