package com.davon.library.service;

import com.davon.library.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing users (Member, Librarian, Admin).
 */
public class UserService {
    private final Set<User> users = new HashSet<>();

    public User createUser(User user) {
        users.add(user);
        return user;
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

    public boolean assignRole(Long userId, String role) {
        Optional<User> userOpt = users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst();
        if (userOpt.isPresent() && userOpt.get() instanceof Admin admin) {
            admin.getPermissions().add(role);
            return true;
        }
        return false;
    }

    public Set<User> getUsers() {
        return users;
    }
}