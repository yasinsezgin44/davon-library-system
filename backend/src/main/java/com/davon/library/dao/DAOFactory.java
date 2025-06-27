package com.davon.library.dao;

import com.davon.library.dao.impl.InMemoryBookDAOImpl;
import com.davon.library.dao.impl.InMemoryUserDAOImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * Factory class for creating DAO instances.
 * This follows the Factory pattern and allows for easy switching between
 * different DAO implementations (e.g., in-memory, database, etc.).
 * 
 * This implementation follows the Dependency Inversion Principle by
 * depending on abstractions rather than concrete implementations.
 */
@ApplicationScoped
public class DAOFactory {

    /**
     * Produces a BookDAO instance.
     * This method can be modified to return different implementations
     * based on configuration or environment.
     * 
     * @return a BookDAO implementation
     */
    @Produces
    @Singleton
    public BookDAO createBookDAO() {
        return new InMemoryBookDAOImpl();
    }

    /**
     * Produces a UserDAO instance.
     * This method can be modified to return different implementations
     * based on configuration or environment.
     * 
     * @return a UserDAO implementation
     */
    @Produces
    @Singleton
    public UserDAO createUserDAO() {
        return new InMemoryUserDAOImpl();
    }

    // Future DAO implementations can be added here
    // For example:
    //
    // @Produces
    // @Singleton
    // public LoanDAO createLoanDAO() {
    // return new InMemoryLoanDAOImpl();
    // }
    //
    // @Produces
    // @Singleton
    // public ReservationDAO createReservationDAO() {
    // return new InMemoryReservationDAOImpl();
    // }
}