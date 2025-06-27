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

    public boolean createUserAccount(User user, String password, String role) {
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
    }

    public boolean deleteUserAccount(Long userId) {
        return userService.deactivateUser(userId);
    }

    public boolean setUserRole(Long userId, String roleName) {
        User user = userService.findById(userId);
        if (user == null) {
            return false;
        }

        Role role = new Role();
        role.setName(roleName);
        userService.assignRole(user, role);
        return true;
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
}
