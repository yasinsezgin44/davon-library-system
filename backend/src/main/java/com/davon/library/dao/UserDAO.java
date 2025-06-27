package com.davon.library.dao;

import com.davon.library.model.User;
import com.davon.library.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 * Extends BaseDAO with user-specific query methods.
 */
public interface UserDAO extends BaseDAO<User, Long> {

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users by their role.
     * 
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Finds active users.
     * 
     * @return a list of active users
     */
    List<User> findActiveUsers();

    /**
     * Finds inactive users.
     * 
     * @return a list of inactive users
     */
    List<User> findInactiveUsers();

    /**
     * Searches users by multiple criteria (username, full name, email).
     * 
     * @param searchTerm the search term to look for
     * @return a list of users matching the search criteria
     */
    List<User> searchUsers(String searchTerm);

    /**
     * Finds users by status.
     * 
     * @param status the status to search for
     * @return a list of users with the specified status
     */
    List<User> findByStatus(String status);

    /**
     * Checks if a username is already taken.
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email is already registered.
     * 
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds users created between two dates.
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of users created in the specified date range
     */
    List<User> findByCreatedDateBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Counts users by role.
     * 
     * @param role the role to count
     * @return the number of users with the specified role
     */
    long countByRole(Role role);

    /**
     * Counts active users.
     * 
     * @return the number of active users
     */
    long countActiveUsers();
}