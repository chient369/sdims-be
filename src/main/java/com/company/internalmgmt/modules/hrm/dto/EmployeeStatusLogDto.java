package com.company.internalmgmt.modules.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Employee Status Log
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeStatusLogDto {
    
    private Long id;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeName;
    
    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
    
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String projectName;
    
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String clientName;
    
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
    
    private LocalDate startDate;
    
    private LocalDate expectedEndDate;
    
    private Integer allocationPercentage;
    
    private Boolean isBillable;
    
    private Long contractId;
    
    private String contractName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long createdBy;
    
    private Long updatedBy;
    
    private String createdByName;
    
    private String updatedByName;
} 
