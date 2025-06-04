package com.company.internalmgmt.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to access a resource they don't have permission for
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs a new access denied exception with the specified detail message.
     *
     * @param message the detail message
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructs a new access denied exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
} 