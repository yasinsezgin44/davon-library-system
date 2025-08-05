package com.davon.library.service;

import com.davon.library.model.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for administrative operations.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class AdminService {

    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private SecurityService securityService;

    /**
     * Creates a new user account with the specified role.
     * 
     * @param user     the user to create
     * @param password the user's password
     * @param role     the role to assign
     * @return true if creation was successful
     * @throws AdminServiceException if account creation fails
     */
    public boolean createUserAccount(User user, String password, String role) throws AdminServiceException {
        try {
            user.setPasswordHash(securityService.hashPassword(password));
            User createdUser = userService.createUser(user);

            if (createdUser != null) {
                // Assign role to user
                Role userRole = new Role();
                userRole.setName(role);
                userService.assignRole(createdUser, userRole);
                return true;
            }

            return false;
        } catch (UserService.UserServiceException e) {
            logger.severe("Failed to create user account: " + e.getMessage());
            throw new AdminServiceException("Failed to create user account: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes (deactivates) a user account.
     * 
     * @param userId the ID of the user to delete
     * @return true if deletion was successful
     * @throws AdminServiceException if deletion fails
     */
    public boolean deleteUserAccount(Long userId) throws AdminServiceException {
        try {
            return userService.deactivateUser(userId);
        } catch (UserService.UserServiceException e) {
            logger.severe("Failed to delete user account: " + e.getMessage());
            throw new AdminServiceException("Failed to delete user account: " + e.getMessage(), e);
        }
    }

    /**
     * Sets a role for a user.
     * 
     * @param userId   the ID of the user
     * @param roleName the name of the role to assign
     * @return true if role assignment was successful
     * @throws AdminServiceException if role assignment fails
     */
    public boolean setUserRole(Long userId, String roleName) throws AdminServiceException {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return false;
            }

            Role role = new Role();
            role.setName(roleName);
            userService.assignRole(user, role);
            return true;
        } catch (UserService.UserServiceException e) {
            logger.severe("Failed to set user role: " + e.getMessage());
            throw new AdminServiceException("Failed to set user role: " + e.getMessage(), e);
        }
    }

    public boolean lockUserAccount(Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return false;
        }

        securityService.lockAccount(user.getUsername());
        return true;
    }

    public boolean unlockUserAccount(Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return false;
        }

        securityService.unlockAccount(user.getUsername());
        return true;
    }

    public List<User> listUserAccounts() {
        return userService.searchUsers("");
    }

    public void configureSecurityPolicy(Map<String, String> policySettings) {
        // Apply security policy settings
        // Example: password complexity, session timeout, etc.
    }

    public List<String> viewSecurityLogs() {
        // Return security logs
        return List.of(); // Placeholder
    }

    /**
     * Custom exception for admin service operations.
     */
    public static class AdminServiceException extends Exception {
        public AdminServiceException(String message) {
            super(message);
        }

        public AdminServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
