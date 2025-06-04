package com.company.internalmgmt.modules.hrm.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.EmployeeStatusLogMapper;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.EmployeeStatusLogRepository;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the EmployeeStatusLogService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeStatusLogServiceImpl implements EmployeeStatusLogService {

    private final EmployeeStatusLogRepository employeeStatusLogRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeStatusLogMapper employeeStatusLogMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeStatusLogDto> findByEmployeeId(Long employeeId, Pageable pageable) {
        Page<EmployeeStatusLog> logs = employeeStatusLogRepository.findByEmployeeId(employeeId, pageable);
        List<EmployeeStatusLogDto> dtos = logs.getContent().stream()
                .map(employeeStatusLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, logs.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeStatusLogDto> findAllByEmployeeId(Long employeeId) {
        return employeeStatusLogRepository.findByEmployeeId(employeeId).stream()
                .map(employeeStatusLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public EmployeeStatusLogDto findMostRecentByEmployeeId(Long employeeId) {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "logTimestamp"));
        List<EmployeeStatusLog> logs = employeeStatusLogRepository.findMostRecentByEmployeeId(employeeId, pageRequest);
        
        return logs.isEmpty() ? null : employeeStatusLogMapper.toDto(logs.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeStatusLogDto> findByStatus(String status) {
        return employeeStatusLogRepository.findByStatus(status).stream()
                .map(employeeStatusLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeStatusLogDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return employeeStatusLogRepository.findByDateRange(startDate, endDate).stream()
                .map(employeeStatusLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeStatusLogDto> findProjectsEndingSoon(LocalDate endDate) {
        return employeeStatusLogRepository.findProjectsEndingSoon(endDate).stream()
                .map(employeeStatusLogMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EmployeeStatusLogDto createStatusLog(Long employeeId, StatusUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        return createStatusLog(employee, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EmployeeStatusLogDto createStatusLog(Employee employee, StatusUpdateRequest request) {
        EmployeeStatusLog statusLog = employeeStatusLogMapper.fromStatusUpdateRequest(request);
        statusLog.setEmployee(employee);
        
        EmployeeStatusLog savedLog = employeeStatusLogRepository.save(statusLog);
        log.info("Created status log for employee {}: {}", employee.getId(), request.getStatus());
        
        return employeeStatusLogMapper.toDto(savedLog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Employee updateEmployeeStatus(Long employeeId, StatusUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Update employee status
        employee.setCurrentStatus(request.getStatus());
        employee.setStatusUpdatedAt(LocalDateTime.now());
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Create status log
        createStatusLog(savedEmployee, request);
        
        log.info("Updated status for employee {}: {}", employeeId, request.getStatus());
        return savedEmployee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EmployeeStatusLogDto updateEmployeeStatusLog(Long id, EmployeeStatusLogDto employeeStatusLogDto) {
        EmployeeStatusLog employeeStatusLog = employeeStatusLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeStatusLog not found with id " + id));
        
        employeeStatusLogMapper.updateFromDto(employeeStatusLogDto, employeeStatusLog);
        employeeStatusLogRepository.save(employeeStatusLog);
        
        log.info("Updated employee status log with id: {}", id);
        return employeeStatusLogMapper.toDto(employeeStatusLog);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public EmployeeStatusLogDto getEmployeeStatusLogById(Long id) {
        EmployeeStatusLog employeeStatusLog = employeeStatusLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeStatusLog not found with id " + id));
        
        return employeeStatusLogMapper.toDto(employeeStatusLog);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteEmployeeStatusLog(Long id) {
        EmployeeStatusLog employeeStatusLog = employeeStatusLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeStatusLog not found with id " + id));
        
        employeeStatusLogRepository.delete(employeeStatusLog);
        log.info("Deleted employee status log with id: {}", id);
    }
} 