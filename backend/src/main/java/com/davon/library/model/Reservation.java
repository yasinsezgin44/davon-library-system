package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

/**
 * Represents a reservation of a book by a member.
 */
@Entity
@Table(name = "reservations")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "member", "book" })
@ToString(callSuper = true, exclude = { "member", "book" })
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "priority_number")
    private int priorityNumber; // Bug: This will be calculated incorrectly

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void fulfill() {
        this.status = ReservationStatus.COMPLETED;
    }

    public enum ReservationStatus {
        PENDING,
        READY_FOR_PICKUP,
        COMPLETED,
        CANCELLED
    }
}