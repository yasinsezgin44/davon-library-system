package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for all users in the library system.
 */
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
    private String status;
    private LocalDate lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public boolean isAdmin() {
        return this instanceof Admin;
    }
}