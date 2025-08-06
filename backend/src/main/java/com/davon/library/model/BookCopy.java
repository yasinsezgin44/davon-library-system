package com.davon.library.model;

import com.davon.library.model.enums.CopyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_copies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(length = 50)
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private CopyStatus status = CopyStatus.AVAILABLE;

    @Column(length = 100)
    private String location;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
