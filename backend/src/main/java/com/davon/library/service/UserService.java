package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.elytron.security.common.BcryptUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.davon.library.model.Role;
import com.davon.library.repository.RoleRepository;

@ApplicationScoped
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Transactional
    public User createUser(User user, String password, Set<Long> roleIds) {
        log.debug("Creating user: {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPasswordHash(BcryptUtil.bcryptHash(password));
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                    .map(roleId -> roleRepository.findByIdOptional(roleId)
                            .orElseThrow(() -> new NotFoundException("Role not found with ID: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        log.debug("Updating user: {}", userId);
        User existingUser = findById(userId);

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setActive(updatedUser.getActive());

        return existingUser;
    }

    @Transactional
    public boolean deactivateUser(Long userId) {
        log.debug("Deactivating user: {}", userId);
        User user = findById(userId);
        user.setActive(false);
        return true;
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

    public User findById(Long userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    @Transactional
    public User updateUserByUsername(String username, User updatedUser) {
        log.debug("Updating user: {}", username);
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

        return existingUser;
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

    public long countUsers() {
        return userRepository.count();
    }
}
