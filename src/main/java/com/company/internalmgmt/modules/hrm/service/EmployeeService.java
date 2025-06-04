package com.company.internalmgmt.modules.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeRequest;
import com.company.internalmgmt.modules.hrm.dto.EmployeeResponse;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service interface for managing employees
 */
public interface EmployeeService {
    
    /**
     * Find all employees with filtering options
     * 
     * @param keyword search term for name or employee code
     * @param teamId filter by team ID
     * @param position filter by position
     * @param status filter by status
     * @param skills filter by skills
     * @param minExperience minimum years of experience
     * @param marginStatus filter by margin status
     * @param pageable pagination info
     * @return page of employee DTOs
     */
    Page<EmployeeDto> findEmployees(
            String keyword, 
            Integer teamId, 
            String position, 
            String status, 
            List<Integer> skills, 
            Integer minExperience, 
            String marginStatus, 
            Pageable pageable);
    
    /**
     * Legacy method for backward compatibility
     */
    default Page<EmployeeDto> findEmployees(String search, String team, String status, Pageable pageable) {
        return findEmployees(search, team != null ? Integer.parseInt(team) : null, null, status, null, null, null, pageable);
    }
    
    /**
     * Find an employee by ID
     * 
     * @param id the employee ID
     * @return the employee DTO
     */
    EmployeeDto findById(Long id);
    
    /**
     * Find an employee by employee code
     * 
     * @param employeeCode the employee code
     * @return the employee DTO
     */
    EmployeeDto findByEmployeeCode(String employeeCode);
    
    /**
     * Create a new employee
     * 
     * @param request the employee request
     * @return the created employee DTO
     */
    EmployeeDto create(EmployeeRequest request);
    
    /**
     * Update an employee
     * 
     * @param id the employee ID
     * @param request the employee request
     * @return the updated employee DTO
     */
    EmployeeDto update(Long id, EmployeeRequest request);
    
    /**
     * Delete an employee
     * 
     * @param id the employee ID
     */
    void delete(Long id);
    
    /**
     * Update an employee's status
     * 
     * @param id the employee ID
     * @param request the status update request
     * @return the updated employee
     */
    Employee updateStatus(Long id, StatusUpdateRequest request);
    
    /**
     * Find employees with specific skills
     * 
     * @param skillIds list of skill IDs
     * @return list of employees
     */
    List<Employee> findEmployeesWithSkills(List<Long> skillIds);
    
    /**
     * Suggest employees based on skills and experience
     * 
     * @param skillIds list of skill IDs
     * @param minExperience minimum years of experience
     * @return list of employee DTOs
     */
    List<EmployeeDto> suggestEmployeesBySkills(List<Long> skillIds, Double minExperience);
    
    /**
     * Find employees by status
     * 
     * @param status the status
     * @return list of employees
     */
    List<Employee> findByStatus(String status);
    
    /**
     * Find employees ending soon
     * 
     * @return list of employees
     */
    List<Employee> findEmployeesEndingSoon();
    
    /**
     * Find available employees
     * 
     * @return list of available employees
     */
    List<Employee> findAvailableEmployees();
    
    /**
     * Import employees from Excel file
     * 
     * @param fileData the file data
     * @return list of imported employees
     */
    List<Employee> importEmployees(byte[] fileData);
    
    /**
     * Export employees to Excel file
     * 
     * @param filters filters to apply
     * @return the Excel file as byte array
     */
    byte[] exportEmployees(String filters);
    
    /**
     * Find an employee entity by ID
     * 
     * @param id the employee ID
     * @return the employee entity
     * @throws ResourceNotFoundException if not found
     */
    Employee findEmployeeById(Long id);
} 
