package com.company.internalmgmt.modules.hrm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Employee responses with additional data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String companyEmail;
    private String internalAccount;
    private String address;
    private String phoneNumber;
    private String emergencyContact;
    private String position;
    
    // Change from String to TeamDto
    private TeamDto team;
    
    private Long reportingLeaderId;
    private String reportingLeaderName;
    private String currentStatus;
    private LocalDateTime statusUpdatedAt;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for detailed view
    private List<EmployeeSkillDto> skills;
    private List<ProjectHistoryDto> projectHistory;
    private List<EmployeeStatusLogDto> statusLogs;
    
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
