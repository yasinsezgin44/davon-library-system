package com.davon.library.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

/**
 * Represents a librarian who manages the library system.
 */
@Entity
@DiscriminatorValue("librarian")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Librarian extends User {
    private LocalDate employmentDate;
    private String employeeId;
}