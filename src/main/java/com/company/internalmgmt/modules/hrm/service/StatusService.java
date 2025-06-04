package com.company.internalmgmt.modules.hrm.service;

import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Employee Status management
 */
public interface StatusService {

    /**
     * Get status logs by employee ID
     * @param employeeId the employee ID
     * @return List of status logs for the specified employee
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    List<EmployeeStatusLog> getStatusLogsByEmployeeId(Long employeeId);

    /**
     * Get status logs by employee ID with pagination
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return Page of status logs for the specified employee
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    Page<EmployeeStatusLog> getStatusLogsByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Get status log by ID
     * @param id the status log ID
     * @return the status log
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    EmployeeStatusLog getStatusLogById(Long id);

    /**
     * Create a new status log
     * @param employeeId the employee ID
     * @param status the status
     * @param projectName the project name (if applicable)
     * @param allocationPercentage the allocation percentage (if applicable)
     * @param expectedEndDate the expected end date (if applicable)
     * @return the created status log
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if employee not found
     */
    @PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    EmployeeStatusLog createStatusLog(Long employeeId, String status, String projectName, 
                                     Double allocationPercentage, LocalDate expectedEndDate);

    /**
     * Get the latest status log for an employee
     * @param employeeId the employee ID
     * @return the latest status log
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    EmployeeStatusLog getLatestStatusLog(Long employeeId);

    /**
     * Find employees with 'EndingSoon' status and expected end date before the specified date
     * @param date the date to compare against
     * @return List of status logs with 'EndingSoon' status and expected end date before the specified date
     */
    @PreAuthorize("hasAnyAuthority('employee-alert:read:all', 'employee-alert:read:team')")
    List<EmployeeStatusLog> findEndingSoonBeforeDate(LocalDate date);

    /**
     * Find status logs by project name
     * @param projectName the project name
     * @return List of status logs for the specified project
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team')")
    List<EmployeeStatusLog> findStatusLogsByProjectName(String projectName);

    /**
     * Find status logs created between two dates
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of status logs
     */
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team')")
    List<EmployeeStatusLog> findStatusLogsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
} 
