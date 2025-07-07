package com.davon.library.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.Set;

/**
 * Represents an admin user in the library system.
 */
@Entity
@DiscriminatorValue("admin")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    private int adminLevel;
    private String department;

    // Note: Storing a Set<String> directly is not ideal for relational databases.
    // This will likely be stored as a serialized blob. For a better approach,
    // consider using a separate @ElementCollection or a @OneToMany relationship.
    private Set<String> permissions;

    private LocalDate lastActivity;

    public void manageUsers() {
    }

    public Object viewReports() {
        return null;
    }

    public boolean assignRoles(String userId, String role) {
        return false;
    }

    public Object generateSystemReport(String type) {
        return null;
    }

    public boolean manageSettings(Object settings) {
        return false;
    }
}