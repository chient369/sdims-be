package com.company.internalmgmt.modules.hrm.exception;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when a skill category is not found
 */
public class SkillCategoryNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new skill category not found exception with the specified detail message.
     *
     * @param categoryId the ID of the skill category that was not found
     */
    public SkillCategoryNotFoundException(Long categoryId) {
        super("Skill category not found with ID: " + categoryId);
    }

    /**
     * Constructs a new skill category not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SkillCategoryNotFoundException(String message) {
        super(message);
    }
} 
