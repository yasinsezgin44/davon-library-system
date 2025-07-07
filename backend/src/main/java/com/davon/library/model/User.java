package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for all users in the library system.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "userType", discriminatorType = DiscriminatorType.STRING)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDate lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public boolean isAdmin() {
        return this instanceof Admin;
    }
}