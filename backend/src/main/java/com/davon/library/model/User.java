package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

/**
 * Base class for all users in the library system.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "userType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Member.class, name = "member"),
        @JsonSubTypes.Type(value = Librarian.class, name = "librarian"),
        @JsonSubTypes.Type(value = Admin.class, name = "admin")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class User extends BaseEntity {
    private String username;

    @JsonIgnore // Don't serialize password hash for security
    private String passwordHash;

    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDate lastLogin;

    public boolean isAdmin() {
        return this instanceof Admin;
    }
}