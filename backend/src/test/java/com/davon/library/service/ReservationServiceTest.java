package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.MemberRepository;
import com.davon.library.repository.ReservationRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class ReservationServiceTest {

    @Inject
    ReservationService reservationService;

    @InjectMock
    ReservationRepository reservationRepository;

    @InjectMock
    MemberRepository memberRepository;

    @InjectMock
    BookRepository bookRepository;

    private Member member;
    private Book book;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);

        book = new Book();
        book.setId(1L);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void testCreateReservation() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(book));

        Reservation createdReservation = reservationService.createReservation(1L, 1L);

        assertNotNull(createdReservation);
        assertEquals(ReservationStatus.PENDING, createdReservation.getStatus());
        Mockito.verify(reservationRepository).persist(any(Reservation.class));
    }

    @Test
    void testCreateReservation_memberNotFound() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> reservationService.createReservation(1L, 1L));
    }

    @Test
    void testCancelReservation() {
        when(reservationRepository.findByIdOptional(1L)).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation(1L);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void testCancelReservation_notFound() {
        when(reservationRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> reservationService.cancelReservation(1L));
    }
}
