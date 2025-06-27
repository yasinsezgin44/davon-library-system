package com.davon.library.dao;

/**
 * Custom exception for DAO operations.
 * This provides a consistent way to handle data access layer errors.
 */
public class DAOException extends Exception {

    private final String operation;
    private final String entityType;

    /**
     * Constructs a new DAOException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DAOException(String message) {
        super(message);
        this.operation = null;
        this.entityType = null;
    }

    /**
     * Constructs a new DAOException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.entityType = null;
    }

    /**
     * Constructs a new DAOException with operation and entity type context.
     * 
     * @param message    the detail message
     * @param operation  the operation that failed (e.g., "save", "update",
     *                   "delete")
     * @param entityType the type of entity involved
     */
    public DAOException(String message, String operation, String entityType) {
        super(message);
        this.operation = operation;
        this.entityType = entityType;
    }

    /**
     * Constructs a new DAOException with operation and entity type context and
     * cause.
     * 
     * @param message    the detail message
     * @param cause      the cause
     * @param operation  the operation that failed
     * @param entityType the type of entity involved
     */
    public DAOException(String message, Throwable cause, String operation, String entityType) {
        super(message, cause);
        this.operation = operation;
        this.entityType = entityType;
    }

    /**
     * Gets the operation that failed.
     * 
     * @return the operation name, or null if not specified
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the entity type involved in the failed operation.
     * 
     * @return the entity type, or null if not specified
     */
    public String getEntityType() {
        return entityType;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());

        if (operation != null && entityType != null) {
            message.append(" [Operation: ").append(operation)
                    .append(", Entity: ").append(entityType).append("]");
        }

        return message.toString();
    }
}