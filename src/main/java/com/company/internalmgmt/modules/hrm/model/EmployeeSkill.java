package com.company.internalmgmt.modules.hrm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.company.internalmgmt.common.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing the relationship between an employee and a skill,
 * including years of experience and assessment levels
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_skills")
public class EmployeeSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "years_experience", precision = 4, scale = 1)
    private BigDecimal yearsExperience;

    @Column(name = "self_assessment_level", length = 50)
    private String selfAssessmentLevel;

    @Column(name = "leader_assessment_level", length = 50)
    private String leaderAssessmentLevel;
    
    @Column(name = "self_comment", columnDefinition = "TEXT")
    private String selfComment;
    
    @Column(name = "leader_comment", columnDefinition = "TEXT")
    private String leaderComment;
    
    /**
     * Get years of experience (alias for yearsExperience)
     * @return years of experience
     */
    public BigDecimal getYearsOfExperience() {
        return this.yearsExperience;
    }
    
    /**
     * Set years of experience (alias for yearsExperience)
     * @param years years of experience
     */
    public void setYearsOfExperience(BigDecimal years) {
        this.yearsExperience = years;
    }
} 
