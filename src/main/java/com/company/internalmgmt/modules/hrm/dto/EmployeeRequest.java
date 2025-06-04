package com.company.internalmgmt.modules.hrm.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating an employee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    private Long userId;

    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$", message = "Employee code must contain only alphanumeric characters, hyphens and underscores")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    private LocalDate birthDate;

    private LocalDate hireDate;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Company email cannot exceed 255 characters")
    private String companyEmail;

    @Size(max = 100, message = "Internal account cannot exceed 100 characters")
    private String internalAccount;

    private String address;

    @Size(max = 50, message = "Phone number cannot exceed 50 characters")
    private String phoneNumber;

    private String emergencyContact;

    @Size(max = 100, message = "Position cannot exceed 100 characters")
    private String position;

    private Long teamId;

    private Long reportingLeaderId;

    @Size(max = 50, message = "Current status cannot exceed 50 characters")
    private String currentStatus;

    private String profilePictureUrl;
} 
