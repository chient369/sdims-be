package com.company.internalmgmt.modules.hrm.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillEvaluationRequest;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.EmployeeSkillMapper;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import com.company.internalmgmt.modules.hrm.model.Skill;
import com.company.internalmgmt.modules.hrm.service.EmployeeSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing employee skills
 */
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Skills Management", description = "APIs for managing employee skills")
public class EmployeeSkillController {

    private final EmployeeSkillService employeeSkillService;
    private final EmployeeSkillMapper employeeSkillMapper;

    /**
     * GET /api/v1/employees/{employeeId}/skills : Get all skills for an employee
     *
     * @param employeeId the ID of the employee
     * @param page the page number
     * @param size the page size
     * @param sort the sort field
     * @param direction the sort direction
     * @return the ResponseEntity with status 200 (OK) and the list of skills in body
     */
    @GetMapping("/{employeeId}/skills")
    @Operation(summary = "Get all skills for an employee", description = "Get all skills for a specific employee")
    public ResponseEntity<Page<EmployeeSkillDto>> getAllEmployeeSkills(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "skill.name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get skills for employee : {}", employeeId);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<EmployeeSkillDto> skills = employeeSkillService.findByEmployeeId(employeeId, pageable);
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /api/v1/employees/{employeeId}/skills/all : Get all skills for an employee (no pagination)
     *
     * @param employeeId the ID of the employee
     * @return the ResponseEntity with status 200 (OK) and the list of skills in body
     */
    @GetMapping("/{employeeId}/skills/all")
    @Operation(summary = "Get all skills for an employee (no pagination)", description = "Get all skills for a specific employee without pagination")
    public ResponseEntity<List<EmployeeSkillDto>> getAllEmployeeSkillsList(@PathVariable Long employeeId) {
        log.debug("REST request to get all skills for employee : {}", employeeId);
        
        List<EmployeeSkillDto> skills = employeeSkillService.findAllByEmployeeId(employeeId);
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /api/v1/employees/{employeeId}/skills/{skillId} : Get a specific skill for an employee
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     * @return the ResponseEntity with status 200 (OK) and with body the skill, or with status 404 (Not Found)
     */
    @GetMapping("/{employeeId}/skills/{skillId}")
    @Operation(summary = "Get a specific skill for an employee", description = "Get detailed information about a specific skill of an employee")
    public ResponseEntity<EmployeeSkillDto> getEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId) {
        log.debug("REST request to get skill {} for employee : {}", skillId, employeeId);
        
        EmployeeSkillDto skill = employeeSkillService.findByEmployeeIdAndSkillId(employeeId, skillId);
        return ResponseEntity.ok(skill);
    }

    /**
     * POST /api/v1/employees/{employeeId}/skills : Add a skill to an employee
     *
     * @param employeeId the ID of the employee
     * @param request the skill request to add
     * @return the ResponseEntity with status 201 (Created) and with body the new skill
     */
    @PostMapping("/{employeeId}/skills")
    @Operation(summary = "Add a skill to an employee", description = "Add a new skill to an employee")
    public ResponseEntity<EmployeeSkillDto> addSkillToEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeSkillRequest request) {
        log.debug("REST request to add skill to employee : {}, request: {}", employeeId, request);
        
        EmployeeSkillDto result = employeeSkillService.addSkill(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/v1/employees/{employeeId}/skills/{skillId} : Update a skill for an employee
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     * @param request the skill request to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated skill
     */
    @PutMapping("/{employeeId}/skills/{skillId}")
    @Operation(summary = "Update a skill for an employee", description = "Update a skill for an employee")
    public ResponseEntity<EmployeeSkillDto> updateEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            @Valid @RequestBody EmployeeSkillRequest request) {
        log.debug("REST request to update skill {} for employee : {}, request: {}", skillId, employeeId, request);
        
        EmployeeSkillDto result = employeeSkillService.updateSkill(employeeId, skillId, request);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/v1/employees/{employeeId}/skills/{skillId} : Remove a skill from an employee
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{employeeId}/skills/{skillId}")
    @Operation(summary = "Remove a skill from an employee", description = "Remove a skill from an employee")
    public ResponseEntity<Void> deleteEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId) {
        log.debug("REST request to delete skill {} from employee : {}", skillId, employeeId);
        
        employeeSkillService.deleteSkill(employeeId, skillId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/employees/{employeeId}/skills/{skillId}/evaluate : Evaluate a skill for an employee
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     * @param request the evaluation request
     * @return the ResponseEntity with status 200 (OK) and with body the updated skill
     */
    @PutMapping("/{employeeId}/skills/{skillId}/evaluate")
    @Operation(summary = "Evaluate a skill for an employee", description = "Evaluate a skill for an employee (leader assessment)")
    public ResponseEntity<EmployeeSkillDto> evaluateEmployeeSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId,
            @Valid @RequestBody EmployeeSkillEvaluationRequest request) {
        log.debug("REST request to evaluate skill {} for employee : {}, level: {}, comment: {}", 
                skillId, employeeId, request.getLeaderAssessmentLevel(), request.getLeaderComment());
        
        EmployeeSkillDto result = employeeSkillService.evaluateSkill(
                employeeId, 
                skillId, 
                request.getLeaderAssessmentLevel(),
                request.getLeaderComment());
        return ResponseEntity.ok(result);
    }

    /**
     * HEAD /api/v1/employees/{employeeId}/skills/{skillId} : Check if an employee has a specific skill
     *
     * @param employeeId the ID of the employee
     * @param skillId the ID of the skill
     * @return the ResponseEntity with status 200 (OK) if the employee has the skill, or 404 (Not Found) otherwise
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/{employeeId}/skills/{skillId}")
    @Operation(summary = "Check if an employee has a specific skill", description = "Check if an employee has a specific skill")
    public ResponseEntity<Void> checkEmployeeHasSkill(
            @PathVariable Long employeeId,
            @PathVariable Long skillId) {
        log.debug("REST request to check if employee {} has skill {}", employeeId, skillId);
        
        boolean hasSkill = employeeSkillService.hasSkill(employeeId, skillId);
        return hasSkill ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
} 
