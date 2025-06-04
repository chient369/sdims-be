package com.company.internalmgmt.modules.hrm.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for EmployeeSkill evaluation by leaders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkillEvaluationRequest {
    @NotBlank(message = "Assessment level is required")
    @Size(max = 50, message = "Assessment level must not exceed 50 characters")
    private String leaderAssessmentLevel;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String leaderComment;
} 
