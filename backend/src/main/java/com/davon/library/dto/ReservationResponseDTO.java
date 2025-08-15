package com.davon.library.dto;

import com.davon.library.model.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponseDTO(
        Long id,
        MemberResponseDTO member,
        BookResponseDTO book,
        LocalDateTime reservationTime,
        ReservationStatus status,
        Integer priorityNumber
) {}
