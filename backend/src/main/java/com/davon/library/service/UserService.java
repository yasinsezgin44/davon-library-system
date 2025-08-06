package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        log.debug("Creating user: {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        log.debug("Updating user: {}", userId);
        User existingUser = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setActive(updatedUser.getActive());

        return existingUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Deleting user: {}", userId);
        boolean deleted = userRepository.deleteById(userId);
        if (!deleted) {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.listAll();
    }

    public Optional<User> getUserById(Long userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findByIdOptional(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String searchTerm) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm);
    }
}
