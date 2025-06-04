package com.company.internalmgmt.modules.opportunity.exception;

/**
 * Exception thrown when a user tries to access an opportunity they don't have permission for.
 */
public class OpportunityAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an OpportunityAccessDeniedException with the specified message.
     *
     * @param message the detail message
     */
    public OpportunityAccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructs an OpportunityAccessDeniedException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public OpportunityAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
} 