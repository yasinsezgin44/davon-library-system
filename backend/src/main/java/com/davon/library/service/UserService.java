package com.davon.library.service;

import com.davon.library.dao.UserDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.*;
import com.davon.library.event.UserStatusListener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Service for managing users (Member, Librarian, Admin).
 * This service follows SOLID principles by depending on abstractions (UserDAO)
 * and focusing only on business logic, not data access.
 */
@ApplicationScoped
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Inject
    private UserDAO userDAO;

    private final List<UserStatusListener> statusListeners = new ArrayList<>();

    /**
     * Creates a new user in the system.
     * 
     * @param user the user to create
     * @return the created user with assigned ID
     * @throws UserServiceException if the user creation fails
     */
    public User createUser(User user) throws UserServiceException {
        try {
            validateUserForCreation(user);
            return userDAO.save(user);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to create user", e);
            throw new UserServiceException("Failed to create user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user in the system.
     * 
     * @param userId      the ID of the user to update
     * @param updatedUser the updated user data
     * @return the updated user
     * @throws UserServiceException if the user update fails
     */
    public User updateUser(Long userId, User updatedUser) throws UserServiceException {
        try {
            if (userId == null) {
                throw new UserServiceException("User ID cannot be null");
            }

            if (!userDAO.existsById(userId)) {
                throw new UserServiceException("User not found with ID: " + userId);
            }

            updatedUser.setId(userId);
            validateUserForUpdate(updatedUser);
            return userDAO.update(updatedUser);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to update user", e);
            throw new UserServiceException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a user account.
     * 
     * @param userId the ID of the user to deactivate
     * @return true if the user was successfully deactivated, false otherwise
     * @throws UserServiceException if the operation fails
     */
    public boolean deactivateUser(Long userId) throws UserServiceException {
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            user.setActive(false);
            userDAO.update(user);
            return true;
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to deactivate user", e);
            throw new UserServiceException("Failed to deactivate user: " + e.getMessage(), e);
        }
    }

    /**
     * Activates a user account.
     * 
     * @param userId the ID of the user to activate
     * @return true if the user was successfully activated, false otherwise
     * @throws UserServiceException if the operation fails
     */
    public boolean activateUser(Long userId) throws UserServiceException {
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            user.setActive(true);
            userDAO.update(user);
            return true;
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to activate user", e);
            throw new UserServiceException("Failed to activate user: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for users using various criteria.
     * 
     * @param query the search query
     * @return a list of users matching the search criteria
     */
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userDAO.searchUsers(query.trim());
    }

    /**
     * Authenticates a user with username and password.
     * 
     * @param username     the username
     * @param passwordHash the hashed password
     * @return the authenticated user if successful, null otherwise
     */
    public User authenticateUser(String username, String passwordHash) {
        if (username == null || passwordHash == null) {
            return null;
        }

        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        if (user.getPasswordHash().equals(passwordHash) && user.isActive()) {
            return user;
        }

        return null;
    }

    /**
     * Assigns a role to a user.
     * 
     * @param user the user to assign the role to
     * @param role the role to assign
     * @return the updated user
     * @throws UserServiceException if the operation fails
     */
    public User assignRole(User user, Role role) throws UserServiceException {
        try {
            if (user == null || role == null) {
                throw new UserServiceException("User and role cannot be null");
            }

            user.setRole(role);
            return userDAO.update(user);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Failed to assign role", e);
            throw new UserServiceException("Failed to assign role: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all users in the system.
     * 
     * @return a list of all users
     */
    public List<User> getUsers() {
        return userDAO.findAll();
    }

    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return the user if found, null otherwise
     */
    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByEmail(email.trim()).orElse(null);
    }

    /**
     * Finds a user by ID.
     * 
     * @param id the user ID
     * @return the user if found, null otherwise
     */
    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return userDAO.findById(id).orElse(null);
    }

    public boolean updateProfile(Long userId, UserProfile profile) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }

        user.setFullName(profile.getFullName());
        user.setEmail(profile.getEmail());
        user.setPhoneNumber(profile.getPhoneNumber());

        if (user instanceof Member) {
            Member member = (Member) user;
            member.setAddress(profile.getAddress());
        }

        return true;
    }

    public void addStatusListener(UserStatusListener listener) {
        statusListeners.add(listener);
    }

    public void removeStatusListener(UserStatusListener listener) {
        statusListeners.remove(listener);
    }

    public boolean updateUserStatus(Long userId, String newStatus) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }

        String oldStatus = user.getStatus();
        user.setStatus(newStatus);

        // Notify listeners
        notifyStatusChange(user, oldStatus, newStatus);
        return true;
    }

    private void notifyStatusChange(User user, String oldStatus, String newStatus) {
        for (UserStatusListener listener : statusListeners) {
            listener.onUserStatusChange(user, oldStatus, newStatus);
        }
    }

    @Data
    @AllArgsConstructor
    public static class UserProfile {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
    }
}