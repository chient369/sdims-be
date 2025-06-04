package com.company.internalmgmt.modules.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.company.internalmgmt.modules.dashboard.dto.employee.EmployeeReportDTO;
import com.company.internalmgmt.modules.dashboard.dto.margin.MarginReportDTO;

/**
 * Service interface for report operations
 */
public interface ReportService {
    
    /**
     * Get detailed employee list report
     */
    EmployeeReportDTO getEmployeeListReport(
        Integer teamId, String position, String status, List<Integer> skills,
        Integer minExperience, Integer projectId, String utilization,
        Boolean includeSkills, Boolean includeProjects, String exportType,
        Pageable pageable, Long currentUserId, Authentication authentication,
        HttpServletResponse response
    );
    
    /**
     * Get detailed margin report by employee/team
     */
    MarginReportDTO getMarginDetailReport(
        Integer teamId, Integer employeeId, String period, LocalDate fromDate,
        LocalDate toDate, String marginThreshold, String groupBy,
        Boolean includeDetails, String exportType, Pageable pageable,
        Long currentUserId, Authentication authentication, HttpServletResponse response
    );
    
    /**
     * Get detailed opportunity list report
     */
    Object getOpportunityListReport(
        Integer customerId, Integer salesId, Integer leaderId, String dealStage,
        String followUpStatus, Boolean onsite, LocalDate fromDate, LocalDate toDate,
        String keyword, Boolean includeNotes, Boolean includeLeaders, String exportType,
        Pageable pageable, Long currentUserId, Authentication authentication,
        HttpServletResponse response
    );
    
    /**
     * Get detailed contract list report
     */
    Object getContractListReport(
        Integer customerId, Integer salesId, String status, String type,
        Integer opportunityId, Double minValue, Double maxValue, LocalDate fromDate,
        LocalDate toDate, LocalDate expiryFromDate, LocalDate expiryToDate,
        String paymentStatus, String keyword, Boolean includePayments,
        Boolean includeEmployees, String exportType, Pageable pageable,
        Long currentUserId, Authentication authentication, HttpServletResponse response
    );
    
    /**
     * Get detailed payment status report
     */
    Object getPaymentStatusReport(
        Integer customerId, Integer salesId, Integer contractId, String status,
        LocalDate fromDate, LocalDate toDate, LocalDate paidFromDate,
        LocalDate paidToDate, Double minAmount, Double maxAmount,
        Boolean includeDetails, String exportType, Pageable pageable,
        Long currentUserId, Authentication authentication, HttpServletResponse response
    );
    
    /**
     * Get sales KPI progress report
     */
    Object getKpiProgressReport(
        Integer salesId, Integer year, Integer quarter, Integer month,
        Double minAchievement, Double maxAchievement, Boolean includeDetails,
        String exportType, Pageable pageable, Long currentUserId,
        Authentication authentication, HttpServletResponse response
    );
} 