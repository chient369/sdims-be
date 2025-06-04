package com.company.internalmgmt.modules.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.company.internalmgmt.modules.dashboard.dto.DashboardSummaryDTO;

/**
 * Service interface for dashboard operations
 */
public interface DashboardService {
    
    /**
     * Get dashboard summary data with widgets
     * 
     * @param fromDate Start date for data range
     * @param toDate End date for data range
     * @param teamId Team ID to filter data
     * @param widgets List of widgets to fetch data for
     * @param currentUserId Current user ID
     * @param authentication Authentication object
     * @return Dashboard summary data
     */
    DashboardSummaryDTO getDashboardSummary(
        LocalDate fromDate, 
        LocalDate toDate, 
        Long teamId, 
        List<String> widgets, 
        Long currentUserId, 
        Authentication authentication
    );
} 