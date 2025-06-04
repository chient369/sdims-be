package com.company.internalmgmt.modules.dashboard.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.util.SecurityUtils;
import com.company.internalmgmt.modules.dashboard.dto.employee.EmployeeReportDTO;
import com.company.internalmgmt.modules.dashboard.dto.margin.MarginReportDTO;
import com.company.internalmgmt.modules.dashboard.service.ReportService;

/**
 * REST controller for report operations
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    /**
     * GET /api/v1/reports/employee-list : Get detailed employee list report
     * API-RPT-002
     */
    @GetMapping("/employee-list")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:team', 'report:read:own')")
    public ResponseEntity<ApiResponse<EmployeeReportDTO>> getEmployeeListReport(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<Integer> skills,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String utilization,
            @RequestParam(required = false, defaultValue = "true") Boolean includeSkills,
            @RequestParam(required = false, defaultValue = "true") Boolean includeProjects,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        EmployeeReportDTO result = reportService.getEmployeeListReport(
            teamId, position, status, skills, minExperience, projectId, 
            utilization, includeSkills, includeProjects, exportType, 
            pageable, currentUserId, authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/reports/margin-detail : Get detailed margin report by employee/team
     * API-RPT-003
     */
    @GetMapping("/margin-detail")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:team')")
    public ResponseEntity<ApiResponse<MarginReportDTO>> getMarginDetailReport(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false, defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String marginThreshold,
            @RequestParam(required = false, defaultValue = "employee") String groupBy,
            @RequestParam(required = false, defaultValue = "true") Boolean includeDetails,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "margin") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Set default date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusYears(1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        MarginReportDTO result = reportService.getMarginDetailReport(
            teamId, employeeId, period, fromDate, toDate, marginThreshold, 
            groupBy, includeDetails, exportType, pageable, currentUserId, 
            authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/reports/opportunity-list : Get detailed opportunity list report
     * API-RPT-004
     */
    @GetMapping("/opportunity-list")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:team', 'report:read:own')")
    public ResponseEntity<ApiResponse<Object>> getOpportunityListReport(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer salesId,
            @RequestParam(required = false) Integer leaderId,
            @RequestParam(required = false) String dealStage,
            @RequestParam(required = false) String followUpStatus,
            @RequestParam(required = false) Boolean onsite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") Boolean includeNotes,
            @RequestParam(required = false, defaultValue = "true") Boolean includeLeaders,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "lastInteractionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Set default date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusYears(1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        Object result = reportService.getOpportunityListReport(
            customerId, salesId, leaderId, dealStage, followUpStatus, onsite,
            fromDate, toDate, keyword, includeNotes, includeLeaders, exportType,
            pageable, currentUserId, authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/reports/contract-list : Get detailed contract list report
     * API-RPT-005
     */
    @GetMapping("/contract-list")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:team', 'report:read:own')")
    public ResponseEntity<ApiResponse<Object>> getContractListReport(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer salesId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer opportunityId,
            @RequestParam(required = false) Double minValue,
            @RequestParam(required = false) Double maxValue,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryFromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryToDate,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "true") Boolean includePayments,
            @RequestParam(required = false, defaultValue = "true") Boolean includeEmployees,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "signedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Set default date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusYears(1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        Object result = reportService.getContractListReport(
            customerId, salesId, status, type, opportunityId, minValue, maxValue,
            fromDate, toDate, expiryFromDate, expiryToDate, paymentStatus, keyword,
            includePayments, includeEmployees, exportType, pageable, currentUserId,
            authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/reports/payment-status : Get detailed payment status report
     * API-RPT-006
     */
    @GetMapping("/payment-status")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:team', 'report:read:own')")
    public ResponseEntity<ApiResponse<Object>> getPaymentStatusReport(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer salesId,
            @RequestParam(required = false) Integer contractId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidFromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidToDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false, defaultValue = "true") Boolean includeDetails,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Set default date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().minusMonths(6);
        }
        if (toDate == null) {
            toDate = LocalDate.now().plusMonths(6);
        }
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        Object result = reportService.getPaymentStatusReport(
            customerId, salesId, contractId, status, fromDate, toDate,
            paidFromDate, paidToDate, minAmount, maxAmount, includeDetails,
            exportType, pageable, currentUserId, authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/reports/kpi-progress : Get sales KPI progress report
     * API-RPT-007
     */
    @GetMapping("/kpi-progress")
    @PreAuthorize("hasAnyAuthority('report:read:all', 'report:read:own')")
    public ResponseEntity<ApiResponse<Object>> getKpiProgressReport(
            @RequestParam(required = false) Integer salesId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Double minAchievement,
            @RequestParam(required = false) Double maxAchievement,
            @RequestParam(required = false, defaultValue = "true") Boolean includeDetails,
            @RequestParam(required = false, defaultValue = "json") String exportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "achievementPercentage") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication,
            HttpServletResponse response) {
        
        // Set default year if not provided
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        
        // Convert from 1-based page to 0-based page
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        Object result = reportService.getKpiProgressReport(
            salesId, year, quarter, month, minAchievement, maxAchievement,
            includeDetails, exportType, pageable, currentUserId, authentication, response
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
} 