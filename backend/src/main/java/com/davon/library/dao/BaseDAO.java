package com.davon.library.dao;

import java.util.List;
import java.util.Optional;

/**
 * Base Data Access Object interface that defines common CRUD operations.
 * This follows the Repository pattern and provides a contract for all entity
 * DAOs.
 * 
 * @param <T>  the entity type
 * @param <ID> the primary key type
 */
public interface BaseDAO<T, ID> {

    /**
     * Saves an entity to the data store.
     * 
     * @param entity the entity to save
     * @return the saved entity with updated ID if applicable
     * @throws DAOException if the save operation fails
     */
    T save(T entity) throws DAOException;

    /**
     * Updates an existing entity in the data store.
     * 
     * @param entity the entity to update
     * @return the updated entity
     * @throws DAOException if the update operation fails
     */
    T update(T entity) throws DAOException;

    /**
     * Deletes an entity from the data store.
     * 
     * @param entity the entity to delete
     * @throws DAOException if the delete operation fails
     */
    void delete(T entity) throws DAOException;

    /**
     * Deletes an entity by its ID.
     * 
     * @param id the ID of the entity to delete
     * @throws DAOException if the delete operation fails
     */
    void deleteById(ID id) throws DAOException;

    /**
     * Finds an entity by its ID.
     * 
     * @param id the ID to search for
     * @return an Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities from the data store.
     * 
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Checks if an entity exists by its ID.
     * 
     * @param id the ID to check
     * @return true if the entity exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts the total number of entities in the data store.
     * 
     * @return the total count of entities
     */
    long count();

    /**
     * Clears all entities from the data store.
     * This method is primarily for testing purposes.
     * 
     * @throws DAOException if the clear operation fails
     */
    default void clearAll() throws DAOException {
        // Default implementation - can be overridden by implementations
        throw new UnsupportedOperationException("clearAll operation not supported by this DAO");
    }
}