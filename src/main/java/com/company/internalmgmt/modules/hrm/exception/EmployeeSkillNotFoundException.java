package com.company.internalmgmt.modules.hrm.exception;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when an employee skill is not found
 */
public class EmployeeSkillNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new employee skill not found exception with the specified detail message.
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     */
    public EmployeeSkillNotFoundException(Long employeeId, Long skillId) {
        super("Skill ID: " + skillId + " not found for employee ID: " + employeeId);
    }

    /**
     * Constructs a new employee skill not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public EmployeeSkillNotFoundException(String message) {
        super(message);
    }
} 
