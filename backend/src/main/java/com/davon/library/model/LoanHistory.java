package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a history record of loan actions.
 */
@Entity
@Table(name = "loan_history")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "member", "loan", "book" })
@ToString(callSuper = true, exclude = { "member", "loan", "book" })
public class LoanHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "action_date", nullable = false)
    private LocalDate actionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private LoanAction action;

    public List<Loan> getActiveLoans() {
        // This would be implemented in LoanHistoryRepository
        return null;
    }

    public List<Loan> getPastLoans() {
        // This would be implemented in LoanHistoryRepository
        return null;
    }

    public float getAverageReturnTime() {
        // Implementation would calculate average days to return
        return 0f;
    }
}
