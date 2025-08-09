package com.davon.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationSummary {
    private Long id;
    private String bookTitle;
    private String userName;
    private LocalDateTime reservationDate;
    private String status;
}
