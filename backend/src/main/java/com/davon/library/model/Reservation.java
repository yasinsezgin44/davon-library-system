package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a reservation of a book by a member.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = { "member", "book" })
public class Reservation extends BaseEntity {
    private Member member;
    private Book book;
    private LocalDate reservationDate;
    private LocalDate expirationDate;
    private ReservationStatus status;

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void fulfill() {
        this.status = ReservationStatus.FULFILLED;
    }

    public enum ReservationStatus {
        PENDING,
        FULFILLED,
        CANCELLED,
        EXPIRED
    }
}