package com.company.internalmgmt.modules.hrm.exception;

/**
 * Exception thrown when employee data is invalid
 */
public class InvalidEmployeeDataException extends RuntimeException {

    /**
     * Constructs a new invalid employee data exception with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidEmployeeDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new invalid employee data exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidEmployeeDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 
