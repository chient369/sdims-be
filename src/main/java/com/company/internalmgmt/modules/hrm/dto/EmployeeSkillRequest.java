package com.company.internalmgmt.modules.hrm.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for EmployeeSkill create/update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkillRequest {
    @NotNull(message = "Skill ID is required")
    private Long skillId;
    
    @DecimalMin(value = "0.0", message = "Years of experience cannot be negative")
    @DecimalMax(value = "99.9", message = "Years of experience is too large")
    private BigDecimal yearsExperience;
    
    private String selfAssessmentLevel;
    
    private String selfComment;
    
    private String leaderAssessmentLevel;
    
    private String leaderComment;
} 
