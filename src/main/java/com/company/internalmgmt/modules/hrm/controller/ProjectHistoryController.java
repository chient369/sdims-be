package com.company.internalmgmt.modules.hrm.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryDto;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.ProjectHistoryMapper;
import com.company.internalmgmt.modules.hrm.model.ProjectHistory;
import com.company.internalmgmt.modules.hrm.service.ProjectHistoryService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing employee project history
 */
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Project History Management", description = "APIs for managing employee project history")
public class ProjectHistoryController {

    private final ProjectHistoryService projectHistoryService;
    private final ProjectHistoryMapper projectHistoryMapper;

    /**
     * GET /api/v1/employees/{employeeId}/projects : Get all project history for an employee
     *
     * @param employeeId the ID of the employee
     * @param page the page number
     * @param size the page size
     * @param sort the sort field
     * @param direction the sort direction
     * @return the ResponseEntity with status 200 (OK) and the list of project history in body
     */
    @GetMapping("/{employeeId}/projects")
    @Operation(summary = "Get all project history for an employee", description = "Get all project history for a specific employee")
    public ResponseEntity<Page<ProjectHistoryDto>> getAllProjectHistory(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        log.debug("REST request to get project history for employee : {}", employeeId);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<ProjectHistoryDto> projects = projectHistoryService.findByEmployeeId(employeeId, pageable);
        return ResponseEntity.ok(projects);
    }

    /**
     * GET /api/v1/employees/{employeeId}/projects/{id} : Get a specific project history for an employee
     *
     * @param employeeId the ID of the employee
     * @param id the ID of the project history
     * @return the ResponseEntity with status 200 (OK) and with body the project history, or with status 404 (Not Found)
     */
    @GetMapping("/{employeeId}/projects/{id}")
    @Operation(summary = "Get a specific project history", description = "Get detailed information about a specific project history by ID")
    public ResponseEntity<ProjectHistoryDto> getProjectHistory(
            @PathVariable Long employeeId,
            @PathVariable Long id) {
        log.debug("REST request to get project history {} for employee : {}", id, employeeId);
        
        ProjectHistoryDto project = projectHistoryService.findById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * POST /api/v1/employees/{employeeId}/projects : Create a new project history for an employee
     *
     * @param employeeId the ID of the employee
     * @param request the project history to create
     * @return the ResponseEntity with status 201 (Created) and with body the new project history
     */
    @PostMapping("/{employeeId}/projects")
    @Operation(summary = "Create a new project history", description = "Create a new project history for an employee")
    public ResponseEntity<ProjectHistoryDto> createProjectHistory(
            @PathVariable Long employeeId,
            @Valid @RequestBody ProjectHistoryRequest request) {
        log.debug("REST request to save project history for employee : {}, request: {}", employeeId, request);
        
        ProjectHistoryDto result = projectHistoryService.addProjectHistory(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/v1/employees/{employeeId}/projects/{id} : Update an existing project history
     *
     * @param employeeId the ID of the employee
     * @param id the ID of the project history to update
     * @param request the project history to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated project history
     */
    @PutMapping("/{employeeId}/projects/{id}")
    @Operation(summary = "Update an existing project history", description = "Update an existing project history")
    public ResponseEntity<ProjectHistoryDto> updateProjectHistory(
            @PathVariable Long employeeId,
            @PathVariable Long id,
            @Valid @RequestBody ProjectHistoryRequest request) {
        log.debug("REST request to update project history {} for employee : {}, request: {}", id, employeeId, request);
        
        ProjectHistoryDto result = projectHistoryService.updateProjectHistory(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/v1/employees/{employeeId}/projects/{id} : Delete a project history
     *
     * @param employeeId the ID of the employee
     * @param id the ID of the project history to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{employeeId}/projects/{id}")
    @Operation(summary = "Delete a project history", description = "Delete a project history")
    public ResponseEntity<Void> deleteProjectHistory(
            @PathVariable Long employeeId,
            @PathVariable Long id) {
        log.debug("REST request to delete project history {} for employee : {}", id, employeeId);
        
        projectHistoryService.deleteProjectHistory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/employees/{employeeId}/projects/current : Get current active projects for an employee
     *
     * @param employeeId the ID of the employee
     * @return the ResponseEntity with status 200 (OK) and the list of current project history in body
     */
    @GetMapping("/{employeeId}/projects/current")
    @Operation(summary = "Get current active projects", description = "Get all current active projects for an employee")
    public ResponseEntity<List<ProjectHistoryDto>> getCurrentProjects(@PathVariable Long employeeId) {
        log.debug("REST request to get current projects for employee : {}", employeeId);
        
        List<ProjectHistory> activeProjects = projectHistoryService.findActiveProjectHistoryByEmployeeId(employeeId);
        List<ProjectHistoryDto> projects = activeProjects.stream()
            .map(projectHistoryMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    /**
     * GET /api/v1/employees/projects/search : Search project history by project name across all employees
     *
     * @param projectName the project name to search for
     * @param page the page number
     * @param size the page size
     * @return the ResponseEntity with status 200 (OK) and the list of project history in body
     */
    @GetMapping("/projects/search")
    @Operation(summary = "Search project history by project name", description = "Search project history by project name across all employees")
    public ResponseEntity<List<ProjectHistoryDto>> searchProjectsByName(
            @RequestParam String projectName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to search projects by name : {}", projectName);
        
        List<ProjectHistoryDto> projects = projectHistoryService.findByProjectName(projectName);
        return ResponseEntity.ok(projects);
    }
} 
