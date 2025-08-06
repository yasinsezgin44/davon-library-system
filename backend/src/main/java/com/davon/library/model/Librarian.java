package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "librarians")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Librarian {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "employment_date")
    private LocalDate employmentDate;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;
}
