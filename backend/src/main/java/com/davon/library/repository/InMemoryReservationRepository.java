package com.davon.library.repository;

import com.davon.library.model.*;
import com.davon.library.repository.ReservationRepository;
import java.util.*;
import java.time.LocalDate;

public class InMemoryReservationRepository implements ReservationRepository {
    private final Map<Long, Reservation> reservations = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(nextId++);
        }
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public boolean existsByMemberAndBook(Member member, Book book) {
        return reservations.values().stream()
                .anyMatch(r -> r.getMember().equals(member) &&
                        r.getBook().equals(book) &&
                        r.getStatus() == Reservation.ReservationStatus.PENDING);
    }

    @Override
    public Reservation findOldestPendingReservation(Book book) {
        return reservations.values().stream()
                .filter(r -> r.getBook().equals(book) &&
                        r.getStatus() == Reservation.ReservationStatus.PENDING)
                .min(Comparator.comparing(Reservation::getReservationDate))
                .orElse(null);
    }
}
