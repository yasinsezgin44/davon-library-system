package com.davon.library.database;

import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.database.DatabaseHealthCheck;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("Database Connection Tests")
class DatabaseConnectionTest {

    @Inject
    DatabaseConnectionManager connectionManager;

    @Inject
    DatabaseHealthCheck healthCheck;

    @Test
    @DisplayName("Test database connection can be established")
    void testDatabaseConnection() throws SQLException {
        // When
        try (Connection connection = connectionManager.getConnection()) {
            // Then
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(5), "Connection should be valid");
        }
    }

    @Test
    @DisplayName("Test multiple connections can be obtained")
    void testMultipleConnections() throws SQLException {
        // When & Then
        try (Connection conn1 = connectionManager.getConnection();
                Connection conn2 = connectionManager.getConnection()) {

            assertNotNull(conn1);
            assertNotNull(conn2);
            assertNotSame(conn1, conn2, "Should get different connection instances");

            assertTrue(conn1.isValid(5));
            assertTrue(conn2.isValid(5));
        }
    }

    @Test
    @DisplayName("Test database health check returns UP")
    void testDatabaseHealthCheck() {
        // When
        HealthCheckResponse response = healthCheck.call();

        // Then
        assertNotNull(response);
        assertEquals("Database Health Check", response.getName());
        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());

        // Check that response contains some meaningful data
        assertNotNull(response.getData());
        assertTrue(response.getData().containsKey("connection"));
        assertEquals("available", response.getData().get("connection"));
    }

    @Test
    @DisplayName("Test connection can execute basic SQL")
    void testBasicSQLExecution() throws SQLException {
        // When & Then
        try (Connection connection = connectionManager.getConnection()) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT 1 as test_value");

            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt("test_value"));
        }
    }

    @Test
    @DisplayName("Test connection metadata")
    void testConnectionMetadata() throws SQLException {
        // When & Then
        try (Connection connection = connectionManager.getConnection()) {
            var metaData = connection.getMetaData();

            assertNotNull(metaData);
            assertTrue(metaData.getDatabaseProductName().toLowerCase().contains("microsoft"));
            assertTrue(metaData.getDatabaseProductName().toLowerCase().contains("sql server"));
        }
    }

    @Test
    @DisplayName("Test connection autocommit setting")
    void testConnectionAutoCommit() throws SQLException {
        // When & Then
        try (Connection connection = connectionManager.getConnection()) {
            // Most connection pools set autocommit to true by default
            // But we should verify the current state
            boolean autoCommit = connection.getAutoCommit();

            // Test that we can change autocommit setting
            connection.setAutoCommit(!autoCommit);
            assertEquals(!autoCommit, connection.getAutoCommit());

            // Reset to original state
            connection.setAutoCommit(autoCommit);
            assertEquals(autoCommit, connection.getAutoCommit());
        }
    }
}