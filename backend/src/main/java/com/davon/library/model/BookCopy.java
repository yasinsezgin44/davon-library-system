package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;

/**
 * Represents a physical copy of a book in the library system.
 */
@Entity
@Table(name = "book_copies")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "book" })
@ToString(callSuper = true, exclude = { "book" })
public class BookCopy extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonBackReference
    private Book book;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "condition")
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CopyStatus status;

    @Column(name = "location")
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