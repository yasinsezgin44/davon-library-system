package com.davon.library.mapper;

import com.davon.library.dto.ReservationRequestDTO;
import com.davon.library.dto.ReservationResponseDTO;
import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Reservation;

public class ReservationMapper {

    public static Reservation toEntity(ReservationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setReservationTime(dto.reservationTime());
        reservation.setStatus(dto.status());
        reservation.setPriorityNumber(dto.priorityNumber());

        if (dto.memberId() != null) {
            Member member = new Member();
            member.setId(dto.memberId());
            reservation.setMember(member);
        }

        if (dto.bookId() != null) {
            Book book = new Book();
            book.setId(dto.bookId());
            reservation.setBook(book);
        }

        return reservation;
    }

    public static ReservationResponseDTO toResponseDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return new ReservationResponseDTO(
                reservation.getId(),
                MemberMapper.toResponseDTO(reservation.getMember()),
                BookMapper.toResponseDTO(reservation.getBook()),
                reservation.getReservationTime(),
                reservation.getStatus(),
                reservation.getPriorityNumber()
        );
    }
}
