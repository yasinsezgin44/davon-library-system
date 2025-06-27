package com.davon.library.dao.impl;

import com.davon.library.dao.UserDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.User;
import com.davon.library.model.Member;
import com.davon.library.model.Librarian;
import com.davon.library.model.Admin;
import com.davon.library.model.Role;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserDAO.
 * This implementation follows SOLID principles by separating data access
 * concerns.
 */
@ApplicationScoped
public class InMemoryUserDAOImpl extends AbstractInMemoryDAO<User> implements UserDAO {

    @Override
    protected String getEntityName() {
        return "User";
    }

    @Override
    protected User cloneEntity(User user) {
        if (user == null)
            return null;

        // Create appropriate subclass instance
        User clonedUser;
        if (user instanceof Member) {
            Member member = (Member) user;
            clonedUser = Member.builder()
                    .address(member.getAddress())
                    .membershipType(member.getMembershipType())
                    .membershipDate(member.getMembershipDate())
                    .build();
        } else if (user instanceof Librarian) {
            Librarian librarian = (Librarian) user;
            clonedUser = Librarian.builder()
                    .employeeId(librarian.getEmployeeId())
                    .department(librarian.getDepartment())
                    .build();
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            clonedUser = Admin.builder()
                    .adminLevel(admin.getAdminLevel())
                    .permissions(admin.getPermissions())
                    .lastLoginDate(admin.getLastLoginDate())
                    .build();
        } else {
            clonedUser = User.builder().build();
        }

        // Set common properties
        clonedUser.setId(user.getId());
        clonedUser.setUsername(user.getUsername());
        clonedUser.setPasswordHash(user.getPasswordHash());
        clonedUser.setEmail(user.getEmail());
        clonedUser.setFullName(user.getFullName());
        clonedUser.setPhoneNumber(user.getPhoneNumber());
        clonedUser.setActive(user.isActive());
        clonedUser.setStatus(user.getStatus());
        clonedUser.setRole(user.getRole());
        clonedUser.setCreatedAt(user.getCreatedAt());
        clonedUser.setUpdatedAt(user.getUpdatedAt());

        return clonedUser;
    }

    @Override
    protected void validateEntity(User user) throws DAOException {
        super.validateEntity(user);

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DAOException("Username cannot be null or empty", "validate", getEntityName());
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new DAOException("Email cannot be null or empty", "validate", getEntityName());
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new DAOException("Password hash cannot be null or empty", "validate", getEntityName());
        }

        // Check for duplicate username (excluding the current user when updating)
        Optional<User> existingUserByUsername = findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getId().equals(user.getId())) {
            throw new DAOException("User with username " + user.getUsername() + " already exists", "validate",
                    getEntityName());
        }

        // Check for duplicate email (excluding the current user when updating)
        Optional<User> existingUserByEmail = findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(user.getId())) {
            throw new DAOException("User with email " + user.getEmail() + " already exists", "validate",
                    getEntityName());
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        return storage.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .map(this::cloneEntity)
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        return storage.values().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .map(this::cloneEntity)
                .findFirst();
    }

    @Override
    public List<User> findByRole(Role role) {
        if (role == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findActiveUsers() {
        return storage.values().stream()
                .filter(User::isActive)
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findInactiveUsers() {
        return storage.values().stream()
                .filter(user -> !user.isActive())
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        return storage.values().stream()
                .filter(user -> matchesSearchTerm(user, lowerSearchTerm))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return List.of();
        }

        return storage.values().stream()
                .filter(user -> status.equals(user.getStatus()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(user -> user.getCreatedAt() != null &&
                        !user.getCreatedAt().isBefore(startDate) &&
                        !user.getCreatedAt().isAfter(endDate))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByRole(Role role) {
        if (role == null) {
            return 0;
        }

        return storage.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .count();
    }

    @Override
    public long countActiveUsers() {
        return storage.values().stream()
                .filter(User::isActive)
                .count();
    }

    /**
     * Helper method to check if a user matches the search term.
     * 
     * @param user       the user to check
     * @param searchTerm the search term (already lowercased)
     * @return true if the user matches the search term
     */
    private boolean matchesSearchTerm(User user, String searchTerm) {
        return (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchTerm)) ||
                (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchTerm)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchTerm));
    }
}