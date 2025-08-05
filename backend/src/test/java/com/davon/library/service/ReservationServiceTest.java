package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService reservationService;
    private Member member1;
    private Member member2;
    private Member member3;
    private Book book;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService();

        // Create test objects
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        member1 = Member.builder()
                .id(1L)
                .fullName("Member One")
                .active(true)
                .build();

        member2 = Member.builder()
                .id(2L)
                .fullName("Member Two")
                .active(true)
                .build();

        member3 = Member.builder()
                .id(3L)
                .fullName("Member Three")
                .active(true)
                .build();
    }

    @Test
    void testReservationCreation() {
        Reservation reservation = reservationService.createReservation(member1, book);

        assertNotNull(reservation);
        assertEquals(member1, reservation.getMember());
        assertEquals(book, reservation.getBook());
        assertEquals(Reservation.ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(1, reservation.getPriorityNumber());
    }

    @Test
    void testReservationPriority() {
        Reservation reservation1 = reservationService.createReservation(member1, book);
        Reservation reservation2 = reservationService.createReservation(member2, book);
        Reservation reservation3 = reservationService.createReservation(member3, book);

        List<Reservation> reservations = reservationService.getReservationsForBook(book);

        assertEquals(1, reservation1.getPriorityNumber(), "First reservation should have priority 1");
        assertEquals(2, reservation2.getPriorityNumber(), "Second reservation should have priority 2");
        assertEquals(3, reservation3.getPriorityNumber(), "Third reservation should have priority 3");
    }

    @Test
    void testReservationCancellation() {
        Reservation reservation = reservationService.createReservation(member1, book);
        reservationService.cancelReservation(reservation);

        assertEquals(Reservation.ReservationStatus.CANCELLED, reservation.getStatus());
        assertTrue(reservationService.getReservationsForBook(book).isEmpty());
    }

    @Test
    void testReservationCompletion() {
        Reservation reservation = reservationService.createReservation(member1, book);

        reservationService.markReservationReadyForPickup(reservation);
        assertEquals(Reservation.ReservationStatus.READY_FOR_PICKUP, reservation.getStatus());

        reservationService.completeReservation(reservation);
        assertEquals(Reservation.ReservationStatus.COMPLETED, reservation.getStatus());
        assertTrue(reservationService.getReservationsForBook(book).isEmpty());
    }
}