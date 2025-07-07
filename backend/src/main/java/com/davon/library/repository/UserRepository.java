package com.davon.library.repository;

import com.davon.library.model.User;
import com.davon.library.model.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based UserDAO implementation.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return the user if found, empty otherwise
     */
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    /**
     * Finds a user by email.
     * 
     * @param email the email to search for
     * @return the user if found, empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Finds users by status.
     * 
     * @param status the user status
     * @return list of users with the given status
     */
    public List<User> findByStatus(UserStatus status) {
        return find("status", status).list();
    }

    /**
     * Finds all active users.
     * 
     * @return list of active users
     */
    public List<User> findActiveUsers() {
        return find("active", true).list();
    }

    /**
     * Finds users by full name containing the given string (case-insensitive).
     * 
     * @param name the name substring to search for
     * @return list of users matching the name
     */
    public List<User> findByFullNameContaining(String name) {
        return find("LOWER(fullName) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    /**
     * Checks if a user exists by username.
     * 
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return find("username", username).count() > 0;
    }

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }

    /**
     * Searches users by username, email, or full name.
     * 
     * @param searchTerm the search term
     * @return list of users matching the search criteria
     */
    public List<User> searchUsers(String searchTerm) {
        String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
        return find("LOWER(username) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(fullName) LIKE ?1",
                lowerSearchTerm).list();
    }
}