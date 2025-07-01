package com.davon.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a reservation of a book by a member.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private Long id;
    private Member member;
    private Book book;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
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