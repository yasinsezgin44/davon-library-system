package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ReservationRepository implements PanacheRepository<Reservation> {

    public List<Reservation> findByMember(Member member) {
        return list("member", member);
    }

    public List<Reservation> findPendingReservationsByBook(Book book) {
        return list("book = ?1 and status = ?2", book, ReservationStatus.PENDING);
    }
}
