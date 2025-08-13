package com.davon.library.service;

import com.davon.library.model.Role;
import com.davon.library.model.User;
import com.davon.library.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @Inject
    private UserService userService;

    @Inject
    private RoleRepository roleRepository;

    @Transactional
    public User createUserWithRole(User user, String roleName) {
        log.info("Admin creating user {} with role {}", user.getUsername(), roleName);
        Role role = roleRepository.find("name", roleName).firstResultOptional()
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    roleRepository.persist(newRole);
                    return newRole;
                });
        user.setRoles(Set.of(role));
        return userService.createUser(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Admin deleting user {}", userId);
        userService.deleteUser(userId);
    }

    @Transactional
    public User assignRoleToUser(Long userId, String roleName) {
        log.info("Admin assigning role {} to user {}", roleName, userId);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Found user: {}", user.getUsername());

        Role role = roleRepository.find("name", roleName).firstResultOptional()
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        log.info("Found role: {}", role.getName());

        user.getRoles().add(role);
        log.info("Assigned role {} to user {}. Roles are now: {}", roleName, userId, user.getRoles());

        return user;
    }
}
