package com.davon.library.service;

import com.davon.library.repository.UserRepository;
import com.davon.library.model.*;
import com.davon.library.event.UserStatusListener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Service for managing users (Member, Librarian, Admin).
 * This service follows SOLID principles by depending on abstractions
 * (UserRepository)
 * and focusing only on business logic, not data access.
 */
@ApplicationScoped
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;

    private final List<UserStatusListener> statusListeners = new ArrayList<>();

    /**
     * Constructor-based injection preferred for immutability and unit-testing.
     */
    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user in the system.
     * 
     * @param user the user to create
     * @return the created user with assigned ID
     * @throws UserServiceException if the user creation fails
     */
    @Transactional
    public User createUser(User user) throws UserServiceException {
        try {
            validateUserForCreation(user);
            userRepository.persist(user);
            return user;
        } catch (Exception e) {
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
    @Transactional
    public User updateUser(Long userId, User updatedUser) throws UserServiceException {
        try {
            if (userId == null) {
                throw new UserServiceException("User ID cannot be null");
            }

            User existingUser = userRepository.findById(userId);
            if (existingUser == null) {
                throw new UserServiceException("User not found with ID: " + userId);
            }

            updatedUser.setId(userId);
            validateUserForUpdate(updatedUser);
            return userRepository.getEntityManager().merge(updatedUser);
        } catch (Exception e) {
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
    @Transactional
    public boolean deactivateUser(Long userId) throws UserServiceException {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }

            user.setActive(false);
            userRepository.getEntityManager().merge(user);
            return true;
        } catch (Exception e) {
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
    @Transactional
    public boolean activateUser(Long userId) throws UserServiceException {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }

            user.setActive(true);
            userRepository.getEntityManager().merge(user);
            return true;
        } catch (Exception e) {
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
        logger.info("Searching users with query: '" + query + "'");
        if (query == null || query.trim().isEmpty()) {
            List<User> allUsers = getAllUsers();
            logger.info("Empty query, returning all users: " + allUsers.size());
            return allUsers;
        }
        List<User> results = userRepository.searchUsers(query.trim());
        logger.info("Search results: " + results.size());
        return results;
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

        Optional<User> userOpt = userRepository.findByUsername(username);
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
     * Note: In this implementation, roles are determined by user types (Member,
     * Librarian, Admin)
     * 
     * @param user the user to assign the role to
     * @param role the role to assign
     * @return the updated user
     * @throws UserServiceException if the operation fails
     */
    @Transactional
    public User assignRole(User user, Role role) throws UserServiceException {
        try {
            if (user == null || role == null) {
                throw new UserServiceException("User and role cannot be null");
            }

            // In this simplified implementation, roles are managed through user inheritance
            // (Member, Librarian, Admin classes), not through a separate role field
            logger.info("Role assignment requested for user " + user.getUsername() + " (role: " + role.getName() + ")");
            logger.info("Note: User roles are determined by user type (Member/Librarian/Admin)");

            return userRepository.getEntityManager().merge(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to assign role", e);
            throw new UserServiceException("Failed to assign role: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all users in the system.
     * 
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.getAllUsersWithInheritance();
            logger.info("Retrieved " + users.size() + " users from database");
            return users;
        } catch (Exception e) {
            logger.severe("Error retrieving all users: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
        return userRepository.findByEmail(email.trim()).orElse(null);
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
        return userRepository.findById(id);
    }

    /**
     * Updates a user's profile information.
     * 
     * @param userId  the ID of the user to update
     * @param profile the new profile information
     * @return true if the profile was successfully updated, false otherwise
     * @throws UserServiceException if the operation fails
     */
    @Transactional
    public boolean updateProfile(Long userId, UserProfile profile) throws UserServiceException {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }

            user.setName(profile.getName());
            user.setEmail(profile.getEmail());
            user.setPhoneNumber(profile.getPhoneNumber());

            if (user instanceof Member) {
                Member member = (Member) user;
                member.setAddress(profile.getAddress());
            }

            userRepository.getEntityManager().merge(user);
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user profile", e);
            throw new UserServiceException("Failed to update user profile: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a status change listener.
     * 
     * @param listener the listener to add
     */
    public void addStatusListener(UserStatusListener listener) {
        if (listener != null) {
            statusListeners.add(listener);
        }
    }

    /**
     * Removes a status change listener.
     * 
     * @param listener the listener to remove
     */
    public void removeStatusListener(UserStatusListener listener) {
        statusListeners.remove(listener);
    }

    /**
     * Updates a user's status and notifies listeners.
     * 
     * @param userId    the ID of the user
     * @param newStatus the new status
     * @return true if the status was successfully updated, false otherwise
     * @throws UserServiceException if the operation fails
     */
    @Transactional
    public boolean updateUserStatus(Long userId, String newStatus) throws UserServiceException {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }

            UserStatus oldStatus = user.getStatus();
            user.setStatus(UserStatus.valueOf(newStatus.toUpperCase()));
            userRepository.getEntityManager().merge(user);

            // Notify listeners
            notifyStatusChange(user, oldStatus.name(), newStatus);
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update user status", e);
            throw new UserServiceException("Failed to update user status: " + e.getMessage(), e);
        }
    }

    /**
     * Gets active users.
     * 
     * @return a list of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * Gets users by type (role equivalent).
     * 
     * @param userType the user type to filter by (Member, Librarian, Admin)
     * @return a list of users of the specified type
     */
    public List<User> getUsersByType(String userType) {
        return userRepository.listAll().stream()
                .filter(user -> {
                    switch (userType.toLowerCase()) {
                        case "member":
                            return user instanceof Member;
                        case "librarian":
                            return user instanceof Librarian;
                        case "admin":
                            return user instanceof Admin;
                        default:
                            return false;
                    }
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks if a username is available.
     * 
     * @param username the username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email is available.
     * 
     * @param email the email to check
     * @return true if the email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Validates a user for creation.
     * 
     * @param user the user to validate
     * @throws UserServiceException if validation fails
     */
    private void validateUserForCreation(User user) throws UserServiceException {
        if (user == null) {
            throw new UserServiceException("User cannot be null");
        }

        if (user.getId() != null) {
            throw new UserServiceException("User ID should be null for new users");
        }

        validateUserData(user);
    }

    /**
     * Validates a user for update.
     * 
     * @param user the user to validate
     * @throws UserServiceException if validation fails
     */
    private void validateUserForUpdate(User user) throws UserServiceException {
        if (user == null) {
            throw new UserServiceException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new UserServiceException("User ID cannot be null for updates");
        }

        validateUserData(user);
    }

    /**
     * Validates user data.
     * 
     * @param user the user to validate
     * @throws UserServiceException if validation fails
     */
    private void validateUserData(User user) throws UserServiceException {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new UserServiceException("Username cannot be null or empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new UserServiceException("Email cannot be null or empty");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new UserServiceException("Password cannot be null or empty");
        }
    }

    /**
     * Notifies all listeners about a status change.
     * 
     * @param user      the user whose status changed
     * @param oldStatus the old status
     * @param newStatus the new status
     */
    private void notifyStatusChange(User user, String oldStatus, String newStatus) {
        for (UserStatusListener listener : statusListeners) {
            try {
                listener.onUserStatusChange(user, oldStatus, newStatus);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error notifying status listener", e);
            }
        }
    }

    /**
     * Custom exception for user service operations.
     */
    public static class UserServiceException extends Exception {
        public UserServiceException(String message) {
            super(message);
        }

        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
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