package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final SecurityService securityService;

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
