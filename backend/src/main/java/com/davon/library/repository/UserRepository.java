package com.davon.library.repository;

import com.davon.library.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<User> findByStatus(String status) {
        return find("status", status).list();
    }

    public List<User> findActiveUsers() {
        return find("active", true).list();
    }

    public List<User> findByFullNameContaining(String name) {
        return find("LOWER(fullName) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    public boolean existsByUsername(String username) {
        return find("username", username).count() > 0;
    }

    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }

    public List<User> searchUsers(String searchTerm) {
        String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
        return find("LOWER(username) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(fullName) LIKE ?1", lowerSearchTerm).list();
    }
}
