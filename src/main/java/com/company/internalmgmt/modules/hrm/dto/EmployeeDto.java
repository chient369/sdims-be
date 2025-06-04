package com.company.internalmgmt.modules.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Employee entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;
    
    private Long userId;

    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code cannot exceed 50 characters")
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

    private TeamDto team;

    private Long reportingLeaderId;
    
    private String reportingLeaderName;

    @Size(max = 50, message = "Current status cannot exceed 50 characters")
    private String currentStatus;

    private LocalDateTime statusUpdatedAt;

    private String profilePictureUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Inner class for Team data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDto {
        private Long id;
        private String name;
    }
} 
