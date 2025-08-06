package com.davon.library.service;

import com.davon.library.model.Loan;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.repository.ReservationRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@QuarkusTest
class NotificationSchedulerTest {

    @Inject
    NotificationScheduler notificationScheduler;

    @InjectMock
    LoanService loanService;

    @InjectMock
    NotificationService notificationService;

    @InjectMock
    ReservationRepository reservationRepository;

    @Test
    void testSendOverdueLoanNotices() {
        when(loanService.getOverdueLoans()).thenReturn(List.of(new Loan(), new Loan()));
        notificationScheduler.sendOverdueLoanNotices();
        Mockito.verify(notificationService, times(2)).sendOverdueNotice(any(Loan.class));
    }

    @Test
    void testSendReservationAvailableNotices() {
        when(reservationRepository.list("status", ReservationStatus.READY_FOR_PICKUP))
                .thenReturn(List.of(new Reservation()));
        notificationScheduler.sendReservationAvailableNotices();
        Mockito.verify(notificationService, times(1)).sendReservationAvailableNotification(any(Reservation.class));
    }
}
