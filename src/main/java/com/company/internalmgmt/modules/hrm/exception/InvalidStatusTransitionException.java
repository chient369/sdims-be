package com.company.internalmgmt.modules.hrm.exception;

/**
 * Exception thrown when an invalid status transition is attempted
 */
public class InvalidStatusTransitionException extends RuntimeException {

    /**
     * Constructs a new invalid status transition exception with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    /**
     * Constructs a new invalid status transition exception with the current and target status.
     *
     * @param currentStatus the current status
     * @param targetStatus the target status
     */
    public InvalidStatusTransitionException(String currentStatus, String targetStatus) {
        super("Invalid status transition from '" + currentStatus + "' to '" + targetStatus + "'");
    }
} 
