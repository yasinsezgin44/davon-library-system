package com.davon.library.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.Set;

/**
 * Represents an admin user in the library system.
 */
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    @Column(name = "admin_level")
    private int adminLevel;

    private String department;

    // Store permissions as text - better approach would be separate table
    @Column(columnDefinition = "TEXT")
    private String permissions; // Changed from Set<String> to String for database compatibility

    @Column(name = "last_activity")
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