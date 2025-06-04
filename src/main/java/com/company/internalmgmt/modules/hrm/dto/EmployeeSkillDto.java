package com.company.internalmgmt.modules.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for EmployeeSkill entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkillDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeName;

    @NotNull(message = "Skill ID is required")
    private Long skillId;
    
    private String skillName;
    
    private String skillCategoryName;

    @DecimalMin(value = "0.0", message = "Years of experience must be at least 0")
    @DecimalMax(value = "99.9", message = "Years of experience cannot exceed 99.9")
    private BigDecimal yearsExperience;

    @Size(max = 50, message = "Self assessment level cannot exceed 50 characters")
    private String selfAssessmentLevel;

    @Size(max = 50, message = "Leader assessment level cannot exceed 50 characters")
    private String leaderAssessmentLevel;
    
    @Size(max = 1000, message = "Self comment cannot exceed 1000 characters")
    private String selfComment;
    
    @Size(max = 1000, message = "Leader comment cannot exceed 1000 characters")
    private String leaderComment;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
