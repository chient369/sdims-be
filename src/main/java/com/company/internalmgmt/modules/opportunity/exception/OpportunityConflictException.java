package com.company.internalmgmt.modules.opportunity.exception;

import java.util.Map;

/**
 * Exception thrown when there is a conflict with opportunity data.
 * For example, when trying to sync data while another sync is in progress.
 */
public class OpportunityConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private Map<String, Object> conflictData;

    /**
     * Constructs an OpportunityConflictException with the specified message.
     *
     * @param message the detail message
     */
    public OpportunityConflictException(String message) {
        super(message);
    }

    /**
     * Constructs an OpportunityConflictException with the specified message and conflict data.
     *
     * @param message the detail message
     * @param conflictData additional data about the conflict
     */
    public OpportunityConflictException(String message, Map<String, Object> conflictData) {
        super(message);
        this.conflictData = conflictData;
    }

    /**
     * Constructs an OpportunityConflictException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public OpportunityConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Get the conflict data associated with this exception.
     *
     * @return the conflict data
     */
    public Map<String, Object> getConflictData() {
        return conflictData;
    }
} 