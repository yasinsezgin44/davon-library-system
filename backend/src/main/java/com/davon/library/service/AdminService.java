package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class AdminService {

    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    @Inject
    private UserService userService;

    @Inject
    private SecurityService securityService;

    public boolean createUserAccount(User user, String password, String roleName) throws AdminServiceException {
        try {
            user.setPasswordHash(securityService.hashPassword(password));
            Role role = new Role();
            role.setName(roleName);
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            User createdUser = userService.createUser(user);
            return createdUser != null;
        } catch (UserService.UserServiceException e) {
            logger.severe("Failed to create user account: " + e.getMessage());
            throw new AdminServiceException("Failed to create user account: " + e.getMessage(), e);
        }
    }

    public boolean deleteUserAccount(Long userId) throws AdminServiceException {
        try {
            return userService.deactivateUser(userId);
        } catch (UserService.UserServiceException e) {
            logger.severe("Failed to delete user account: " + e.getMessage());
            throw new AdminServiceException("Failed to delete user account: " + e.getMessage(), e);
        }
    }

    public boolean setUserRole(Long userId, String roleName) throws AdminServiceException {
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return false;
            }
            Role role = new Role();
            role.setName(roleName);
            user.getRoles().add(role);
            userService.updateUser(userId, user);
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
        return userService.getAllUsers();
    }

    public void configureSecurityPolicy(Map<String, String> policySettings) {
    }

    public List<String> viewSecurityLogs() {
        return List.of();
    }

    public static class AdminServiceException extends Exception {
        public AdminServiceException(String message) {
            super(message);
        }

        public AdminServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
