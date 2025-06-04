package com.company.internalmgmt.modules.hrm.controller;

import com.company.internalmgmt.common.dto.PageResponseDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.mapper.EmployeeStatusLogMapper;
import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import com.company.internalmgmt.modules.hrm.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Employee Status Log management
 */
@RestController
@RequestMapping("/api/v1/status-logs")
@RequiredArgsConstructor
@Slf4j
public class StatusController {

    private final StatusService statusService;
    private final EmployeeStatusLogMapper statusLogMapper;

    /**
     * Get status logs by employee ID
     *
     * @param employeeId the employee ID
     * @return list of status logs
     */
    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    public ResponseEntity<List<EmployeeStatusLogDto>> getStatusLogsByEmployeeId(
            @PathVariable Long employeeId) {
        
        log.info("Getting status logs for employee with ID: {}", employeeId);
        
        List<EmployeeStatusLog> statusLogs = statusService.getStatusLogsByEmployeeId(employeeId);
        List<EmployeeStatusLogDto> logDtos = statusLogs.stream()
                .map(statusLogMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(logDtos);
    }

    /**
     * Get status logs by employee ID with pagination
     *
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return page of status logs
     */
    @GetMapping("/employees/{employeeId}/paged")
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    public ResponseEntity<PageResponseDto<EmployeeStatusLogDto>> getStatusLogsByEmployeeIdPaged(
            @PathVariable Long employeeId,
            Pageable pageable) {
        
        log.info("Getting paged status logs for employee with ID: {}", employeeId);
        
        Page<EmployeeStatusLog> statusLogsPage = statusService.getStatusLogsByEmployeeId(employeeId, pageable);
        
        List<EmployeeStatusLogDto> logDtos = statusLogsPage.getContent().stream()
                .map(statusLogMapper::toDto)
                .collect(Collectors.toList());
        
        PageResponseDto<EmployeeStatusLogDto> response = PageResponseDto.success(
                logDtos,
                statusLogsPage.getNumber(),
                statusLogsPage.getSize(),
                statusLogsPage.getTotalElements(),
                statusLogsPage.getTotalPages(),
                statusLogsPage.getSort().toString()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get status log by ID
     *
     * @param id the status log ID
     * @return the status log
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    public ResponseEntity<EmployeeStatusLogDto> getStatusLogById(@PathVariable Long id) {
        log.info("Getting status log by ID: {}", id);
        
        EmployeeStatusLog statusLog = statusService.getStatusLogById(id);
        EmployeeStatusLogDto logDto = statusLogMapper.toDto(statusLog);
        
        return ResponseEntity.ok(logDto);
    }
} 
