package com.company.internalmgmt.modules.hrm.exception;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when a project history is not found
 */
public class ProjectHistoryNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new project history not found exception with the specified detail message.
     *
     * @param historyId the ID of the project history that was not found
     */
    public ProjectHistoryNotFoundException(Long historyId) {
        super("Project history not found with ID: " + historyId);
    }

    /**
     * Constructs a new project history not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ProjectHistoryNotFoundException(String message) {
        super(message);
    }
} 
