package com.davon.library.service;

import com.davon.library.repository.UserRepository;
import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Inject
    UserRepository userRepository;

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

    public User authenticateUser(String username, String passwordHash) {
        if (username == null || passwordHash == null) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        if (user.getPasswordHash().equals(passwordHash) && user.getActive()) {
            return user;
        }

        return null;
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.listAll();
            logger.info("Retrieved " + users.size() + " users from database");
            return users;
        } catch (Exception e) {
            logger.severe("Error retrieving all users: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id);
    }

    private void validateUserForCreation(User user) throws UserServiceException {
        if (user == null) {
            throw new UserServiceException("User cannot be null");
        }

        if (user.getId() != null) {
            throw new UserServiceException("User ID should be null for new users");
        }

        validateUserData(user);
    }

    private void validateUserForUpdate(User user) throws UserServiceException {
        if (user == null) {
            throw new UserServiceException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new UserServiceException("User ID cannot be null for updates");
        }

        validateUserData(user);
    }

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

    public static class UserServiceException extends Exception {
        public UserServiceException(String message) {
            super(message);
        }

        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
