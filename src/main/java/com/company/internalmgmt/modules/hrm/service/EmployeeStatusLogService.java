package com.company.internalmgmt.modules.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;

/**
 * Service interface for managing employee status logs
 */
public interface EmployeeStatusLogService {
    
    /**
     * Find status logs for a specific employee
     * 
     * @param employeeId the employee ID
     * @param pageable pagination info
     * @return page of status log DTOs
     */
    Page<EmployeeStatusLogDto> findByEmployeeId(Long employeeId, Pageable pageable);
    
    /**
     * Find all status logs for a specific employee
     * 
     * @param employeeId the employee ID
     * @return list of status log DTOs
     */
    List<EmployeeStatusLogDto> findAllByEmployeeId(Long employeeId);
    
    /**
     * Find the most recent status log for an employee
     * 
     * @param employeeId the employee ID
     * @return the most recent status log DTO
     */
    EmployeeStatusLogDto findMostRecentByEmployeeId(Long employeeId);
    
    /**
     * Find status logs for employees with a specific status
     * 
     * @param status the status
     * @return list of status log DTOs
     */
    List<EmployeeStatusLogDto> findByStatus(String status);
    
    /**
     * Find status logs created within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of status log DTOs
     */
    List<EmployeeStatusLogDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find status logs for projects ending soon
     * 
     * @param endDate the end date threshold
     * @return list of status log DTOs
     */
    List<EmployeeStatusLogDto> findProjectsEndingSoon(LocalDate endDate);
    
    /**
     * Create a status log for an employee
     * 
     * @param employeeId the employee ID
     * @param request the status update request
     * @return the created status log DTO
     */
    EmployeeStatusLogDto createStatusLog(Long employeeId, StatusUpdateRequest request);
    
    /**
     * Create a status log for an employee
     * 
     * @param employee the employee
     * @param request the status update request
     * @return the created status log DTO
     */
    EmployeeStatusLogDto createStatusLog(Employee employee, StatusUpdateRequest request);
    
    /**
     * Update an employee's current status
     * 
     * @param employeeId the employee ID
     * @param request the status update request
     * @return the updated employee
     */
    Employee updateEmployeeStatus(Long employeeId, StatusUpdateRequest request);
    
    /**
     * Update an existing employee status log
     * 
     * @param id the status log ID
     * @param employeeStatusLogDto the updated status log data
     * @return the updated status log DTO
     */
    EmployeeStatusLogDto updateEmployeeStatusLog(Long id, EmployeeStatusLogDto employeeStatusLogDto);
    
    /**
     * Get employee status log by ID
     * 
     * @param id the status log ID
     * @return the status log DTO
     */
    EmployeeStatusLogDto getEmployeeStatusLogById(Long id);
    
    /**
     * Delete an employee status log
     * 
     * @param id the status log ID
     */
    void deleteEmployeeStatusLog(Long id);
} 