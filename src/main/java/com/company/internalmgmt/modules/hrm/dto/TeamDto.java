package com.company.internalmgmt.modules.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Team entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDto {

    private Long id;
    
    private String name;
    
    private String department;
    
    private String description;
    
    private Long leaderId;
    
    private String leaderName;
    
    private Long parentTeamId;
    
    private String parentTeamName;
    
    private List<EmployeeDto> members;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
} 