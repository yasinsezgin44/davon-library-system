package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Reservation;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.MemberRepository;
import com.davon.library.repository.ReservationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReservationService {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    BookRepository bookRepository;

    @Transactional
    public Reservation createReservation(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus("PENDING");
        reservationRepository.persist(reservation);
        return reservation;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation != null) {
            reservation.setStatus("CANCELLED");
            reservationRepository.persist(reservation);
        }
    }

    public List<Reservation> getReservationsByMember(Long memberId) {
        return reservationRepository.list("member.id", memberId);
    }
}
