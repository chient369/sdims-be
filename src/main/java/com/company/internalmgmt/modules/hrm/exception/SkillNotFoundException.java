package com.company.internalmgmt.modules.hrm.exception;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when a skill is not found
 */
public class SkillNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new skill not found exception with the specified detail message.
     *
     * @param skillId the ID of the skill that was not found
     */
    public SkillNotFoundException(Long skillId) {
        super("Skill not found with ID: " + skillId);
    }

    /**
     * Constructs a new skill not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SkillNotFoundException(String message) {
        super(message);
    }
} 
