package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a librarian who manages the library system.
 */
@Entity
@Table(name = "librarians")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Librarian extends User {
    @Column(name = "employment_date")
    private LocalDate employmentDate;
    @Column(name = "employee_id")
    private String employeeId;
}