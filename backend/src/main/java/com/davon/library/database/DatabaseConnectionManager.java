package com.davon.library.database;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection manager for MSSQL database operations.
 * Uses Quarkus Agroal connection pooling for efficient connection management.
 */
@ApplicationScoped
public class DatabaseConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionManager.class);

    @Inject
    AgroalDataSource dataSource;

    /**
     * Gets a database connection from the connection pool.
     * 
     * @return a database connection
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Successfully obtained database connection");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to obtain database connection", e);
            throw e;
        }
    }

    /**
     * Safely closes a database connection and returns it to the pool.
     * 
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Successfully closed database connection");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Checks if the database is accessible.
     * 
     * @return true if database is accessible, false otherwise
     */
    public boolean isDatabaseAccessible() {
        try (Connection connection = getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            logger.error("Database is not accessible", e);
            return false;
        }
    }

    /**
     * Gets database metadata information.
     * 
     * @return database product name and version
     */
    public String getDatabaseInfo() {
        try (Connection connection = getConnection()) {
            var metaData = connection.getMetaData();
            return String.format("%s %s",
                    metaData.getDatabaseProductName(),
                    metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            logger.error("Failed to get database info", e);
            return "Unknown";
        }
    }
}