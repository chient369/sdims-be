package com.company.internalmgmt.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.contract.dto.ContractDTO;
import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;
import com.company.internalmgmt.modules.contract.service.ContractService;
import com.company.internalmgmt.modules.dashboard.dto.DashboardSummaryDTO;
import com.company.internalmgmt.modules.dashboard.dto.EmployeeStatusDTO;
import com.company.internalmgmt.modules.dashboard.dto.MarginDistributionDTO;
import com.company.internalmgmt.modules.dashboard.dto.OpportunityStatusDTO;
import com.company.internalmgmt.modules.dashboard.dto.RevenueSummaryDTO;
import com.company.internalmgmt.modules.dashboard.dto.UtilizationRateDTO;
import com.company.internalmgmt.modules.dashboard.service.DashboardService;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;
import com.company.internalmgmt.modules.hrm.service.TeamService;
import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.service.MarginService;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest;
import com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.OpportunitySummaryDTO;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;

/**
 * Implementation of DashboardService
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private OpportunityService opportunityService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private MarginService marginService;
    
    @Autowired
    private ContractPaymentTermService paymentTermService;
    
    @Autowired
    private EmployeeStatusLogService employeeStatusLogService;
    
    @Autowired
    private TeamService teamService;
    
    @Override
    public DashboardSummaryDTO getDashboardSummary(
            LocalDate fromDate, 
            LocalDate toDate, 
            Long teamId, 
            List<String> widgets, 
            Long currentUserId, 
            Authentication authentication) {
        
        // Create date range
        DashboardSummaryDTO.DateRangeDTO dateRange = DashboardSummaryDTO.DateRangeDTO.builder()
                .fromDate(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toDate(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
        
        // Build widgets data based on requested widgets
        DashboardSummaryDTO.WidgetsDTO.WidgetsDTOBuilder widgetsBuilder = 
                DashboardSummaryDTO.WidgetsDTO.builder();
        
        if (widgets.contains("opportunity_status")) {
            widgetsBuilder.opportunityStatus(buildOpportunityStatus(fromDate, toDate, teamId, currentUserId, authentication));
        }
        
        if (widgets.contains("margin_distribution")) {
            widgetsBuilder.marginDistribution(buildMarginDistribution(fromDate, toDate, teamId, currentUserId, authentication));
        }
        
        if (widgets.contains("revenue_summary")) {
            widgetsBuilder.revenueSummary(buildRevenueSummary(fromDate, toDate, teamId, currentUserId, authentication));
        }
        
        if (widgets.contains("employee_status")) {
            widgetsBuilder.employeeStatus(buildEmployeeStatus(fromDate, toDate, teamId, currentUserId, authentication));
        }
        
        if (widgets.contains("utilization_rate")) {
            widgetsBuilder.utilizationRate(buildUtilizationRate(fromDate, toDate, teamId, currentUserId, authentication));
        }
        
        return DashboardSummaryDTO.builder()
                .dateRange(dateRange)
                .widgets(widgetsBuilder.build())
                .build();
    }
    
    /**
     * Build opportunity status widget data
     */
    private OpportunityStatusDTO buildOpportunityStatus(
            LocalDate fromDate, LocalDate toDate, Long teamId, 
            Long currentUserId, Authentication authentication) {
        
        try {
            // Build request for opportunity list
            ListOpportunitiesRequest request = new ListOpportunitiesRequest();
            request.setFromDate(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            request.setToDate(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            request.setPage(1);
            request.setSize(1000); // Get large number to get all opportunities for summary
            request.setSortBy("lastInteractionDate");
            request.setSortDir("desc");
            
            // Get opportunities from service
            ListOpportunitiesResponse response = opportunityService.getOpportunities(request);
            OpportunitySummaryDTO summary = response.getSummary();
            List<OpportunityDTO> opportunities = response.getContent();
            
            // Extract status counts from byStatus map (using green/yellow/red based on status)
            int greenCount = summary.getByStatus().getOrDefault("won", 0) + 
                           summary.getByStatus().getOrDefault("qualified", 0);
            int yellowCount = summary.getByStatus().getOrDefault("proposal", 0) + 
                            summary.getByStatus().getOrDefault("negotiation", 0);
            int redCount = summary.getByStatus().getOrDefault("lost", 0) + 
                         summary.getByStatus().getOrDefault("new", 0);
            
            // Build by status
            OpportunityStatusDTO.ByStatusDTO byStatus = OpportunityStatusDTO.ByStatusDTO.builder()
                    .green(greenCount)
                    .yellow(yellowCount)
                    .red(redCount)
                    .build();
            
            // Build by deal stage from byStatus map
            List<OpportunityStatusDTO.DealStageDTO> byDealStage = Arrays.asList(
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("New")
                            .count(summary.getByStatus().getOrDefault("new", 0))
                            .build(),
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("Contacted")
                            .count(summary.getByStatus().getOrDefault("contacted", 0))
                            .build(),
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("Qualified")
                            .count(summary.getByStatus().getOrDefault("qualified", 0))
                            .build(),
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("Proposal")
                            .count(summary.getByStatus().getOrDefault("proposal", 0))
                            .build(),
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("Negotiation")
                            .count(summary.getByStatus().getOrDefault("negotiation", 0))
                            .build(),
                    OpportunityStatusDTO.DealStageDTO.builder()
                            .stage("Won")
                            .count(summary.getByStatus().getOrDefault("won", 0))
                            .build()
            );
            
            // Get top opportunities (first 5)
            List<OpportunityStatusDTO.TopOpportunityDTO> topOpportunities = opportunities.stream()
                    .limit(5)
                    .map(opp -> OpportunityStatusDTO.TopOpportunityDTO.builder()
                            .id(opp.getId().intValue())
                            .name(opp.getName())
                            .customer(opp.getCustomerName())
                            .value(opp.getAmount() != null ? opp.getAmount().longValue() : 0L)
                            .stage(opp.getStatus())
                            .lastInteraction(opp.getLastInteractionDate() != null ? 
                                    opp.getLastInteractionDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                            .build())
                    .collect(Collectors.toList());
            
            return OpportunityStatusDTO.builder()
                    .totalOpportunities(summary.getTotalCount())
                    .byStatus(byStatus)
                    .byDealStage(byDealStage)
                    .topOpportunities(topOpportunities)
                    .build();
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return OpportunityStatusDTO.builder()
                    .totalOpportunities(0)
                    .byStatus(OpportunityStatusDTO.ByStatusDTO.builder()
                            .green(0).yellow(0).red(0).build())
                    .byDealStage(Collections.emptyList())
                    .topOpportunities(Collections.emptyList())
                    .build();
        }
    }
    
    /**
     * Build margin distribution widget data
     */
    private MarginDistributionDTO buildMarginDistribution(
            LocalDate fromDate, LocalDate toDate, Long teamId, 
            Long currentUserId, Authentication authentication) {
        
        try {
            // Get margin summary from margin service
            MarginSummaryDTO marginSummary = marginService.getMarginSummary(
                    teamId, 
                    "month", 
                    fromDate, 
                    toDate, 
                    null, 
                    null, 
                    null, 
                    "table", 
                    "status"
            );
            
            // Get margin thresholds
            Map<String, BigDecimal> thresholds = marginService.getMarginThresholds();
            
            // Get employee margins to calculate distribution
            Pageable pageable = PageRequest.of(0, 1000); // Get large number for summary
            Page<EmployeeMarginDTO> employeeMargins = marginService.getEmployeeMargins(
                    null, 
                    teamId, 
                    "month", 
                    fromDate, 
                    toDate, 
                    null, 
                    null, 
                    null, 
                    null, 
                    pageable
            );
            
            // Calculate distribution counts
            int totalEmployees = (int) employeeMargins.getTotalElements();
            int redCount = 0, yellowCount = 0, greenCount = 0;
            
            for (EmployeeMarginDTO emp : employeeMargins.getContent()) {
                // Get the most recent margin status
                if (!emp.getPeriods().isEmpty()) {
                    String status = emp.getPeriods().get(emp.getPeriods().size() - 1).getMarginStatus();
                    switch (status.toLowerCase()) {
                        case "red": redCount++; break;
                        case "yellow": yellowCount++; break;
                        case "green": greenCount++; break;
                    }
                }
            }
            
            // Build distribution
            MarginDistributionDTO.DistributionDTO distribution = MarginDistributionDTO.DistributionDTO.builder()
                    .green(MarginDistributionDTO.DistributionCategoryDTO.builder()
                            .count(greenCount)
                            .percentage(totalEmployees > 0 ? (double) greenCount * 100 / totalEmployees : 0.0)
                            .build())
                    .yellow(MarginDistributionDTO.DistributionCategoryDTO.builder()
                            .count(yellowCount)
                            .percentage(totalEmployees > 0 ? (double) yellowCount * 100 / totalEmployees : 0.0)
                            .build())
                    .red(MarginDistributionDTO.DistributionCategoryDTO.builder()
                            .count(redCount)
                            .percentage(totalEmployees > 0 ? (double) redCount * 100 / totalEmployees : 0.0)
                            .build())
                    .build();
            
            // Build trend data (last 6 months)
            List<MarginDistributionDTO.TrendItemDTO> trend = new ArrayList<>();
            LocalDate trendEndDate = toDate; // Use toDate from parameters as the end point for the trend calculation

            for (int i = 0; i < 6; i++) {
                LocalDate monthEndDate = trendEndDate.minusMonths(i);
                LocalDate monthStartDate = monthEndDate.withDayOfMonth(1);
                
                try {
                    MarginSummaryDTO monthlySummary = marginService.getMarginSummary(
                            teamId,
                            "month",
                            monthStartDate,
                            monthEndDate,
                            null,
                            null,
                            null,
                            "table", // Assuming "table" is suitable for this context
                            "status" // Assuming "status" is suitable
                    );
                    
                    double monthlyAverageMargin = 0.0;
                    if (monthlySummary != null && 
                        monthlySummary.getSummary() != null && 
                        monthlySummary.getSummary().getAverageMargin() != null) {
                        monthlyAverageMargin = monthlySummary.getSummary().getAverageMargin().doubleValue();
                    }
                    
                    trend.add(MarginDistributionDTO.TrendItemDTO.builder()
                            .month(monthEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(monthlyAverageMargin)
                            .build());
                } catch (Exception e) {
                    // Log error or handle as needed, e.g., add a default/error trend item
                    // For now, just print a message and add a zero-value trend item
                    System.err.println("Error fetching margin summary for month " + monthEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) + ": " + e.getMessage());
                    trend.add(MarginDistributionDTO.TrendItemDTO.builder()
                            .month(monthEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(0.0) // Default to 0 if error
                            .build());
                }
            }
            Collections.reverse(trend); // Ensure the trend is in chronological order
            
            return MarginDistributionDTO.builder()
                    .totalEmployees(totalEmployees)
                    .distribution(distribution)
                    .trend(trend)
                    .build();
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return MarginDistributionDTO.builder()
                    .totalEmployees(0)
                    .distribution(MarginDistributionDTO.DistributionDTO.builder()
                            .green(MarginDistributionDTO.DistributionCategoryDTO.builder().count(0).percentage(0.0).build())
                            .yellow(MarginDistributionDTO.DistributionCategoryDTO.builder().count(0).percentage(0.0).build())
                            .red(MarginDistributionDTO.DistributionCategoryDTO.builder().count(0).percentage(0.0).build())
                            .build())
                    .trend(Collections.emptyList())
                    .build();
        }
    }
    
    /**
     * Build revenue summary widget data
     */
    private RevenueSummaryDTO buildRevenueSummary(
            LocalDate fromDate, LocalDate toDate, Long teamId, 
            Long currentUserId, Authentication authentication) {
        
        try {
            // Get contract data
            Pageable pageable = PageRequest.of(0, 1000);
            Page<ContractDTO> contracts = contractService.searchContracts(
                    null, null, null, null, null, null, null, 
                    fromDate, toDate, null, pageable
            );
            
            // Calculate target achievements
            // TODO: Replace with actual target data source if available.
            // The following is a simplified calculation for targets and actuals based on total revenue in the period.
            long monthlyTarget = 0; 
            long quarterlyTarget = 0;
            long ytdTarget = 0;
            long actualMonthlyRevenue = 0;
            long actualQuarterlyRevenue = 0;
            long actualYtdRevenue = 0;

            // Calculate actual revenue for current month, quarter, YTD based on contract sign dates
            // This assumes revenue is recognized at contract signing for simplicity for this widget.
            // A more accurate approach would involve payment terms and revenue recognition schedules.
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfCurrentMonth = fromDate.withDayOfMonth(1); // Use fromDate from request to determine current month context
            LocalDate firstDayOfCurrentQuarter = firstDayOfCurrentMonth.with(firstDayOfCurrentMonth.getMonth().firstMonthOfQuarter());
            LocalDate firstDayOfCurrentYear = firstDayOfCurrentMonth.withDayOfYear(1);

            actualMonthlyRevenue = contracts.getContent().stream()
                .filter(c -> c.getSignDate() != null && 
                             !c.getSignDate().isBefore(firstDayOfCurrentMonth) && 
                             c.getSignDate().isBefore(firstDayOfCurrentMonth.plusMonths(1)))
                .mapToLong(c -> c.getAmount() != null ? c.getAmount().longValue() : 0L)
                .sum();

            actualQuarterlyRevenue = contracts.getContent().stream()
                .filter(c -> c.getSignDate() != null && 
                             !c.getSignDate().isBefore(firstDayOfCurrentQuarter) && 
                             c.getSignDate().isBefore(firstDayOfCurrentQuarter.plusMonths(3)))
                .mapToLong(c -> c.getAmount() != null ? c.getAmount().longValue() : 0L)
                .sum();

            actualYtdRevenue = contracts.getContent().stream()
                .filter(c -> c.getSignDate() != null && 
                             !c.getSignDate().isBefore(firstDayOfCurrentYear) && 
                             c.getSignDate().isBefore(firstDayOfCurrentYear.plusYears(1)))
                .mapToLong(c -> c.getAmount() != null ? c.getAmount().longValue() : 0L)
                .sum();

            // Simplified targets based on total revenue in the requested period (fromDate - toDate)
            // This is a placeholder and should be replaced with actual business targets.
            if (contracts.getTotalElements() > 0) {
                 long totalRevenueInPeriod = contracts.getContent().stream()
                    .mapToLong(contract -> contract.getAmount() != null ? contract.getAmount().longValue() : 0L)
                    .sum();
                // Distribute totalRevenueInPeriod as a mock target for month/quarter/year based on the period length.
                // This is a very rough estimation.
                long daysInPeriod = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate.plusDays(1));
                if (daysInPeriod > 0) {
                    monthlyTarget = (long) (totalRevenueInPeriod * (30.0 / daysInPeriod));
                    quarterlyTarget = (long) (totalRevenueInPeriod * (90.0 / daysInPeriod));
                    ytdTarget = (long) (totalRevenueInPeriod * (365.0 / daysInPeriod)); 
                }
            }

            // Count new contracts in period
            int newContractsCount = (int) contracts.getContent().stream()
                    .filter(contract -> {
                        LocalDate signedDate = contract.getSignDate();
                        return signedDate != null && 
                               !signedDate.isBefore(fromDate) && 
                               !signedDate.isAfter(toDate);
                    })
                    .count();
            
            // Get payment information
            List<ContractPaymentTermDTO> overduePayments = paymentTermService.findOverduePaymentTerms();
            long overdueAmount = overduePayments.stream()
                    .mapToLong(payment -> payment.getAmount() != null ? 
                            payment.getAmount().longValue() : 0L)
                    .sum();
            
            List<ContractPaymentTermDTO> upcomingPayments = paymentTermService.findPaymentTermsDueSoon(30);
            long upcomingAmount = upcomingPayments.stream()
                    .mapToLong(payment -> payment.getAmount() != null ? 
                            payment.getAmount().longValue() : 0L)
                    .sum();
            
            return RevenueSummaryDTO.builder()
                    .currentMonth(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(monthlyTarget) // Placeholder target
                            .actual(actualMonthlyRevenue)
                            .achievement(monthlyTarget > 0 ? (double) actualMonthlyRevenue * 100 / monthlyTarget : (actualMonthlyRevenue > 0 ? 100.0 : 0.0))
                            .build())
                    .currentQuarter(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(quarterlyTarget) // Placeholder target
                            .actual(actualQuarterlyRevenue)
                            .achievement(quarterlyTarget > 0 ? (double) actualQuarterlyRevenue * 100 / quarterlyTarget : (actualQuarterlyRevenue > 0 ? 100.0 : 0.0))
                            .build())
                    .ytd(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(ytdTarget) // Placeholder target
                            .actual(actualYtdRevenue)
                            .achievement(ytdTarget > 0 ? (double) actualYtdRevenue * 100 / ytdTarget : (actualYtdRevenue > 0 ? 100.0 : 0.0))
                            .build())
                    .contracts(RevenueSummaryDTO.ContractsDTO.builder()
                            .total((int) contracts.getTotalElements())
                            .newlyAdded(newContractsCount)
                            .build())
                    .payment(RevenueSummaryDTO.PaymentDTO.builder()
                            .totalDue(overdueAmount + upcomingAmount)
                            .overdue(overdueAmount)
                            .upcoming(upcomingAmount)
                            .build())
                    .build();
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return RevenueSummaryDTO.builder()
                    .currentMonth(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(0L).actual(0L).achievement(0.0).build())
                    .currentQuarter(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(0L).actual(0L).achievement(0.0).build())
                    .ytd(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(0L).actual(0L).achievement(0.0).build())
                    .contracts(RevenueSummaryDTO.ContractsDTO.builder()
                            .total(0).newlyAdded(0).build())
                    .payment(RevenueSummaryDTO.PaymentDTO.builder()
                            .totalDue(0L).overdue(0L).upcoming(0L).build())
                    .build();
        }
    }
    
    /**
     * Build employee status widget data
     */
    private EmployeeStatusDTO buildEmployeeStatus(
            LocalDate fromDate, LocalDate toDate, Long teamId, 
            Long currentUserId, Authentication authentication) {
        
        try {
            // Get all employees
            Pageable pageable = PageRequest.of(0, 1000);
            Page<EmployeeDto> employees = employeeService.findEmployees(
                    null, 
                    teamId != null ? teamId.intValue() : null, 
                    null, 
                    null, 
                    null, 
                    null, 
                    null, 
                    pageable
            );
            
            // Count employees by status
            int totalEmployees = (int) employees.getTotalElements();
            int allocatedCount = 0, availableCount = 0, endingSoonCount = 0, onLeaveCount = 0;
            
            for (EmployeeDto emp : employees.getContent()) {
                String status = emp.getCurrentStatus();
                if (status != null) {
                    switch (status.toLowerCase()) {
                        case "allocated": 
                        case "active": 
                            allocatedCount++; 
                            break;
                        case "available": 
                        case "bench": 
                            availableCount++; 
                            break;
                        case "ending_soon": 
                            endingSoonCount++; 
                            break;
                        case "on_leave": 
                        case "leave": 
                            onLeaveCount++; 
                            break;
                    }
                }
            }
            
            // Get employees ending soon by querying EmployeeStatusLog
            List<EmployeeStatusDTO.EmployeeBasicDTO> endingSoonList = employees.getContent().stream()
                    .filter(emp -> "ending_soon".equalsIgnoreCase(emp.getCurrentStatus()))
                    .limit(5)
                    .map(emp -> {
                        EmployeeStatusDTO.EmployeeBasicDTO.EmployeeBasicDTOBuilder builder = EmployeeStatusDTO.EmployeeBasicDTO.builder()
                                .id(emp.getId().intValue())
                                .name(emp.getFirstName() + " " + emp.getLastName());
                        
                        // Get project end date from latest status log
                        try {
                            com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto latestStatus = 
                                    employeeStatusLogService.findMostRecentByEmployeeId(emp.getId());
                            if (latestStatus != null && latestStatus.getExpectedEndDate() != null) {
                                builder.projectEndDate(latestStatus.getExpectedEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                            } else {
                                builder.projectEndDate(null);
                            }
                        } catch (Exception e) {
                            builder.projectEndDate(null);
                        }
                        
                        return builder.build();
                    })
                    .collect(Collectors.toList());
            
            return EmployeeStatusDTO.builder()
                    .totalEmployees(totalEmployees)
                    .byStatus(EmployeeStatusDTO.ByStatusDTO.builder()
                            .allocated(allocatedCount)
                            .available(availableCount)
                            .endingSoon(endingSoonCount)
                            .onLeave(onLeaveCount)
                            .build())
                    .endingSoonList(endingSoonList)
                    .build();
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return EmployeeStatusDTO.builder()
                    .totalEmployees(0)
                    .byStatus(EmployeeStatusDTO.ByStatusDTO.builder()
                            .allocated(0).available(0).endingSoon(0).onLeave(0).build())
                    .endingSoonList(Collections.emptyList())
                    .build();
        }
    }
    
    /**
     * Build utilization rate widget data
     */
    private UtilizationRateDTO buildUtilizationRate(
            LocalDate fromDate, LocalDate toDate, Long teamId, 
            Long currentUserId, Authentication authentication) {
        
        try {
            // Get all active employees (not resigned/terminated)
            // Assuming "active", "allocated", "available", "ending_soon", "on_leave" are active states
            // and "resigned", "terminated" are inactive.
            // For simplicity, let's consider all employees returned by default findEmployees as potentially active.
            // A more precise filter might be needed based on actual status values.
            Pageable allEmployeesPageable = PageRequest.of(0, Integer.MAX_VALUE); // Get all
            Page<EmployeeDto> allActiveEmployeesPage = employeeService.findEmployees(
                    null, null, null, null, null, null, null, allEmployeesPageable
            );
            List<EmployeeDto> allActiveEmployeesList = allActiveEmployeesPage.getContent();
            long totalActiveEmployees = allActiveEmployeesList.stream()
                .filter(emp -> emp.getCurrentStatus() != null && 
                               !emp.getCurrentStatus().equalsIgnoreCase("Resigned") &&
                               !emp.getCurrentStatus().equalsIgnoreCase("Terminated"))
                .count();

            // Get allocated employees
            long allocatedEmployeesCount = allActiveEmployeesList.stream()
                    .filter(emp -> emp.getCurrentStatus() != null && 
                                   (emp.getCurrentStatus().equalsIgnoreCase("Allocated") || 
                                    emp.getCurrentStatus().equalsIgnoreCase("Active"))) // Assuming "Active" also means allocated
                    .count();
            
            double overallUtilization = totalActiveEmployees > 0 ? 
                    (double) allocatedEmployeesCount * 100 / totalActiveEmployees : 0.0;
            
            // Build team utilization list
            List<com.company.internalmgmt.modules.hrm.dto.TeamDto> teams = teamService.getAllTeams();
            List<UtilizationRateDTO.TeamUtilizationDTO> teamUtilizations = teams.stream().map(t -> {
                Page<EmployeeDto> teamEmployeesPage = employeeService.findEmployees(
                        null, t.getId().intValue(), null, null, null, null, null, allEmployeesPageable
                );
                List<EmployeeDto> teamEmployeesList = teamEmployeesPage.getContent();

                long totalActiveInTeam = teamEmployeesList.stream()
                    .filter(emp -> emp.getCurrentStatus() != null &&
                                   !emp.getCurrentStatus().equalsIgnoreCase("Resigned") &&
                                   !emp.getCurrentStatus().equalsIgnoreCase("Terminated"))
                    .count();
                
                long allocatedInTeam = teamEmployeesList.stream()
                        .filter(emp -> emp.getCurrentStatus() != null &&
                                       (emp.getCurrentStatus().equalsIgnoreCase("Allocated") ||
                                        emp.getCurrentStatus().equalsIgnoreCase("Active")))
                        .count();
                
                double teamRate = totalActiveInTeam > 0 ? 
                        (double) allocatedInTeam * 100 / totalActiveInTeam : 0.0;
                
                return UtilizationRateDTO.TeamUtilizationDTO.builder()
                        .team(t.getName())
                        .rate(teamRate)
                        .build();
            }).collect(Collectors.toList());

            // Build trend data (last 6 months)
            List<UtilizationRateDTO.UtilizationTrendDTO> trendData = new ArrayList<>();
            LocalDate currentTrendMonth = LocalDate.now(); // Start from the current month for the trend

            for (int i = 0; i < 6; i++) {
                LocalDate loopMonth = currentTrendMonth.minusMonths(i);
                // For calculating utilization, we typically look at the state at a point in time
                // or an average over a period. For simplicity, we'll re-calculate overall utilization
                // as if it were for that past month. This might need more sophisticated historical tracking in a real system.

                // Recalculate overall utilization for the specific month (loopMonth)
                // This is still using current statuses. A real implementation needs to check statuses as of 'loopMonth'
                // The following is an approximation for trend calculation as EmployeeDto reflects current state,
                // and we don't have easy access to historical monthly snapshots of every employee's status for past months.
                long monthlyTotalActiveEmployees = 0;
                long monthlyAllocatedEmployeesCount = 0;
                double monthlyOverallUtilization = 0.0;

                try {
                    // Note: employeeService.findEmployees doesn't directly support historical queries by date.
                    // The following logic assumes current employee statuses reflect the past, which is an approximation.
                    // For accurate historical trend, you would need to query historical employee status data (e.g., from EmployeeStatusLogTable for each employee active in that month)
                    
                    Pageable allEmployeesPageableForTrend = PageRequest.of(0, Integer.MAX_VALUE);
                    Page<EmployeeDto> allActiveEmployeesPageForTrend = employeeService.findEmployees(
                            null, null, null, null, null, null, null, allEmployeesPageableForTrend
                    );
                    List<EmployeeDto> allActiveEmployeesListForTrend = allActiveEmployeesPageForTrend.getContent();

                    // This is still using current statuses. A real implementation needs to check statuses as of 'loopMonth'
                    // The following is an approximation for trend calculation as EmployeeDto reflects current state,
                    // and we don't have easy access to historical monthly snapshots of every employee's status for past months.
                    monthlyTotalActiveEmployees = allActiveEmployeesListForTrend.stream()
                        .filter(emp -> emp.getCurrentStatus() != null && 
                                       !emp.getCurrentStatus().equalsIgnoreCase("Resigned") &&
                                       !emp.getCurrentStatus().equalsIgnoreCase("Terminated") &&
                                       (emp.getHireDate() == null || !emp.getHireDate().isAfter(loopMonth.withDayOfMonth(loopMonth.lengthOfMonth()))))
                        .count();

                    monthlyAllocatedEmployeesCount = allActiveEmployeesListForTrend.stream()
                            .filter(emp -> emp.getCurrentStatus() != null && 
                                           (emp.getCurrentStatus().equalsIgnoreCase("Allocated") || 
                                            emp.getCurrentStatus().equalsIgnoreCase("Active")) &&
                                           (emp.getHireDate() == null || !emp.getHireDate().isAfter(loopMonth.withDayOfMonth(loopMonth.lengthOfMonth()))))
                            .count();

                    monthlyOverallUtilization = monthlyTotalActiveEmployees > 0 ? 
                            (double) monthlyAllocatedEmployeesCount * 100 / monthlyTotalActiveEmployees : 0.0;

                } catch (Exception e) {
                    System.err.println("Error calculating utilization for trend for month " + loopMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")) + ": " + e.getMessage());
                    monthlyOverallUtilization = 0.0; // Default to 0 if error
                }

                trendData.add(UtilizationRateDTO.UtilizationTrendDTO.builder()
                        .month(loopMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .value(monthlyOverallUtilization)
                        .build());
            }
            Collections.reverse(trendData); // Ensure chronological order
            
            return UtilizationRateDTO.builder()
                    .overall(overallUtilization)
                    .byTeam(teamUtilizations)
                    .trend(trendData)
                    .build();
            
        } catch (Exception e) {
            // Log the exception
            // log.error("Error building utilization rate widget: {}", e.getMessage(), e);
            // Return fallback data if error occurs
            return UtilizationRateDTO.builder()
                    .overall(0.0)
                    .byTeam(Collections.emptyList())
                    .trend(Collections.emptyList())
                    .build();
        }
    }
} 