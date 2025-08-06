package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ReservationServiceTest {

    @Inject
    ReservationService reservationService;

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    LoanRepository loanRepository;

    @Inject
    FineRepository fineRepository;

    private User user;
    private Member member;
    private Book book;

    @BeforeEach
    @Transactional
    void setUp() {
        fineRepository.deleteAll();
        loanRepository.deleteAll();
        reservationRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("password");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        userRepository.persist(user);

        member = new Member();
        member.setUser(user);
        member.setFineBalance(BigDecimal.ZERO);
        memberRepository.persist(member);

        book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        bookRepository.persist(book);
    }

    @Test
    @Transactional
    void testCreateReservation() {
        Reservation reservation = reservationService.createReservation(member.getId(), book.getId());
        assertNotNull(reservation.getId());
        assertEquals("PENDING", reservation.getStatus());
    }

    @Test
    @Transactional
    void testCancelReservation() {
        Reservation reservation = reservationService.createReservation(member.getId(), book.getId());
        reservationService.cancelReservation(reservation.getId());
        assertEquals("CANCELLED", reservationRepository.findById(reservation.getId()).getStatus());
    }
}
