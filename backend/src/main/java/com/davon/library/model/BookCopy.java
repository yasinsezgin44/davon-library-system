package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a physical copy of a book in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookCopy extends BaseEntity {
    private Book book;
    private LocalDate acquisitionDate;
    private String condition;
    private CopyStatus status;
    private String location;

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    public void checkOut() {
        this.status = CopyStatus.CHECKED_OUT;
    }

    public void checkIn() {
        this.status = CopyStatus.AVAILABLE;
    }

    public boolean updateCondition(String newCondition, String notes) {
        this.condition = newCondition;
        // Consider adding conditionHistory to track changes over time
        return true;
    }

    public enum CopyStatus {
        AVAILABLE,
        CHECKED_OUT,
        IN_REPAIR,
        LOST,
        RESERVED
    }
}