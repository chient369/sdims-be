package com.company.internalmgmt.modules.hrm.exception;

/**
 * Exception thrown when there is an error during import/export operations
 */
public class ImportExportException extends RuntimeException {

    /**
     * Constructs a new import/export exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ImportExportException(String message) {
        super(message);
    }

    /**
     * Constructs a new import/export exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ImportExportException(String message, Throwable cause) {
        super(message, cause);
    }
} 
