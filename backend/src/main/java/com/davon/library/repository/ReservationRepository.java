package com.davon.library.service;

import com.davon.library.model.*;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    boolean existsByMemberAndBook(Member member, Book book);

    Reservation findOldestPendingReservation(Book book);
}
