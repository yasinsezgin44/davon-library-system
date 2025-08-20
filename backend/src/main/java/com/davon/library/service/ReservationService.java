package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.MemberRepository;
import com.davon.library.repository.ReservationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    BookRepository bookRepository;

    @Transactional
    public Reservation createReservation(Long memberId, Long bookId) {
        log.info("Creating reservation for member {} and book {}", memberId, bookId);

        Member member = memberRepository.findByIdOptional(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        // assign queue position: 1 + number of existing pending reservations for this book
        int queueSize = reservationRepository.findPendingReservationsByBook(book).size();
        reservation.setPriorityNumber(queueSize + 1);
        reservationRepository.persist(reservation);

        log.info("Reservation created successfully with ID {}", reservation.getId());
        return reservation;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        log.info("Cancelling reservation {}", reservationId);
        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    public List<Reservation> getReservationsByMember(Long memberId) {
        Member member = memberRepository.findByIdOptional(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        return reservationRepository.findByMember(member);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.listAll();
    }

    @Transactional
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        reservation.setStatus(status);
    }
}
