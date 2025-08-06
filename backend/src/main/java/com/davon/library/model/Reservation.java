package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    @Column(length = 20)
    private String status;

    @Column(name = "priority_number")
    private Integer priorityNumber;

    @PrePersist
    protected void onCreate() {
        reservationTime = LocalDateTime.now();
    }
}
