package com.davon.library.service;

import com.davon.library.model.*;

import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Service for managing users (Member, Librarian, Admin).
 */
public class UserService {
    private final Set<User> users = new HashSet<>();
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    @Data
    @AllArgsConstructor
    public static class UserProfile {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
    }
}