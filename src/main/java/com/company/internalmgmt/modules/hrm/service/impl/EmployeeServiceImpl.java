package com.company.internalmgmt.modules.hrm.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.company.internalmgmt.common.exception.ResourceAlreadyExistsException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeRequest;
import com.company.internalmgmt.modules.hrm.dto.EmployeeResponse;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.EmployeeMapper;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.EmployeeSkillRepository;
import com.company.internalmgmt.modules.hrm.repository.EmployeeStatusLogRepository;
import com.company.internalmgmt.modules.hrm.repository.specification.EmployeeSpecifications;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of EmployeeService
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeStatusLogRepository employeeStatusLogRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeStatusLogService employeeStatusLogService;
    
    @Override
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    public Page<EmployeeDto> findEmployees(
            String keyword, 
            Integer teamId, 
            String position, 
            String status, 
            List<Integer> skills, 
            Integer minExperience, 
            String marginStatus, 
            Pageable pageable) {
        
        log.debug("Finding employees with filters: keyword={}, teamId={}, position={}, status={}, skills={}, minExperience={}, marginStatus={}",
                keyword, teamId, position, status, skills, minExperience, marginStatus);
        
        Specification<Employee> spec = Specification.where(null);
        
        // Filter by keyword (search in name, employee code)
        if (StringUtils.hasText(keyword)) {
            spec = spec.and(EmployeeSpecifications.nameContains(keyword));
        }
        
        // Filter by team
        if (teamId != null) {
            spec = spec.and(EmployeeSpecifications.inTeam(teamId.longValue()));
        }
        
        // Filter by position
        if (StringUtils.hasText(position)) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("position")), "%" + position.toLowerCase() + "%"));
        }
        
        // Filter by status
        if (StringUtils.hasText(status)) {
            spec = spec.and(EmployeeSpecifications.statusEquals(status));
        }
        
        // Filter by skills
        if (skills != null && !skills.isEmpty()) {
            // Convert Integer list to Long list
            List<Long> skillIdsLong = skills.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            
            // Get employees with specified skills
            List<Long> employeeIds = findEmployeesWithSkills(skillIdsLong).stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList());
            
            if (employeeIds.isEmpty()) {
                // No employees match the skills filter, return empty result
                return Page.empty(pageable);
            }
            
            // Add employee IDs to specification
            spec = spec.and((root, query, cb) -> root.get("id").in(employeeIds));
        }
        
        // Filter by margin status (would be implemented when margin data is available)
        if (StringUtils.hasText(marginStatus)) {
            // This is a placeholder - actual implementation would depend on how margin status is stored
            log.debug("Filtering by margin status {} is not fully implemented yet", marginStatus);
        }
        
        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        return employeePage.map(employeeMapper::toDto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    public Page<EmployeeDto> findEmployees(String search, String team, String status, Pageable pageable) {
        Specification<Employee> spec = Specification.where(null);
        
        if (StringUtils.hasText(search)) {
            spec = spec.and(EmployeeSpecifications.nameContains(search));
        }
        
        if (StringUtils.hasText(team)) {
            spec = spec.and(EmployeeSpecifications.teamEquals(team));
        }
        
        if (StringUtils.hasText(status)) {
            spec = spec.and(EmployeeSpecifications.statusEquals(status));
        }
        
        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        return employeePage.map(employeeMapper::toDto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team', 'employee:read:own')")
    public EmployeeDto findById(Long id) {
        Employee employee = findEmployeeById(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team', 'employee:read:own')")
    public EmployeeDto findByEmployeeCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + employeeCode));
        return employeeMapper.toDto(employee);
    }

    @Override
    @PreAuthorize("hasAuthority('employee:create')")
    @Transactional
    public EmployeeDto create(EmployeeRequest request) {
        // Check if employee code is already in use
        if (employeeRepository.findByEmployeeCode(request.getEmployeeCode()).isPresent()) {
            throw new ResourceAlreadyExistsException("Employee code already in use: " + request.getEmployeeCode());
        }
        
        // Check if company email is already in use
        if (employeeRepository.findByCompanyEmail(request.getCompanyEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Company email already in use: " + request.getCompanyEmail());
        }
        
        Employee employee = employeeMapper.toEntity(request);
        employee.setCurrentStatus("Available");
        employee.setStatusUpdatedAt(LocalDateTime.now());
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Create initial status log
        EmployeeStatusLog statusLog = new EmployeeStatusLog();
        statusLog.setEmployee(savedEmployee);
        statusLog.setStatus("Available");
        statusLog.setLogTimestamp(LocalDateTime.now());
        employeeStatusLogRepository.save(statusLog);
        
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee:update:all', 'employee:update:team', 'employee:update:own')")
    @Transactional
    public EmployeeDto update(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeById(id);
        
        // Check if employee code is already in use by another employee
        Optional<Employee> existingWithCode = employeeRepository.findByEmployeeCode(request.getEmployeeCode());
        if (existingWithCode.isPresent() && !existingWithCode.get().getId().equals(id)) {
            throw new ResourceAlreadyExistsException("Employee code already in use: " + request.getEmployeeCode());
        }
        
        // Check if company email is already in use by another employee
        Optional<Employee> existingWithEmail = employeeRepository.findByCompanyEmail(request.getCompanyEmail());
        if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(id)) {
            throw new ResourceAlreadyExistsException("Company email already in use: " + request.getCompanyEmail());
        }
        
        employeeMapper.updateEntityFromRequest(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @PreAuthorize("hasAuthority('employee:delete')")
    @Transactional
    public void delete(Long id) {
        Employee employee = findEmployeeById(id);
        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    @Transactional
    public Employee updateStatus(Long id, StatusUpdateRequest request) {
        Employee employee = findEmployeeById(id);
        
        // Update employee status
        employee.setCurrentStatus(request.getStatus());
        employee.setStatusUpdatedAt(LocalDateTime.now());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        
        // Create status log
        employeeStatusLogService.createStatusLog(updatedEmployee, request);
        
        return updatedEmployee;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee-suggest:read', 'employee:read:all', 'employee:read:team')")
    public List<Employee> findEmployeesWithSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EmployeeSkill> employeeSkills = employeeSkillRepository.findBySkillIdIn(skillIds);
        
        // Group by employee and count skills
        return employeeSkills.stream()
                .collect(Collectors.groupingBy(EmployeeSkill::getEmployee, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == skillIds.size()) // Employee has all required skills
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAuthority('employee-suggest:read')")
    public List<EmployeeDto> suggestEmployeesBySkills(List<Long> skillIds, Double minExperience) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EmployeeSkill> employeeSkills;
        
        if (minExperience != null) {
            employeeSkills = employeeSkillRepository.findBySkillIdInAndMinExperience(skillIds, minExperience);
        } else {
            employeeSkills = employeeSkillRepository.findBySkillIdIn(skillIds);
        }
        
        // Group by employee and count skills
        return employeeSkills.stream()
                .collect(Collectors.groupingBy(EmployeeSkill::getEmployee, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == skillIds.size()) // Employee has all required skills
                .map(entry -> employeeMapper.toDto(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team')")
    public List<Employee> findByStatus(String status) {
        return employeeRepository.findByCurrentStatusEqualsIgnoreCase(status, Pageable.unpaged()).getContent();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee-alert:read:all', 'employee-alert:read:team')")
    public List<Employee> findEmployeesEndingSoon() {
        LocalDate thresholdDate = LocalDate.now().plusMonths(1); // Consider ending soon if within 1 month
        return employeeRepository.findEmployeesEndingSoon(thresholdDate, Pageable.unpaged()).getContent();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team')")
    public List<Employee> findAvailableEmployees() {
        return employeeRepository.findByCurrentStatusEqualsIgnoreCase("Available", Pageable.unpaged()).getContent();
    }

    @Override
    @PreAuthorize("hasAuthority('employee:import')")
    @Transactional
    public List<Employee> importEmployees(byte[] fileData) {
        // List<Employee> importedEmployees = new ArrayList<>();
        
        // try (Workbook workbook = WorkbookFactory.create(fileData)) {
        //     // Implementation of Excel file parsing would go here
        //     // This is a simplified version
            
        //     log.info("Successfully imported {} employees", importedEmployees.size());
        //     return importedEmployees;
        // } catch (Exception e) {
        //     log.error("Error importing employees", e);
        //     throw new RuntimeException("Failed to import employees: " + e.getMessage());
        // }
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('employee:export')")
    public byte[] exportEmployees(String filters) {
        // try (Workbook workbook = new XSSFWorkbook()) {
        //     // Implementation of Excel file creation would go here
        //     // This is a simplified version
            
        //     return new byte[0]; // Placeholder
        // } catch (Exception e) {
        //     log.error("Error exporting employees", e);
        //     throw new RuntimeException("Failed to export employees: " + e.getMessage());
        // }
        return null;
    }
    
    /**
     * Helper method to find an employee by ID
     * 
     * @param id the employee ID
     * @return the employee
     * @throws ResourceNotFoundException if not found
     */
    @Override
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }
} 
