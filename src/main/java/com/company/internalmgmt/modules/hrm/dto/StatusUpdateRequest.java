package com.company.internalmgmt.modules.hrm.dto;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating employee status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    
    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
    
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String projectName;
    
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String clientName;
    
    private LocalDate startDate;
    
    private LocalDate expectedEndDate;
    
    @Min(value = 0, message = "Allocation percentage must be at least 0")
    @Max(value = 100, message = "Allocation percentage cannot exceed 100")
    private Integer allocationPercentage;
    
    private Boolean isBillable;
    
    private Long contractId;
    
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
} 
