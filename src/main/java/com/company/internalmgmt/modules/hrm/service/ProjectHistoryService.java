package com.company.internalmgmt.modules.hrm.service;

import com.company.internalmgmt.modules.hrm.model.ProjectHistory;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryDto;

/**
 * Service interface for managing project history
 */
public interface ProjectHistoryService {

    /**
     * Find project history for a specific employee
     * 
     * @param employeeId the employee ID
     * @param pageable pagination info
     * @return page of project history DTOs
     */
    Page<ProjectHistoryDto> findByEmployeeId(Long employeeId, Pageable pageable);
    
    /**
     * Find all project history for a specific employee
     * 
     * @param employeeId the employee ID
     * @return list of project history DTOs
     */
    List<ProjectHistoryDto> findAllByEmployeeId(Long employeeId);
    
    /**
     * Find a project history entry by ID
     * 
     * @param id the project history ID
     * @return the project history DTO
     */
    ProjectHistoryDto findById(Long id);
    
    /**
     * Find project history entries by project name
     * 
     * @param projectName the project name
     * @return list of project history DTOs
     */
    List<ProjectHistoryDto> findByProjectName(String projectName);
    
    /**
     * Find project history entries by client name
     * 
     * @param clientName the client name
     * @return list of project history DTOs
     */
    List<ProjectHistoryDto> findByClientName(String clientName);
    
    /**
     * Find project history entries that overlap with a date range
     * 
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of project history DTOs
     */
    List<ProjectHistoryDto> findOverlappingWithDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Add a project history entry for an employee
     * 
     * @param employeeId the employee ID
     * @param request the project history request
     * @return the created project history DTO
     */
    ProjectHistoryDto addProjectHistory(Long employeeId, ProjectHistoryRequest request);
    
    /**
     * Update a project history entry
     * 
     * @param id the project history ID
     * @param request the project history request
     * @return the updated project history DTO
     */
    ProjectHistoryDto updateProjectHistory(Long id, ProjectHistoryRequest request);
    
    /**
     * Delete a project history entry
     * 
     * @param id the project history ID
     */
    void deleteProjectHistory(Long id);

    /**
     * Get project history by employee ID
     * @param employeeId the employee ID
     * @return List of project history for the specified employee
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    List<ProjectHistory> getProjectHistoryByEmployeeId(Long employeeId);

    /**
     * Get project history by employee ID with pagination
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return Page of project history for the specified employee
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    Page<ProjectHistory> getProjectHistoryByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Get project history by ID
     * @param id the project history ID
     * @return the project history
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    ProjectHistory getProjectHistoryById(Long id);

    /**
     * Create a new project history entry
     * @param employeeId the employee ID
     * @param projectHistory the project history to create
     * @return the created project history
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if employee not found
     */
    @PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    ProjectHistory createProjectHistory(Long employeeId, ProjectHistory projectHistory);

    /**
     * Add project history to an employee
     * @param employeeId the employee ID
     * @param request the project history request
     * @return the created project history
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if employee not found
     */
    @PreAuthorize("hasAnyAuthority('project-history:create:all', 'project-history:create:team')")
    ProjectHistory addProjectHistoryToEmployee(Long employeeId, ProjectHistoryRequest request);

    /**
     * Update an existing project history entry
     * @param id the project history ID
     * @param projectHistory the updated project history
     * @return the updated project history
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    ProjectHistory updateProjectHistory(Long id, ProjectHistory projectHistory);

    /**
     * Find project history by date range
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return List of project history within the specified date range
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    List<ProjectHistory> findProjectHistoryByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * Find project history by project name (partial match, case-insensitive)
     * @param projectName the project name
     * @return List of project history with matching project name
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    List<ProjectHistory> findProjectHistoryByProjectName(String projectName);

    /**
     * Find project history by client name (partial match, case-insensitive)
     * @param clientName the client name
     * @return List of project history with matching client name
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    List<ProjectHistory> findProjectHistoryByClientName(String clientName);

    /**
     * Find active project history for an employee (where end date is null)
     * @param employeeId the employee ID
     * @return List of active project history for the specified employee
     */
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    List<ProjectHistory> findActiveProjectHistoryByEmployeeId(Long employeeId);
} 
