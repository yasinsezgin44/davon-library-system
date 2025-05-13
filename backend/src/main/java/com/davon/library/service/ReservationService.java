package com.davon.library.service;

import com.davon.library.model.*;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    public ReservationService(ReservationRepository reservationRepository,
            NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    public Reservation placeReservation(Member member, Book book) {
        // Check if book is already available
        if (book.isAvailable()) {
            return null; // No need to reserve if available
        }

        // Check if member already has a reservation for this book
        boolean alreadyReserved = reservationRepository.existsByMemberAndBook(member, book);
        if (alreadyReserved) {
            return null;
        }

        Reservation reservation = Reservation.builder()
                .member(member)
                .book(book)
                .reservationDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(7))
                .status(Reservation.ReservationStatus.PENDING)
                .build();

        if (member.getReservations() != null) {
            member.getReservations().add(reservation);
        }

        return reservationRepository.save(reservation);
    }

    public boolean cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElse(null);

        if (reservation == null) {
            return false;
        }

        reservation.cancel();
        reservationRepository.save(reservation);
        return true;
    }

    public void processAvailableReservations(Book book) {
        // Find the oldest pending reservation for this book
        Reservation oldestReservation = reservationRepository.findOldestPendingReservation(book);

        if (oldestReservation != null) {
            oldestReservation.setStatus(Reservation.ReservationStatus.FULFILLED);
            reservationRepository.save(oldestReservation);

            // Notify member
            notificationService.sendReservationNotification(oldestReservation);
        }
    }
}
