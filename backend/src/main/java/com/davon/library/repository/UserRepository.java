package com.davon.library.repository;

import com.davon.library.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PanacheRepository<User> {

    default Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    default Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    default List<User> findByStatus(String status) {
        return list("status", status);
    }

    default List<User> findActiveUsers() {
        return list("active", true);
    }

    default boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    default boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    default List<User> searchUsers(String searchTerm) {
        String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
        return list("LOWER(username) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(fullName) LIKE ?1", lowerSearchTerm);
    }
}
