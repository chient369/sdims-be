package com.company.internalmgmt.modules.hrm.exception;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;

/**
 * Exception thrown when an employee is not found
 */
public class EmployeeNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new employee not found exception with the specified detail message.
     *
     * @param employeeId the ID of the employee that was not found
     */
    public EmployeeNotFoundException(Long employeeId) {
        super("Employee not found with ID: " + employeeId);
    }

    /**
     * Constructs a new employee not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public EmployeeNotFoundException(String message) {
        super(message);
    }
} 
