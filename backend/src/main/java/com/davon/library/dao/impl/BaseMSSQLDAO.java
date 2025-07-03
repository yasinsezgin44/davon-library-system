package com.davon.library.dao.impl;

import com.davon.library.dao.BaseDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.BaseEntity;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base MSSQL DAO implementation providing common database operations.
 * All MSSQL-specific DAOs should extend this class.
 * 
 * @param <T>  the entity type
 * @param <ID> the primary key type
 */
public abstract class BaseMSSQLDAO<T extends BaseEntity, ID> implements BaseDAO<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    protected DatabaseConnectionManager connectionManager;

    /**
     * Gets the table name for this DAO.
     * 
     * @return the table name
     */
    protected abstract String getTableName();

    /**
     * Gets the primary key column name.
     * 
     * @return the primary key column name
     */
    protected abstract String getIdColumnName();

    /**
     * Maps a ResultSet row to an entity.
     * 
     * @param rs the ResultSet
     * @return the mapped entity
     * @throws SQLException if mapping fails
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    /**
     * Maps an entity to PreparedStatement parameters for INSERT.
     * 
     * @param stmt   the PreparedStatement
     * @param entity the entity to map
     * @throws SQLException if mapping fails
     */
    protected abstract void mapEntityToInsertStatement(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Maps an entity to PreparedStatement parameters for UPDATE.
     * 
     * @param stmt   the PreparedStatement
     * @param entity the entity to map
     * @throws SQLException if mapping fails
     */
    protected abstract void mapEntityToUpdateStatement(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Gets the INSERT SQL statement.
     * 
     * @return the INSERT SQL
     */
    protected abstract String getInsertSQL();

    /**
     * Gets the UPDATE SQL statement.
     * 
     * @return the UPDATE SQL
     */
    protected abstract String getUpdateSQL();

    @Override
    public T save(T entity) throws DAOException {
        String sql = getInsertSQL();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            mapEntityToInsertStatement(stmt, entity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating entity failed, no rows affected.");
            }

            // Get generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    @SuppressWarnings("unchecked")
                    ID generatedId = (ID) generatedKeys.getObject(1);
                    entity.setId((Long) generatedId);
                }
            }

            logger.debug("Successfully saved entity to table {}", getTableName());
            return entity;

        } catch (SQLException e) {
            logger.error("Error saving entity to table {}", getTableName(), e);
            throw new DAOException("Failed to save entity", e);
        }
    }

    @Override
    public T update(T entity) throws DAOException {
        String sql = getUpdateSQL();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set updated timestamp
            entity.setUpdatedAt(LocalDateTime.now());

            mapEntityToUpdateStatement(stmt, entity);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating entity failed, no rows affected.");
            }

            logger.debug("Successfully updated entity in table {}", getTableName());
            return entity;

        } catch (SQLException e) {
            logger.error("Error updating entity in table {}", getTableName(), e);
            throw new DAOException("Failed to update entity", e);
        }
    }

    @Override
    public void delete(T entity) throws DAOException {
        deleteById((ID) entity.getId());
    }

    @Override
    public void deleteById(ID id) throws DAOException {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No entity found with id {} in table {}", id, getTableName());
            } else {
                logger.debug("Successfully deleted entity with id {} from table {}", id, getTableName());
            }

        } catch (SQLException e) {
            logger.error("Error deleting entity with id {} from table {}", id, getTableName(), e);
            throw new DAOException("Failed to delete entity", e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = mapResultSetToEntity(rs);
                    logger.debug("Found entity with id {} in table {}", id, getTableName());
                    return Optional.of(entity);
                }
            }

            logger.debug("No entity found with id {} in table {}", id, getTableName());
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding entity with id {} in table {}", id, getTableName(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName() + " ORDER BY " + getIdColumnName();
        List<T> entities = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }

            logger.debug("Found {} entities in table {}", entities.size(), getTableName());
            return entities;

        } catch (SQLException e) {
            logger.error("Error finding all entities in table {}", getTableName(), e);
            return entities; // Return empty list on error
        }
    }

    @Override
    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                logger.debug("Entity with id {} {} in table {}", id, exists ? "exists" : "does not exist",
                        getTableName());
                return exists;
            }

        } catch (SQLException e) {
            logger.error("Error checking existence of entity with id {} in table {}", id, getTableName(), e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("Count of entities in table {}: {}", getTableName(), count);
                return count;
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting entities in table {}", getTableName(), e);
            return 0;
        }
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM " + getTableName();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsAffected = stmt.executeUpdate();
            logger.debug("Cleared {} entities from table {}", rowsAffected, getTableName());

        } catch (SQLException e) {
            logger.error("Error clearing all entities from table {}", getTableName(), e);
            throw new DAOException("Failed to clear all entities", e);
        }
    }

    /**
     * Helper method to execute a query and map results to entities.
     * 
     * @param sql    the SQL query
     * @param params the query parameters
     * @return list of entities
     */
    protected List<T> executeQuery(String sql, Object... params) {
        List<T> entities = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entities.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Query returned {} entities from table {}", entities.size(), getTableName());

        } catch (SQLException e) {
            logger.error("Error executing query on table {}: {}", getTableName(), sql, e);
        }

        return entities;
    }
}