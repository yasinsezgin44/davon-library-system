package com.davon.library.integration;

import com.davon.library.dao.*;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.database.DatabaseHealthCheck;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("Database Integration Tests")
class DatabaseIntegrationTest {

    @Inject
    DatabaseConnectionManager connectionManager;

    @Inject
    @Readiness
    DatabaseHealthCheck healthCheck;

    @Inject
    BookDAO bookDAO;

    @Inject
    UserDAO userDAO;

    @Inject
    LoanDAO loanDAO;

    @Inject
    BookCopyDAO bookCopyDAO;

    @Inject
    FineDAO fineDAO;

    @Test
    @DisplayName("Test all DAO beans are injectable")
    void testDAOInjection() {
        // All DAOs should be injected successfully
        assertNotNull(bookDAO, "BookDAO should be injected");
        assertNotNull(userDAO, "UserDAO should be injected");
        assertNotNull(loanDAO, "LoanDAO should be injected");
        assertNotNull(bookCopyDAO, "BookCopyDAO should be injected");
        assertNotNull(fineDAO, "FineDAO should be injected");
    }

    @Test
    @DisplayName("Test database connection is available")
    void testDatabaseConnection() throws Exception {
        try (Connection connection = connectionManager.getConnection()) {
            assertNotNull(connection, "Database connection should not be null");
            assertTrue(connection.isValid(5), "Database connection should be valid");
        }
    }

    @Test
    @DisplayName("Test database health check returns UP")
    void testDatabaseHealthCheck() {
        HealthCheckResponse response = healthCheck.call();

        assertNotNull(response, "Health check response should not be null");
        assertEquals("Database Health Check", response.getName());
        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
    }

    @Test
    @DisplayName("Test basic DAO operations work")
    void testBasicDAOOperations() {
        // Test that the DAOs can execute count operations (simple database queries)
        assertDoesNotThrow(() -> {
            bookDAO.count();
        }, "BookDAO count operation should not throw exception");

        assertDoesNotThrow(() -> {
            userDAO.count();
        }, "UserDAO count operation should not throw exception");

        assertDoesNotThrow(() -> {
            loanDAO.count();
        }, "LoanDAO count operation should not throw exception");

        assertDoesNotThrow(() -> {
            bookCopyDAO.count();
        }, "BookCopyDAO count operation should not throw exception");

        assertDoesNotThrow(() -> {
            fineDAO.count();
        }, "FineDAO count operation should not throw exception");
    }
}