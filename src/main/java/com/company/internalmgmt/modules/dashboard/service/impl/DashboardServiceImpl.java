package com.company.internalmgmt.modules.dashboard.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.math.BigDecimal;

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
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.service.MarginService;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest;
import com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.OpportunitySummaryDTO;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;

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
            List<MarginDistributionDTO.TrendItemDTO> trend = Arrays.asList(
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.minusMonths(5).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build(),
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.minusMonths(4).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build(),
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build(),
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build(),
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build(),
                    MarginDistributionDTO.TrendItemDTO.builder()
                            .month(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .value(marginSummary.getSummary().getAverageMargin().doubleValue())
                            .build()
            );
            
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
            
            // Calculate revenue metrics
            long totalRevenue = contracts.getContent().stream()
                    .mapToLong(contract -> contract.getAmount() != null ? 
                            contract.getAmount().longValue() : 0L)
                    .sum();
            
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
            
            // Calculate target achievements (assuming targets from contracts)
            long monthlyTarget = totalRevenue / 12; // Simple assumption
            long quarterlyTarget = totalRevenue / 4;
            long ytdTarget = totalRevenue;
            
            return RevenueSummaryDTO.builder()
                    .currentMonth(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(monthlyTarget)
                            .actual(totalRevenue / 12) // Simple calculation
                            .achievement(monthlyTarget > 0 ? (double) (totalRevenue / 12) * 100 / monthlyTarget : 0.0)
                            .build())
                    .currentQuarter(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(quarterlyTarget)
                            .actual(totalRevenue / 4) // Simple calculation
                            .achievement(quarterlyTarget > 0 ? (double) (totalRevenue / 4) * 100 / quarterlyTarget : 0.0)
                            .build())
                    .ytd(RevenueSummaryDTO.RevenuePeriodDTO.builder()
                            .target(ytdTarget)
                            .actual(totalRevenue)
                            .achievement(ytdTarget > 0 ? (double) totalRevenue * 100 / ytdTarget : 0.0)
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
            
            // Get margin summary to get team utilization data
            MarginSummaryDTO marginSummary = marginService.getMarginSummary(
                    teamId, 
                    "month", 
                    fromDate, 
                    toDate, 
                    null, 
                    null, 
                    null, 
                    "table", 
                    "team"
            );
            
            // Calculate overall utilization (simple assumption based on allocated vs total)
            int totalEmployees = (int) employees.getTotalElements();
            int allocatedEmployees = 0;
            
            for (EmployeeDto emp : employees.getContent()) {
                String status = emp.getCurrentStatus();
                if (status != null && ("allocated".equalsIgnoreCase(status) || "active".equalsIgnoreCase(status))) {
                    allocatedEmployees++;
                }
            }
            
            double overallUtilization = totalEmployees > 0 ? 
                    (double) allocatedEmployees * 100 / totalEmployees : 0.0;
            
            // Build team utilization list from margin summary
            List<UtilizationRateDTO.TeamUtilizationDTO> teamUtilizations = marginSummary.getTeams().stream()
                    .map(team -> UtilizationRateDTO.TeamUtilizationDTO.builder()
                            .team(team.getName())
                            .rate(team.getMargin() != null ? team.getMargin().doubleValue() : 0.0)
                            .build())
                    .collect(Collectors.toList());
            
            // Build trend data (mock for now - could be enhanced with historical data)
            List<UtilizationRateDTO.UtilizationTrendDTO> trendData = Arrays.asList(
                    UtilizationRateDTO.UtilizationTrendDTO.builder()
                            .month("Jan").value(75.0).build(),
                    UtilizationRateDTO.UtilizationTrendDTO.builder()
                            .month("Feb").value(82.0).build(),
                    UtilizationRateDTO.UtilizationTrendDTO.builder()
                            .month("Mar").value(overallUtilization).build()
            );
            
            return UtilizationRateDTO.builder()
                    .overall(overallUtilization)
                    .byTeam(teamUtilizations)
                    .trend(trendData)
                    .build();
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return UtilizationRateDTO.builder()
                    .overall(0.0)
                    .byTeam(Collections.emptyList())
                    .trend(Collections.emptyList())
                    .build();
        }
    }
} 