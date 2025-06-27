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

    public User updateUser(Long userId, User updatedUser) {
        users.removeIf(u -> Objects.equals(u.getId(), userId));
        users.add(updatedUser);
        return updatedUser;
    }

    public boolean deactivateUser(Long userId) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), userId))
                .peek(u -> u.setActive(false))
                .findFirst()
                .isPresent();
    }

    public boolean activateUser(Long userId) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), userId))
                .peek(u -> u.setActive(true))
                .findFirst()
                .isPresent();
    }

    public List<User> searchUsers(String query) {
        return users.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase())
                        || u.getFullName().toLowerCase().contains(query.toLowerCase())
                        || u.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public User authenticateUser(String username, String passwordHash) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPasswordHash().equals(passwordHash)
                        && u.isActive())
                .findFirst()
                .orElse(null);
    }

    public User assignRole(User user, Role role) {
        // Simply save the user
        return userRepository.save(user);
    }

    public Set<User> getUsers() {
        return users;
    }

    public User findUserByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public User findById(Long id) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .findFirst()
                .orElse(null);
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