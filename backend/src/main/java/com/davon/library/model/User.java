package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all users in the library system.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean active;
}