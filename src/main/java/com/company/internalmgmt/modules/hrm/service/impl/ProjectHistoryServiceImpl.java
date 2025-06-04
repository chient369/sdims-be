package com.company.internalmgmt.modules.hrm.service.impl;

import com.company.internalmgmt.common.exception.ResourceAlreadyExistsException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryDto;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.ProjectHistoryMapper;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.ProjectHistory;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.ProjectHistoryRepository;
import com.company.internalmgmt.modules.hrm.service.ProjectHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProjectHistoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectHistoryServiceImpl implements ProjectHistoryService {

    private final ProjectHistoryRepository projectHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectHistoryMapper projectHistoryMapper;

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public Page<ProjectHistoryDto> findByEmployeeId(Long employeeId, Pageable pageable) {
        log.debug("Request to get project history by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        Page<ProjectHistory> page = projectHistoryRepository.findByEmployeeIdOrderByStartDateDesc(employeeId, pageable);
        return page.map(projectHistoryMapper::toDto);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public List<ProjectHistoryDto> findAllByEmployeeId(Long employeeId) {
        log.debug("Request to get all project history by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        List<ProjectHistory> projectHistoryList = projectHistoryRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
        return projectHistoryList.stream()
                .map(projectHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
   // @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public ProjectHistoryDto findById(Long id) {
        log.debug("Request to get project history by id: {}", id);
        
        ProjectHistory projectHistory = findProjectHistoryById(id);
        return projectHistoryMapper.toDto(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistoryDto> findByProjectName(String projectName) {
        log.debug("Request to get project history by projectName: {}", projectName);
        
        List<ProjectHistory> projectHistoryList = projectHistoryRepository.findByProjectNameContainingIgnoreCase(projectName);
        return projectHistoryList.stream()
                .map(projectHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistoryDto> findByClientName(String clientName) {
        log.debug("Request to get project history by clientName: {}", clientName);
        
        List<ProjectHistory> projectHistoryList = projectHistoryRepository.findByClientNameContainingIgnoreCase(clientName);
        return projectHistoryList.stream()
                .map(projectHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistoryDto> findOverlappingWithDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        log.debug("Request to get project history overlapping with date range: {} to {} for employee {}", startDate, endDate, employeeId);
        List<ProjectHistory> projectHistoryList = projectHistoryRepository.findOverlappingWithDateRange(employeeId, startDate, endDate);
        return projectHistoryList.stream()
                .map(projectHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
   // @PreAuthorize("hasAnyAuthority('project-history:create:all', 'project-history:create:team')")
    @Transactional
    public ProjectHistoryDto addProjectHistory(Long employeeId, ProjectHistoryRequest request) {
        log.debug("Request to add project history for employee: {}, request: {}", employeeId, request);
        
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check for overlapping projects
        if (projectHistoryRepository.existsOverlappingProject(
                employeeId, request.getStartDate(), request.getEndDate())) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has a project assigned during this time period");
        }
        
        ProjectHistory projectHistory = new ProjectHistory();
        projectHistory.setEmployee(employee);
        projectHistory.setProjectName(request.getProjectName());
        projectHistory.setClientName(request.getClientName());
        projectHistory.setDescription(request.getDescription());
        projectHistory.setRole(request.getRole());
        projectHistory.setStartDate(request.getStartDate());
        projectHistory.setEndDate(request.getEndDate());
        
        projectHistory = projectHistoryRepository.save(projectHistory);
        return projectHistoryMapper.toDto(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:update:all', 'project-history:update:team')")
    @Transactional
    public ProjectHistoryDto updateProjectHistory(Long id, ProjectHistoryRequest request) {
        log.debug("Request to update project history: {}, request: {}", id, request);
        
        ProjectHistory projectHistory = findProjectHistoryById(id);
        
        // Check for overlapping projects (excluding this one)
        if (projectHistoryRepository.existsOverlappingProjectExcludingCurrent(
                projectHistory.getEmployee().getId(), request.getStartDate(), request.getEndDate(), id)) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has a project assigned during this time period");
        }
        
        projectHistory.setProjectName(request.getProjectName());
        projectHistory.setClientName(request.getClientName());
        projectHistory.setDescription(request.getDescription());
        projectHistory.setRole(request.getRole());
        projectHistory.setStartDate(request.getStartDate());
        projectHistory.setEndDate(request.getEndDate());
        
        projectHistory = projectHistoryRepository.save(projectHistory);
        return projectHistoryMapper.toDto(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:delete:all', 'project-history:delete:team')")
    @Transactional
    public void deleteProjectHistory(Long id) {
        log.debug("Request to delete project history: {}", id);
        
        ProjectHistory projectHistory = findProjectHistoryById(id);
        projectHistoryRepository.delete(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public List<ProjectHistory> getProjectHistoryByEmployeeId(Long employeeId) {
        log.debug("Request to get project history by employeeId: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        return projectHistoryRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public Page<ProjectHistory> getProjectHistoryByEmployeeId(Long employeeId, Pageable pageable) {
        log.debug("Request to get project history by employeeId with pagination: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        return projectHistoryRepository.findByEmployeeIdOrderByStartDateDesc(employeeId, pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public ProjectHistory getProjectHistoryById(Long id) {
        log.debug("Request to get project history by id: {}", id);
        return findProjectHistoryById(id);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    @Transactional
    public ProjectHistory createProjectHistory(Long employeeId, ProjectHistory projectHistory) {
        log.debug("Request to create project history for employee: {}, projectHistory: {}", employeeId, projectHistory);
        
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check for overlapping projects
        if (projectHistoryRepository.existsOverlappingProject(
                employeeId, projectHistory.getStartDate(), projectHistory.getEndDate())) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has a project assigned during this time period");
        }
        
        projectHistory.setEmployee(employee);
        return projectHistoryRepository.save(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:create:all', 'project-history:create:team')")
    @Transactional
    public ProjectHistory addProjectHistoryToEmployee(Long employeeId, ProjectHistoryRequest request) {
        log.debug("Request to add project history to employee: {}, request: {}", employeeId, request);
        
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Check for overlapping projects
        if (projectHistoryRepository.existsOverlappingProject(
                employeeId, request.getStartDate(), request.getEndDate())) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has a project assigned during this time period");
        }
        
        ProjectHistory projectHistory = new ProjectHistory();
        projectHistory.setEmployee(employee);
        projectHistory.setProjectName(request.getProjectName());
        projectHistory.setClientName(request.getClientName());
        projectHistory.setDescription(request.getDescription());
        projectHistory.setRole(request.getRole());
        projectHistory.setStartDate(request.getStartDate());
        projectHistory.setEndDate(request.getEndDate());
        
        return projectHistoryRepository.save(projectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    @Transactional
    public ProjectHistory updateProjectHistory(Long id, ProjectHistory projectHistory) {
        log.debug("Request to update project history: {}, projectHistory: {}", id, projectHistory);
        
        ProjectHistory existingProjectHistory = findProjectHistoryById(id);
        
        // Check for overlapping projects (excluding this one)
        if (projectHistoryRepository.existsOverlappingProjectExcludingCurrent(
                existingProjectHistory.getEmployee().getId(), 
                projectHistory.getStartDate(), 
                projectHistory.getEndDate(), 
                id)) {
            throw new ResourceAlreadyExistsException(
                    "Employee already has a project assigned during this time period");
        }
        
        // Update fields
        existingProjectHistory.setProjectName(projectHistory.getProjectName());
        existingProjectHistory.setClientName(projectHistory.getClientName());
        existingProjectHistory.setDescription(projectHistory.getDescription());
        existingProjectHistory.setRole(projectHistory.getRole());
        existingProjectHistory.setStartDate(projectHistory.getStartDate());
        existingProjectHistory.setEndDate(projectHistory.getEndDate());
        
        return projectHistoryRepository.save(existingProjectHistory);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistory> findProjectHistoryByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        log.debug("Request to find project history by date range: {} to {} for employee {}", startDate, endDate, employeeId);
        return projectHistoryRepository.findOverlappingWithDateRange(employeeId, startDate, endDate);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistory> findProjectHistoryByProjectName(String projectName) {
        log.debug("Request to find project history by project name: {}", projectName);
        return projectHistoryRepository.findByProjectNameContainingIgnoreCase(projectName);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team')")
    public List<ProjectHistory> findProjectHistoryByClientName(String clientName) {
        log.debug("Request to find project history by client name: {}", clientName);
        return projectHistoryRepository.findByClientNameContainingIgnoreCase(clientName);
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('project-history:read:all', 'project-history:read:team', 'project-history:read:own')")
    public List<ProjectHistory> findActiveProjectHistoryByEmployeeId(Long employeeId) {
        log.debug("Request to find active project history by employee id: {}", employeeId);
        
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        
        return projectHistoryRepository.findByEmployeeIdAndEndDateIsNull(employeeId);
    }
    
    /**
     * Helper method to find project history by ID
     * 
     * @param id the project history ID
     * @return the project history
     * @throws ResourceNotFoundException if not found
     */
    private ProjectHistory findProjectHistoryById(Long id) {
        return projectHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project history not found with id: " + id));
    }
} 
