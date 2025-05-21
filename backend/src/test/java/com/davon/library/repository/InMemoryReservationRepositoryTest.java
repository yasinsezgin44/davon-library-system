package com.davon.library.repository;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryReservationRepositoryTest {
    private InMemoryReservationRepository repository;
    private Member member;
    private Book book;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReservationRepository();
        member = new Member();
        member.setId(1L);
        book = new Book();
        book.setId(1L);
        reservation = new Reservation();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now().minusDays(2));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
    }

    @Test
    void testSaveAndFindById() {
        Reservation saved = repository.save(reservation);
        assertNotNull(saved.getId());
        Optional<Reservation> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved, found.get());
    }

    @Test
    void testExistsByMemberAndBook() {
        repository.save(reservation);
        boolean exists = repository.existsByMemberAndBook(member, book);
        assertTrue(exists);
    }

    @Test
    void testExistsByMemberAndBook_NotPending() {
        reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
        repository.save(reservation);
        boolean exists = repository.existsByMemberAndBook(member, book);
        assertFalse(exists);
    }

    @Test
    void testFindOldestPendingReservation() {
        Reservation r1 = new Reservation();
        r1.setMember(member);
        r1.setBook(book);
        r1.setReservationDate(LocalDate.now().minusDays(5));
        r1.setStatus(Reservation.ReservationStatus.PENDING);
        repository.save(r1);
        repository.save(reservation);
        Reservation found = repository.findOldestPendingReservation(book);
        assertNotNull(found);
        assertEquals(r1, found);
    }

    @Test
    void testFindOldestPendingReservation_None() {
        Reservation found = repository.findOldestPendingReservation(book);
        assertNull(found);
    }
}