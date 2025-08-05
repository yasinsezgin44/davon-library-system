package com.davon.library.repository;

import com.davon.library.model.User;
import com.davon.library.model.UserStatus;
import com.davon.library.model.Member;
import com.davon.library.model.Librarian;
import com.davon.library.model.Admin;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Repository for User entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based UserDAO implementation.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return the user if found, empty otherwise
     */
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    /**
     * Finds a user by email.
     * 
     * @param email the email to search for
     * @return the user if found, empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Finds users by status.
     * 
     * @param status the user status
     * @return list of users with the given status
     */
    public List<User> findByStatus(UserStatus status) {
        return find("status", status).list();
    }

    /**
     * Finds all active users.
     * 
     * @return list of active users
     */
    public List<User> findActiveUsers() {
        return find("active", true).list();
    }

    /**
     * Finds users by full name containing the given string (case-insensitive).
     * 
     * @param name the name substring to search for
     * @return list of users matching the name
     */
    public List<User> findByFullNameContaining(String name) {
        return find("LOWER(full_name) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    /**
     * Checks if a user exists by username.
     * 
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return find("username", username).count() > 0;
    }

    /**
     * Checks if a user exists by email.
     * 
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }

    /**
     * Searches users by username, email, or full name.
     * 
     * @param searchTerm the search term
     * @return list of users matching the search criteria
     */
    public List<User> searchUsers(String searchTerm) {
        String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
        List<User> allResults = new ArrayList<>();

        try {
            // Search in Members
            List<Member> memberResults = getEntityManager()
                    .createQuery(
                            "SELECT m FROM Member m WHERE LOWER(m.username) LIKE :searchTerm OR LOWER(m.email) LIKE :searchTerm OR LOWER(m.fullName) LIKE :searchTerm",
                            Member.class)
                    .setParameter("searchTerm", lowerSearchTerm)
                    .getResultList();
            allResults.addAll(memberResults);

            // Search in Librarians
            List<Librarian> librarianResults = getEntityManager()
                    .createQuery(
                            "SELECT l FROM Librarian l WHERE LOWER(l.username) LIKE :searchTerm OR LOWER(l.email) LIKE :searchTerm OR LOWER(l.fullName) LIKE :searchTerm",
                            Librarian.class)
                    .setParameter("searchTerm", lowerSearchTerm)
                    .getResultList();
            allResults.addAll(librarianResults);

            // Search in Admins
            List<Admin> adminResults = getEntityManager()
                    .createQuery(
                            "SELECT a FROM Admin a WHERE LOWER(a.username) LIKE :searchTerm OR LOWER(a.email) LIKE :searchTerm OR LOWER(a.fullName) LIKE :searchTerm",
                            Admin.class)
                    .setParameter("searchTerm", lowerSearchTerm)
                    .getResultList();
            allResults.addAll(adminResults);

        } catch (Exception e) {
            // If any query fails, return empty list
            return new ArrayList<>();
        }

        return allResults;
    }

    /**
     * Gets all users by querying concrete subclasses and combining results.
     * 
     * @return list of all users
     */
    public List<User> getAllUsersWithInheritance() {
        List<User> allUsers = new ArrayList<>();

        // Query each concrete subclass separately
        try {
            // Query Members
            List<Member> members = getEntityManager()
                    .createQuery("SELECT m FROM Member m", Member.class)
                    .getResultList();
            allUsers.addAll(members);

            // Query Librarians
            List<Librarian> librarians = getEntityManager()
                    .createQuery("SELECT l FROM Librarian l", Librarian.class)
                    .getResultList();
            allUsers.addAll(librarians);

            // Query Admins
            List<Admin> admins = getEntityManager()
                    .createQuery("SELECT a FROM Admin a", Admin.class)
                    .getResultList();
            allUsers.addAll(admins);

        } catch (Exception e) {
            // If any query fails, try a simpler approach
            return new ArrayList<>();
        }

        return allUsers;
    }
}