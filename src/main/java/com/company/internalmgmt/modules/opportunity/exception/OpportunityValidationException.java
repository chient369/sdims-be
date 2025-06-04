package com.company.internalmgmt.modules.opportunity.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when opportunity data validation fails.
 */
public class OpportunityValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private Map<String, String> errors = new HashMap<>();

    /**
     * Constructs an OpportunityValidationException with the specified message.
     *
     * @param message the detail message
     */
    public OpportunityValidationException(String message) {
        super(message);
    }

    /**
     * Constructs an OpportunityValidationException with the specified message and errors.
     *
     * @param message the detail message
     * @param errors a map of field names to error messages
     */
    public OpportunityValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Add a validation error.
     *
     * @param field the field name
     * @param message the error message
     * @return this exception instance for chaining
     */
    public OpportunityValidationException addError(String field, String message) {
        this.errors.put(field, message);
        return this;
    }

    /**
     * Get the validation errors.
     *
     * @return a map of field names to error messages
     */
    public Map<String, String> getErrors() {
        return errors;
    }
    
    /**
     * Check if there are any validation errors.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
} 