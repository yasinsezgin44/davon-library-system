package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ReservationService {
    // Bug: Using a simple Map instead of a proper database, causing concurrency
    // issues
    private final Map<Long, List<Reservation>> bookReservations = new HashMap<>();
    private long nextReservationId = 1;

    public Reservation createReservation(Member member, Book book) {
        if (!isBookAvailableForReservation(book)) {
            throw new IllegalStateException("Book is not available for reservation");
        }

        List<Reservation> reservations = bookReservations.computeIfAbsent(book.getId(), k -> new ArrayList<>());

        int priority = calculatePriority(reservations, LocalDateTime.now());

        Reservation reservation = Reservation.builder()
                .id(nextReservationId++)
                .member(member)
                .book(book)
                .reservationTime(LocalDateTime.now())
                .status(Reservation.ReservationStatus.PENDING)
                .priorityNumber(priority)
                .build();

        reservations.add(reservation);
        return reservation;
    }

    private boolean isBookAvailableForReservation(Book book) {
        // Check if book exists and is not available for immediate checkout
        return book != null && book.getId() != null;
    }

    private int calculatePriority(List<Reservation> existingReservations, LocalDateTime reservationTime) {
        if (existingReservations.isEmpty()) {
            return 1;
        }

        LocalDateTime baseHour = reservationTime.withHour(0).withMinute(0).withSecond(0);
        int samePriorityCount = 0;

        for (Reservation existing : existingReservations) {
            if (existing.getReservationTime().withHour(0).withMinute(0).withSecond(0).equals(baseHour)) {
                samePriorityCount++;
            }
        }

        return existingReservations.size() + samePriorityCount + 1;
    }

    public List<Reservation> getReservationsForBook(Book book) {
        return bookReservations.getOrDefault(book.getId(), new ArrayList<>());
    }

    public void cancelReservation(Reservation reservation) {
        List<Reservation> reservations = bookReservations.get(reservation.getBook().getId());
        if (reservations != null) {
            reservations.remove(reservation);
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        }
    }

    public void markReservationReadyForPickup(Reservation reservation) {
        reservation.setStatus(Reservation.ReservationStatus.READY_FOR_PICKUP);
    }

    public void completeReservation(Reservation reservation) {
        List<Reservation> reservations = bookReservations.get(reservation.getBook().getId());
        if (reservations != null) {
            reservations.remove(reservation);
            reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        }
    }
}