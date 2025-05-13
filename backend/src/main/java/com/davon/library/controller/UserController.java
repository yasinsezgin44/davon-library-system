package com.davon.library.controller;

import com.davon.library.model.*;
import com.davon.library.service.UserService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public List<User> getUsers(Object filter) {
        if (filter == null) {
            return userService.searchUsers("");
        }
        // Apply filters based on filter object
        return userService.searchUsers(filter.toString());
    }

    public User createUser(Object userData) {
        // Extract user data and create user
        // This is simplified - in a real app, you'd have proper parsing/validation
        User newUser = new Member(); // or appropriate user type
        // Set user properties from userData
        return userService.createUser(newUser);
    }

    public User updateUser(Long id, Object userData) {
        User user = userService.findById(id);
        if (user == null) {
            return null;
        }

        // Update user properties from userData
        return userService.updateUser(id, user);
    }

    public void deleteUser(Long id) {
        userService.deactivateUser(id);
    }

    public User getUserById(Long id) {
        return userService.findById(id);
    }

    public List<User> searchUsers(String query) {
        return userService.searchUsers(query);
    }
}
