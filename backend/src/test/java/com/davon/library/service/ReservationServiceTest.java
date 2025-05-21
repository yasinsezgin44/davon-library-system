package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService reservationService;
    private TestReservationRepository reservationRepository;
    private TestNotificationService notificationService;
    private Member member;
    private Book availableBook;
    private Book unavailableBook;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        reservationRepository = new TestReservationRepository();
        notificationService = new TestNotificationService();
        reservationService = new ReservationService(reservationRepository, notificationService);

        // Create test member
        member = Member.builder()
                .id(1L)
                .fullName("Test Member")
                .email("member@test.com")
                .active(true)
                .reservations(new HashSet<>())
                .build();

        // Create books with different availability
        availableBook = Book.builder()
                .id(1L)
                .title("Available Book")
                .ISBN("1234567890")
                .build();

        unavailableBook = Book.builder()
                .id(2L)
                .title("Unavailable Book")
                .ISBN("0987654321")
                .build();

        // Mock the isAvailable method behavior
        // We'll override the isAvailable method for test purposes
        availableBook = new TestBook(availableBook, true);
        unavailableBook = new TestBook(unavailableBook, false);

        // Create a test reservation
        testReservation = Reservation.builder()
                .id(1L)
                .member(member)
                .book(unavailableBook)
                .reservationDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(7))
                .status(Reservation.ReservationStatus.PENDING)
                .build();
    }

    @Test
    void testPlaceReservationForAvailableBook() {
        // Trying to reserve an available book should return null
        Reservation reservation = reservationService.placeReservation(member, availableBook);
        assertNull(reservation);

        // Verify no reservation was saved
        assertEquals(0, reservationRepository.getReservations().size());
    }

    @Test
    void testPlaceReservationForUnavailableBook() {
        // Placing a reservation for an unavailable book should succeed
        Reservation reservation = reservationService.placeReservation(member, unavailableBook);

        assertNotNull(reservation);
        assertEquals(member, reservation.getMember());
        assertEquals(unavailableBook, reservation.getBook());
        assertEquals(Reservation.ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(LocalDate.now(), reservation.getReservationDate());
        assertEquals(LocalDate.now().plusDays(7), reservation.getExpirationDate());

        // Verify the reservation was saved
        assertEquals(1, reservationRepository.getReservations().size());

        // Verify it was added to the member's reservations
        assertTrue(member.getReservations().size() > 0);
        // Since reservations might not have IDs set by the repository in this test
        // environment,
        // we'll check for a matching book instead
        boolean hasReservation = false;
        for (Reservation r : member.getReservations()) {
            if (r.getBook() != null && unavailableBook.equals(r.getBook())) {
                hasReservation = true;
                break;
            }
        }
        assertTrue(hasReservation);
    }

    @Test
    void testPlaceReservationForAlreadyReservedBook() {
        // First reservation should succeed
        Reservation firstReservation = reservationService.placeReservation(member, unavailableBook);
        assertNotNull(firstReservation);

        // Second reservation by the same member for the same book should fail
        Reservation secondReservation = reservationService.placeReservation(member, unavailableBook);
        assertNull(secondReservation);

        // Verify only one reservation was saved
        assertEquals(1, reservationRepository.getReservations().size());
    }

    @Test
    void testCancelReservation() {
        // Save test reservation
        reservationRepository.save(testReservation);

        // Cancel the reservation
        boolean result = reservationService.cancelReservation(testReservation.getId());

        // Verify the operation was successful
        assertTrue(result);

        // Verify the reservation status was updated
        assertEquals(Reservation.ReservationStatus.CANCELLED, testReservation.getStatus());
    }

    @Test
    void testCancelNonExistentReservation() {
        // Try to cancel a non-existent reservation
        boolean result = reservationService.cancelReservation(999L);

        // Verify the operation failed
        assertFalse(result);
    }

    @Test
    void testProcessAvailableReservations() {
        // Save test reservation
        reservationRepository.save(testReservation);

        // Process reservations for the book
        reservationService.processAvailableReservations(unavailableBook);

        // Verify the reservation status was updated
        assertEquals(Reservation.ReservationStatus.FULFILLED, testReservation.getStatus());

        // Verify notification was sent
        assertEquals(1, notificationService.getNotificationCount());
        assertEquals(testReservation, notificationService.getLastReservation());
    }

    @Test
    void testProcessAvailableReservationsNoReservations() {
        // Process reservations for a book with no reservations
        reservationService.processAvailableReservations(availableBook);

        // Verify no notifications were sent
        assertEquals(0, notificationService.getNotificationCount());
    }

    // Test implementations

    static class TestReservationRepository implements ReservationRepository {
        private List<Reservation> reservations = new ArrayList<>();

        public List<Reservation> getReservations() {
            return reservations;
        }

        @Override
        public Reservation save(Reservation reservation) {
            reservations.removeIf(r -> r.getId() != null && r.getId().equals(reservation.getId()));
            reservations.add(reservation);
            return reservation;
        }

        @Override
        public Optional<Reservation> findById(Long id) {
            return reservations.stream()
                    .filter(r -> r.getId() != null && r.getId().equals(id))
                    .findFirst();
        }

        @Override
        public boolean existsByMemberAndBook(Member member, Book book) {
            return reservations.stream()
                    .anyMatch(r -> r.getMember().equals(member) &&
                            r.getBook().equals(book) &&
                            r.getStatus() == Reservation.ReservationStatus.PENDING);
        }

        @Override
        public Reservation findOldestPendingReservation(Book book) {
            return reservations.stream()
                    .filter(r -> r.getBook().equals(book) &&
                            r.getStatus() == Reservation.ReservationStatus.PENDING)
                    .sorted((r1, r2) -> r1.getReservationDate().compareTo(r2.getReservationDate()))
                    .findFirst()
                    .orElse(null);
        }
    }

    static class TestNotificationService extends NotificationService {
        private int notificationCount = 0;
        private Reservation lastReservation;

        @Override
        public void sendReservationNotification(Reservation reservation) {
            notificationCount++;
            lastReservation = reservation;
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public Reservation getLastReservation() {
            return lastReservation;
        }
    }

    // Custom Book class that overrides isAvailable method for testing
    static class TestBook extends Book {
        private final boolean available;

        public TestBook(Book book, boolean available) {
            super();
            this.setId(book.getId());
            this.setTitle(book.getTitle());
            this.setISBN(book.getISBN());
            this.setPublicationYear(book.getPublicationYear());
            this.setDescription(book.getDescription());
            this.setCoverImage(book.getCoverImage());
            this.setPages(book.getPages());
            this.setAuthors(book.getAuthors());
            this.setPublisher(book.getPublisher());
            this.setCategory(book.getCategory());
            this.available = available;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}