package com.company.internalmgmt.modules.dashboard.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.company.internalmgmt.modules.dashboard.dto.DashboardSummaryDTO;
import com.company.internalmgmt.modules.dashboard.service.DashboardService;

/**
 * REST controller for dashboard operations
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    /**
     * GET /api/v1/dashboard/summary : Get dashboard summary data
     * 
     * @param fromDate Start date for data range (yyyy-MM-dd, default: beginning of current month)
     * @param toDate End date for data range (yyyy-MM-dd, default: current date)
     * @param teamId Team ID to filter data
     * @param widgets List of widgets to fetch data for
     * @param authentication Authentication object
     * @return ResponseEntity with dashboard summary data
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('dashboard:read:all', 'dashboard:read:team', 'dashboard:read:own')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) List<String> widgets,
            Authentication authentication) {
        
        // Set default date range if not provided
        if (fromDate == null) {
            fromDate = LocalDate.now().withDayOfMonth(1); // Beginning of current month
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        
        // Set default widgets if not provided
        if (widgets == null || widgets.isEmpty()) {
            widgets = Arrays.asList(
                "opportunity_status", 
                "margin_distribution", 
                "revenue_summary", 
                "employee_status", 
                "utilization_rate"
            );
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(
            fromDate, toDate, teamId, widgets, currentUserId, authentication
        );
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
} 