package com.davon.library.dao.impl;

import com.davon.library.dao.BaseDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.BaseEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Abstract base implementation for in-memory DAOs.
 * Provides common CRUD operations using ConcurrentHashMap for thread safety.
 * 
 * @param <T> the entity type that extends BaseEntity
 */
public abstract class AbstractInMemoryDAO<T extends BaseEntity> implements BaseDAO<T, Long> {

    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1L);

    /**
     * Gets the entity class name for error messages.
     * 
     * @return the simple name of the entity class
     */
    protected abstract String getEntityName();

    @Override
    public T save(T entity) throws DAOException {
        try {
            validateEntity(entity);

            if (entity.getId() == null) {
                entity.setId(idGenerator.getAndIncrement());
                entity.setCreatedAt(java.time.LocalDateTime.now());
            }

            entity.setUpdatedAt(java.time.LocalDateTime.now());
            storage.put(entity.getId(), cloneEntity(entity));

            return cloneEntity(entity);
        } catch (Exception e) {
            throw new DAOException("Failed to save entity", e, "save", getEntityName());
        }
    }

    @Override
    public T update(T entity) throws DAOException {
        try {
            validateEntity(entity);

            if (entity.getId() == null) {
                throw new DAOException("Cannot update entity without ID", "update", getEntityName());
            }

            if (!storage.containsKey(entity.getId())) {
                throw new DAOException("Entity not found with ID: " + entity.getId(), "update", getEntityName());
            }

            entity.setUpdatedAt(java.time.LocalDateTime.now());
            storage.put(entity.getId(), cloneEntity(entity));

            return cloneEntity(entity);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Failed to update entity", e, "update", getEntityName());
        }
    }

    @Override
    public void delete(T entity) throws DAOException {
        try {
            if (entity == null || entity.getId() == null) {
                throw new DAOException("Cannot delete null entity or entity without ID", "delete", getEntityName());
            }

            deleteById(entity.getId());
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Failed to delete entity", e, "delete", getEntityName());
        }
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        try {
            if (id == null) {
                throw new DAOException("Cannot delete entity with null ID", "deleteById", getEntityName());
            }

            if (!storage.containsKey(id)) {
                throw new DAOException("Entity not found with ID: " + id, "deleteById", getEntityName());
            }

            storage.remove(id);
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Failed to delete entity by ID", e, "deleteById", getEntityName());
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        T entity = storage.get(id);
        return entity != null ? Optional.of(cloneEntity(entity)) : Optional.empty();
    }

    @Override
    public List<T> findAll() {
        return storage.values().stream()
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    /**
     * Validates the entity before saving or updating.
     * Subclasses can override this method to add specific validation logic.
     * 
     * @param entity the entity to validate
     * @throws DAOException if validation fails
     */
    protected void validateEntity(T entity) throws DAOException {
        if (entity == null) {
            throw new DAOException("Entity cannot be null", "validate", getEntityName());
        }
    }

    /**
     * Creates a deep copy of the entity to ensure data integrity.
     * Subclasses should implement this method according to their entity structure.
     * 
     * @param entity the entity to clone
     * @return a deep copy of the entity
     */
    protected abstract T cloneEntity(T entity);

    /**
     * Clears all data from the storage.
     * Useful for testing purposes.
     */
    protected void clear() {
        storage.clear();
        idGenerator.set(1L);
    }
}