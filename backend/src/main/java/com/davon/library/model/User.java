package com.davon.library.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "userType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Member.class, name = "member"),
        @JsonSubTypes.Type(value = Librarian.class, name = "librarian"),
        @JsonSubTypes.Type(value = Admin.class, name = "admin")
})
public abstract class User extends BaseEntity {
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean active;
    private UserStatus status;
    private LocalDate lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public boolean isAdmin() {
        return this instanceof Admin;
    }
}