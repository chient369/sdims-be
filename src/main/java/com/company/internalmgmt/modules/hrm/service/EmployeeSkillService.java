package com.company.internalmgmt.modules.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillRequest;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service interface for managing employee skills
 */
public interface EmployeeSkillService {
    
    /**
     * Find skills for a specific employee
     * 
     * @param employeeId the employee ID
     * @param pageable pagination info
     * @return page of employee skill DTOs
     */
    Page<EmployeeSkillDto> findByEmployeeId(Long employeeId, Pageable pageable);
    
    /**
     * Find all skills for a specific employee
     * 
     * @param employeeId the employee ID
     * @return list of employee skill DTOs
     */
    List<EmployeeSkillDto> findAllByEmployeeId(Long employeeId);
    
    /**
     * Find a specific skill for an employee
     * 
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @return the employee skill DTO
     */
    EmployeeSkillDto findByEmployeeIdAndSkillId(Long employeeId, Long skillId);
    
    /**
     * Find employees with specific skills
     * 
     * @param skillIds list of skill IDs
     * @return list of employee skills
     */
    List<EmployeeSkill> findBySkillIds(List<Long> skillIds);
    
    /**
     * Find employees with specific skills and minimum experience
     * 
     * @param skillIds list of skill IDs
     * @param minExperience minimum years of experience
     * @return list of employee skills
     */
    List<EmployeeSkill> findBySkillIdsAndMinExperience(List<Long> skillIds, Double minExperience);
    
    /**
     * Add a skill to an employee
     * 
     * @param employeeId the employee ID
     * @param request the employee skill request
     * @return the created employee skill DTO
     */
    EmployeeSkillDto addSkill(Long employeeId, EmployeeSkillRequest request);
    
    /**
     * Update a skill for an employee
     * 
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @param request the employee skill request
     * @return the updated employee skill DTO
     */
    EmployeeSkillDto updateSkill(Long employeeId, Long skillId, EmployeeSkillRequest request);
    
    /**
     * Delete a skill from an employee
     * 
     * @param employeeId the employee ID
     * @param skillId the skill ID
     */
    void deleteSkill(Long employeeId, Long skillId);
    
    /**
     * Evaluate a skill for an employee (update leader assessment)
     * 
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @param level the assessment level
     * @param comment the leader's comment
     * @return the updated employee skill DTO
     */
    EmployeeSkillDto evaluateSkill(Long employeeId, Long skillId, String level, String comment);
    
    /**
     * Check if an employee has a specific skill
     * 
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @return true if the employee has the specified skill
     */
    boolean hasSkill(Long employeeId, Long skillId);

    /**
     * Get employee skills by employee ID
     * @param employeeId the employee ID
     * @return List of employee skills
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:read:all', 'employee-skill:read:team', 'employee-skill:read:own')")
    List<EmployeeSkill> getEmployeeSkillsByEmployeeId(Long employeeId);

    /**
     * Get employee skill by ID
     * @param id the employee skill ID
     * @return the employee skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:read:all', 'employee-skill:read:team', 'employee-skill:read:own')")
    EmployeeSkill getEmployeeSkillById(Long id);

    /**
     * Get employee skill by employee ID and skill ID
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @return the employee skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:read:all', 'employee-skill:read:team', 'employee-skill:read:own')")
    EmployeeSkill getEmployeeSkillByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    /**
     * Add a skill to an employee
     * @param employeeId the employee ID
     * @param employeeSkill the employee skill to add
     * @return the created employee skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if employee or skill not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if employee already has the skill
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:create:all', 'employee-skill:create:team', 'employee-skill:create:own')")
    EmployeeSkill addSkillToEmployee(Long employeeId, EmployeeSkill employeeSkill);

    /**
     * Update an employee skill
     * @param id the employee skill ID
     * @param employeeSkill the updated employee skill
     * @return the updated employee skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:update:all', 'employee-skill:update:team', 'employee-skill:update:own')")
    EmployeeSkill updateEmployeeSkill(Long id, EmployeeSkill employeeSkill);

    /**
     * Evaluate an employee skill (by leader)
     * @param id the employee skill ID
     * @param leaderAssessmentLevel the leader's assessment level
     * @param leaderComment the leader's comment
     * @return the updated employee skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAuthority('employee-skill:evaluate')")
    EmployeeSkill evaluateEmployeeSkill(Long id, String leaderAssessmentLevel, String leaderComment);

    /**
     * Remove a skill from an employee
     * @param id the employee skill ID
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:delete:all', 'employee-skill:delete:team', 'employee-skill:delete:own')")
    void removeEmployeeSkill(Long id);

    /**
     * Remove a skill from an employee by employee ID and skill ID
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-skill:delete:all', 'employee-skill:delete:team', 'employee-skill:delete:own')")
    void removeEmployeeSkillByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    /**
     * Find employees by skill and level
     * @param skillId the skill ID
     * @param level the skill level (Basic, Intermediate, Advanced)
     * @return List of employee skills matching the criteria
     */
    @PreAuthorize("hasAnyAuthority('employee-suggest:read', 'employee-skill:read:all', 'employee-skill:read:team')")
    List<EmployeeSkill> findEmployeesBySkillAndLevel(Long skillId, String level);

    /**
     * Find employees by skill and minimum years of experience
     * @param skillId the skill ID
     * @param years the minimum years of experience
     * @return List of employee skills matching the criteria
     */
    @PreAuthorize("hasAnyAuthority('employee-suggest:read', 'employee-skill:read:all', 'employee-skill:read:team')")
    List<EmployeeSkill> findEmployeesBySkillAndMinYears(Long skillId, double years);
} 
