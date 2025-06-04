package com.company.internalmgmt.modules.hrm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.company.internalmgmt.common.dto.PageResponseDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * REST controller for managing employee status logs
 */
@RestController
@RequestMapping("/api/v1/employee-status-logs")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Employee Status Log", description = "API for managing employee status logs")
public class EmployeeStatusLogController {

    private final EmployeeStatusLogService employeeStatusLogService;

    /**
     * GET /api/v1/employees/{employeeId}/status-logs : Get status logs for a specific employee
     *
     * @param employeeId the ID of the employee
     * @param pageable pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of status logs in body
     */
    @GetMapping("/employees/{employeeId}/status-logs")
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    @Operation(summary = "Get status logs for a specific employee", description = "Returns a paginated list of status logs for the specified employee")
    public ResponseEntity<PageResponseDto<EmployeeStatusLogDto>> getEmployeeStatusLogs(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId,
            Pageable pageable) {
        
        log.debug("REST request to get status logs for Employee : {}", employeeId);
        Page<EmployeeStatusLogDto> page = employeeStatusLogService.findByEmployeeId(employeeId, pageable);
        PageResponseDto<EmployeeStatusLogDto> response = PageResponseDto.success(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSort().toString()
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/employees/{employeeId}/status-logs/latest : Get most recent status log for an employee
     *
     * @param employeeId the ID of the employee
     * @return the ResponseEntity with status 200 (OK) and the most recent status log in body
     */
    @GetMapping("/employees/{employeeId}/status-logs/latest")
    @PreAuthorize("hasAnyAuthority('employee-status:read:all', 'employee-status:read:team', 'employee-status:read:own')")
    @Operation(summary = "Get most recent status log for an employee", description = "Returns the most recent status log for the specified employee")
    public ResponseEntity<EmployeeStatusLogDto> getLatestEmployeeStatusLog(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId) {
        
        log.debug("REST request to get most recent status log for Employee : {}", employeeId);
        EmployeeStatusLogDto statusLog = employeeStatusLogService.findMostRecentByEmployeeId(employeeId);
        return ResponseEntity.ok(statusLog);
    }
    
    /**
     * GET /api/v1/status-logs/status : Get status logs by status
     *
     * @param status the status to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of status logs in body
     */
    @GetMapping("/status-logs/status")
    @PreAuthorize("hasAuthority('employee-status:read:all')")
    @Operation(summary = "Get status logs by status", description = "Returns a list of status logs with the specified status")
    public ResponseEntity<List<EmployeeStatusLogDto>> getStatusLogsByStatus(
            @Parameter(description = "Status to filter by", required = true)
            @RequestParam String status) {
        
        log.debug("REST request to get status logs for status : {}", status);
        List<EmployeeStatusLogDto> statusLogs = employeeStatusLogService.findByStatus(status);
        return ResponseEntity.ok(statusLogs);
    }
    
    /**
     * GET /api/v1/status-logs/date-range : Get status logs created within a date range
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the ResponseEntity with status 200 (OK) and the list of status logs in body
     */
    @GetMapping("/status-logs/date-range")
    @PreAuthorize("hasAuthority('employee-status:read:all')")
    @Operation(summary = "Get status logs by date range", description = "Returns a list of status logs created within the specified date range")
    public ResponseEntity<List<EmployeeStatusLogDto>> getStatusLogsByDateRange(
            @Parameter(description = "Start date (inclusive)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (inclusive)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.debug("REST request to get status logs between {} and {}", startDate, endDate);
        List<EmployeeStatusLogDto> statusLogs = employeeStatusLogService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(statusLogs);
    }
    
    /**
     * GET /api/v1/status-logs/ending-soon : Get status logs for projects ending soon
     *
     * @param endDate the end date threshold
     * @return the ResponseEntity with status 200 (OK) and the list of status logs in body
     */
    @GetMapping("/status-logs/ending-soon")
    @PreAuthorize("hasAnyAuthority('employee-alert:read:all', 'employee-alert:read:team')")
    @Operation(summary = "Get status logs for projects ending soon", description = "Returns a list of status logs for projects ending before the specified date")
    public ResponseEntity<List<EmployeeStatusLogDto>> getProjectsEndingSoon(
            @Parameter(description = "End date threshold", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.debug("REST request to get projects ending before : {}", endDate);
        List<EmployeeStatusLogDto> statusLogs = employeeStatusLogService.findProjectsEndingSoon(endDate);
        return ResponseEntity.ok(statusLogs);
    }
    
    /**
     * POST /api/v1/employees/{employeeId}/status : Update employee status
     *
     * @param employeeId the ID of the employee
     * @param request the status update request
     * @return the ResponseEntity with status 201 (Created) and the new status log in body
     */
    @PostMapping("/employees/{employeeId}/status")
    @PreAuthorize("hasAnyAuthority('employee-status:update:all', 'employee-status:update:team')")
    @Operation(summary = "Update employee status", description = "Updates an employee's status and creates a new status log entry")
    public ResponseEntity<EmployeeStatusLogDto> updateEmployeeStatus(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId,
            @Parameter(description = "Status update request", required = true)
            @Valid @RequestBody StatusUpdateRequest request) {
        
        log.debug("REST request to update status for Employee : {}", employeeId);
        employeeStatusLogService.updateEmployeeStatus(employeeId, request);
        EmployeeStatusLogDto statusLog = employeeStatusLogService.createStatusLog(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusLog);
    }
} 