package com.company.internalmgmt.modules.hrm.service.impl;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.EmployeeStatusLogRepository;
import com.company.internalmgmt.modules.hrm.service.StatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of StatusService
 */
@Service
public class StatusServiceImpl implements StatusService {

    private final EmployeeStatusLogRepository employeeStatusLogRepository;
    private final EmployeeRepository employeeRepository;

    public StatusServiceImpl(EmployeeStatusLogRepository employeeStatusLogRepository, EmployeeRepository employeeRepository) {
        this.employeeStatusLogRepository = employeeStatusLogRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeStatusLog> getStatusLogsByEmployeeId(Long employeeId) {
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        
        return employeeStatusLogRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Page<EmployeeStatusLog> getStatusLogsByEmployeeId(Long employeeId, Pageable pageable) {
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        
        return employeeStatusLogRepository.findByEmployeeId(employeeId, pageable);
    }

    @Override
    public EmployeeStatusLog getStatusLogById(Long id) {
        return employeeStatusLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status log not found with ID: " + id));
    }

    @Override
    @Transactional
    public EmployeeStatusLog createStatusLog(Long employeeId, String status, String projectName, 
                                           Double allocationPercentage, LocalDate expectedEndDate) {
        // Check if employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        
        // Create new status log
        EmployeeStatusLog statusLog = new EmployeeStatusLog();
        statusLog.setEmployee(employee);
        statusLog.setStatus(status);
        statusLog.setProjectName(projectName);
        
        // Convert Double to BigDecimal if not null
        if (allocationPercentage != null) {
            statusLog.setAllocationPercentage(Integer.valueOf(allocationPercentage.toString()));
        }
        
        statusLog.setExpectedEndDate(expectedEndDate);
        statusLog.setLogTimestamp(LocalDateTime.now());
        
        // Save status log
        EmployeeStatusLog savedStatusLog = employeeStatusLogRepository.save(statusLog);
        
        // Update employee current status
        employee.setCurrentStatus(status);
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);
        
        return savedStatusLog;
    }

    @Override
    public EmployeeStatusLog getLatestStatusLog(Long employeeId) {
        // Check if employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        
        List<EmployeeStatusLog> latestLogs = employeeStatusLogRepository.findLatestByEmployeeId(
                employeeId, PageRequest.of(0, 1));
        
        if (latestLogs.isEmpty()) {
            throw new ResourceNotFoundException("No status logs found for employee with ID: " + employeeId);
        }
        
        return latestLogs.get(0);
    }

    @Override
    public List<EmployeeStatusLog> findEndingSoonBeforeDate(LocalDate date) {
        return employeeStatusLogRepository.findEndingSoonBeforeDate(date);
    }

    @Override
    public List<EmployeeStatusLog> findStatusLogsByProjectName(String projectName) {
        return employeeStatusLogRepository.findByProjectNameContainingIgnoreCase(projectName);
    }

    @Override
    public List<EmployeeStatusLog> findStatusLogsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return employeeStatusLogRepository.findByLogTimestampBetween(startDate, endDate);
    }
} 
