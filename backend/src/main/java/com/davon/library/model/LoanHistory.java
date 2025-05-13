package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a history record of loan actions.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoanHistory extends BaseEntity {
    private Member member;
    private Loan loan;
    private Book book;
    private LocalDate actionDate;
    private String action; // CHECKOUT, RETURN, RENEWAL

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
