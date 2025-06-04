package com.company.internalmgmt.modules.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for project history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectHistoryDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeName;

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    private String projectName;

    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    private String clientName;

    @Size(max = 100, message = "Role cannot exceed 100 characters")
    private String role;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
