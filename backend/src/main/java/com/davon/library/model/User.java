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
@Inheritance(strategy = InheritanceType.JOINED)
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

    // Audit fields - inherited from BaseEntity, but need column mapping
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this instanceof Admin;
    }
}