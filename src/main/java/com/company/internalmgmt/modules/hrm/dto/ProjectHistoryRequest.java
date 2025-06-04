package com.company.internalmgmt.modules.hrm.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for ProjectHistory create/update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectHistoryRequest {
    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String projectName;
    
    @Size(max = 255, message = "Client name must not exceed 255 characters")
    private String clientName;
    
    @Size(max = 100, message = "Role must not exceed 100 characters")
    private String role;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
} 
