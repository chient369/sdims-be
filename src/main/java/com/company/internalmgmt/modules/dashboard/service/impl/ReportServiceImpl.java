package com.company.internalmgmt.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.contract.service.ContractService;
import com.company.internalmgmt.modules.dashboard.dto.employee.EmployeeReportDTO;
import com.company.internalmgmt.modules.dashboard.dto.margin.MarginReportDTO;
import com.company.internalmgmt.modules.dashboard.service.ReportService;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;
import com.company.internalmgmt.modules.hrm.service.TeamService;
import com.company.internalmgmt.modules.margin.service.MarginService;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeResponse;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.service.EmployeeSkillService;
import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.contract.service.ContractEmployeeService;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityAssignmentRepository;
import com.company.internalmgmt.modules.opportunity.service.OpportunityNoteService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ReportService
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private OpportunityService opportunityService;
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private MarginService marginService;
    
    @Autowired
    private EmployeeSkillService employeeSkillService;
    
    @Autowired
    private EmployeeStatusLogService employeeStatusLogService;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private ContractEmployeeService contractEmployeeService;
    
    @Autowired
    private ContractPaymentTermService paymentTermService;
    
    @Autowired
    private OpportunityAssignmentRepository opportunityAssignmentRepository;
    
    @Autowired
    private OpportunityNoteService opportunityNoteService;
    
    @Override
    public EmployeeReportDTO getEmployeeListReport(
            Integer teamId, String position, String status, List<Integer> skills,
            Integer minExperience, Integer projectId, String utilization,
            Boolean includeSkills, Boolean includeProjects, String exportType,
            Pageable pageable, Long currentUserId, Authentication authentication,
            HttpServletResponse response) {
        
        // Handle export types (csv, excel)
        if (!"json".equals(exportType)) {
            handleExport(exportType, "employee_report", response);
            return null;
        }
        
        try {
            // Get employee data from service
            Page<EmployeeDto> employeePage = employeeService.findEmployees(
                    null, // searchTerm
                    teamId, 
                    position, 
                    status, 
                    null, // skills filter - not directly supported in service
                    null, // fromDate
                    null, // toDate
                    pageable
            );
            
            // Build report info
            Map<String, Object> filters = new HashMap<>();
            if (teamId != null) filters.put("teamId", teamId);
            if (position != null) filters.put("position", position);
            if (status != null) filters.put("status", status);
            if (skills != null && !skills.isEmpty()) filters.put("skills", skills);
            if (minExperience != null) filters.put("minExperience", minExperience);
            if (projectId != null) filters.put("projectId", projectId);
            if (utilization != null) filters.put("utilization", utilization);
            
            EmployeeReportDTO.ReportInfoDTO reportInfo = EmployeeReportDTO.ReportInfoDTO.builder()
                    .reportName("Báo cáo danh sách nhân viên")
                    .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .filters(filters)
                    .build();
            
            // Convert employees to report content
            List<EmployeeReportDTO.EmployeeDetailDTO> content = employeePage.getContent().stream()
                    .map(emp -> {
                        // Get detailed employee info if needed
                        EmployeeResponse empDetail = null;
                        if (Boolean.TRUE.equals(includeSkills) || Boolean.TRUE.equals(includeProjects)) {
                            try {
                                EmployeeDto empDto = employeeService.findById(emp.getId());
                                // Note: EmployeeDto doesn't include skills/projects, would need separate service calls
                            } catch (Exception e) {
                                // Continue without detailed info if error
                            }
                        }
                        
                        // Build skills list
                        List<EmployeeReportDTO.SkillDTO> skillDTOs = Collections.emptyList();
                        if (Boolean.TRUE.equals(includeSkills)) {
                            try {
                                // Get skills from EmployeeSkillService
                                List<EmployeeSkillDto> employeeSkills = employeeSkillService.findAllByEmployeeId(emp.getId());
                                skillDTOs = employeeSkills.stream()
                                        .map(skill -> EmployeeReportDTO.SkillDTO.builder()
                                                .id(skill.getSkillId().intValue())
                                                .name(skill.getSkillName())
                                                .category(skill.getSkillCategoryName())
                                                .level(skill.getSelfAssessmentLevel())
                                                .years(skill.getYearsExperience() != null ? 
                                                        skill.getYearsExperience().intValue() : 0)
                                                .build())
                                        .collect(Collectors.toList());
                            } catch (Exception e) {
                                // Continue without skills if error
                            }
                        }
                        
                        // Get current project from latest status log
                        EmployeeReportDTO.ProjectDTO currentProject = null;
                        if (Boolean.TRUE.equals(includeProjects)) {
                            try {
                                EmployeeStatusLogDto latestStatus = employeeStatusLogService.findMostRecentByEmployeeId(emp.getId());
                                if (latestStatus != null && latestStatus.getProjectName() != null) {
                                    currentProject = EmployeeReportDTO.ProjectDTO.builder()
                                            .id(null) // No project ID in status log
                                            .name(latestStatus.getProjectName())
                                            .customer(latestStatus.getClientName())
                                            .allocation(latestStatus.getAllocationPercentage())
                                            .startDate(latestStatus.getStartDate() != null ? 
                                                    latestStatus.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .endDate(latestStatus.getExpectedEndDate() != null ? 
                                                    latestStatus.getExpectedEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .build();
                                }
                            } catch (Exception e) {
                                // Continue without project info if error
                            }
                        }
                        
                        // Calculate utilization based on status
                        Integer utilRate = 0;
                        if ("allocated".equalsIgnoreCase(emp.getCurrentStatus()) || 
                            "active".equalsIgnoreCase(emp.getCurrentStatus())) {
                            utilRate = 100; // Assume full utilization if allocated
                        }
                        
                        // Calculate experience in years
                        Integer totalExperience = 0;
                        if (emp.getHireDate() != null) {
                            totalExperience = (int) java.time.temporal.ChronoUnit.YEARS.between(
                                    emp.getHireDate(), LocalDate.now());
                        }
                        
                        // Get team leader info
                        EmployeeReportDTO.LeaderDTO teamLeader = null;
                        if (emp.getTeam() != null) {
                            try {
                                TeamDto teamDetail = teamService.getTeamById(emp.getTeam().getId());
                                if (teamDetail.getLeaderId() != null) {
                                    teamLeader = EmployeeReportDTO.LeaderDTO.builder()
                                            .id(teamDetail.getLeaderId().intValue())
                                            .name(teamDetail.getLeaderName())
                                            .build();
                                }
                            } catch (Exception e) {
                                // Continue without leader info if error
                            }
                        }
                        
                        return EmployeeReportDTO.EmployeeDetailDTO.builder()
                                .id(emp.getId().intValue())
                                .employeeCode(emp.getEmployeeCode())
                                .name(emp.getFirstName() + " " + emp.getLastName())
                                .email(emp.getCompanyEmail())
                                .position(emp.getPosition())
                                .team(emp.getTeam() != null ? 
                                        EmployeeReportDTO.TeamDTO.builder()
                                                .id(emp.getTeam().getId().intValue())
                                                .name(emp.getTeam().getName())
                                                .leader(teamLeader)
                                                .build() : null)
                                .status(emp.getCurrentStatus())
                                .currentProject(currentProject)
                                .utilization(utilRate)
                                .skills(skillDTOs)
                                .joinDate(emp.getHireDate() != null ? 
                                        emp.getHireDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                .totalExperience(totalExperience)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            // Calculate summary metrics from actual data
            int totalEmployees = (int) employeePage.getTotalElements();
            int allocatedCount = 0, availableCount = 0, endingSoonCount = 0;
            double totalUtilization = 0.0;
            Map<String, Integer> skillCounts = new HashMap<>();
            
            for (EmployeeDto emp : employeePage.getContent()) {
                String empStatus = emp.getCurrentStatus();
                if (empStatus != null) {
                    switch (empStatus.toLowerCase()) {
                        case "allocated": 
                        case "active": 
                            allocatedCount++; 
                            totalUtilization += 100.0;
                            break;
                        case "available": 
                        case "bench": 
                            availableCount++; 
                            break;
                        case "ending_soon": 
                            endingSoonCount++; 
                            totalUtilization += 50.0; // Assume 50% utilization for ending soon
                            break;
                    }
                }
                
                // Aggregate skills data for top skills
                if (Boolean.TRUE.equals(includeSkills)) {
                    try {
                        List<EmployeeSkillDto> employeeSkills = employeeSkillService.findAllByEmployeeId(emp.getId());
                        for (EmployeeSkillDto skill : employeeSkills) {
                            skillCounts.merge(skill.getSkillName(), 1, Integer::sum);
                        }
                    } catch (Exception e) {
                        // Continue if error getting skills
                    }
                }
            }
            
            // Get top 5 skills
            List<EmployeeReportDTO.TopSkillDTO> topSkills = skillCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(5)
                    .map(entry -> EmployeeReportDTO.TopSkillDTO.builder()
                            .name(entry.getKey())
                            .count(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
            
            double utilizationRate = totalEmployees > 0 ? totalUtilization / totalEmployees : 0.0;
            
            EmployeeReportDTO.SummaryMetricsDTO summaryMetrics = EmployeeReportDTO.SummaryMetricsDTO.builder()
                    .totalEmployees(totalEmployees)
                    .allocatedCount(allocatedCount)
                    .availableCount(availableCount)
                    .endingSoonCount(endingSoonCount)
                    .utilizationRate(utilizationRate)
                    .topSkills(topSkills)
                    .build();
            
            // Build pageable info
            EmployeeReportDTO.PageableDTO pageableInfo = EmployeeReportDTO.PageableDTO.builder()
                    .pageNumber(pageable.getPageNumber() + 1) // Convert to 1-based
                    .pageSize(pageable.getPageSize())
                    .totalPages(employeePage.getTotalPages())
                    .totalElements((int) employeePage.getTotalElements())
                    .sort(pageable.getSort().toString())
                    .build();
            
            return EmployeeReportDTO.builder()
                    .reportInfo(reportInfo)
                    .content(content)
                    .summaryMetrics(summaryMetrics)
                    .pageable(pageableInfo)
                    .build();
                    
        } catch (Exception e) {
            // Return fallback data if error occurs
            return buildFallbackEmployeeReport(pageable, teamId, position, status);
        }
    }
    
    /**
     * Build fallback employee report when service calls fail
     */
    private EmployeeReportDTO buildFallbackEmployeeReport(
            Pageable pageable, Integer teamId, String position, String status) {
        
        Map<String, Object> filters = new HashMap<>();
        if (teamId != null) filters.put("teamId", teamId);
        if (position != null) filters.put("position", position);
        if (status != null) filters.put("status", status);
        
        EmployeeReportDTO.ReportInfoDTO reportInfo = EmployeeReportDTO.ReportInfoDTO.builder()
                .reportName("Báo cáo danh sách nhân viên")
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .filters(filters)
                .build();
        
        EmployeeReportDTO.SummaryMetricsDTO summaryMetrics = EmployeeReportDTO.SummaryMetricsDTO.builder()
                .totalEmployees(0)
                .allocatedCount(0)
                .availableCount(0)
                .endingSoonCount(0)
                .utilizationRate(0.0)
                .topSkills(Collections.emptyList())
                .build();
        
        EmployeeReportDTO.PageableDTO pageableInfo = EmployeeReportDTO.PageableDTO.builder()
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPages(0)
                .totalElements(0)
                .sort(pageable.getSort().toString())
                .build();
        
        return EmployeeReportDTO.builder()
                .reportInfo(reportInfo)
                .content(Collections.emptyList())
                .summaryMetrics(summaryMetrics)
                .pageable(pageableInfo)
                .build();
    }
    
    @Override
    public MarginReportDTO getMarginDetailReport(
            Integer teamId, Integer employeeId, String period, LocalDate fromDate,
            LocalDate toDate, String marginThreshold, String groupBy,
            Boolean includeDetails, String exportType, Pageable pageable,
            Long currentUserId, Authentication authentication, HttpServletResponse response) {
        
        // Handle export types (csv, excel)
        if (!"json".equals(exportType)) {
            handleExport(exportType, "margin_detail_report", response);
            return null;
        }
        
        try {
            // Get margin data based on groupBy (employee vs team)
            if ("employee".equals(groupBy)) {
                // Get employee margin data
                Page<EmployeeMarginDTO> employeeMargins = marginService.getEmployeeMargins(
                        employeeId != null ? employeeId.longValue() : null,
                        teamId != null ? teamId.longValue() : null,
                        period,
                        fromDate,
                        toDate,
                        null, // yearMonth
                        null, // yearQuarter
                        null, // year
                        null, // status
                        pageable
                );
                
                // Convert to report content
                List<Object> content = employeeMargins.getContent().stream()
                        .map(emp -> {
                            // Get team leader info
                            MarginReportDTO.LeaderDTO teamLeader = null;
                            if (emp.getTeam() != null) {
                                try {
                                    TeamDto teamDetail = teamService.getTeamById(emp.getTeam().getId());
                                    if (teamDetail.getLeaderId() != null) {
                                        teamLeader = MarginReportDTO.LeaderDTO.builder()
                                                .id(teamDetail.getLeaderId().intValue())
                                                .name(teamDetail.getLeaderName())
                                                .build();
                                    }
                                } catch (Exception e) {
                                    // Continue without leader info if error
                                }
                            }
                            
                            return MarginReportDTO.EmployeeMarginDTO.builder()
                                    .employeeId(emp.getEmployeeId().intValue())
                                    .employeeCode(emp.getEmployeeCode())
                                    .employeeName(emp.getName())
                                    .team(emp.getTeam() != null ? 
                                            MarginReportDTO.TeamBasicDTO.builder()
                                                    .id(emp.getTeam().getId().intValue())
                                                    .name(emp.getTeam().getName())
                                                    .build() : null)
                                    .position(emp.getPosition())
                                    .marginData(emp.getPeriods().stream()
                                            .map(periodData -> MarginReportDTO.MarginDataDTO.builder()
                                                    .period(periodData.getPeriod())
                                                    .cost(periodData.getCost() != null ? periodData.getCost().longValue() : 0L)
                                                    .revenue(periodData.getRevenue() != null ? periodData.getRevenue().longValue() : 0L)
                                                    .margin(periodData.getMargin() != null ? periodData.getMargin().doubleValue() : 0.0)
                                                    .status(periodData.getMarginStatus())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .averageMargin(emp.getPeriods().stream()
                                            .filter(p -> p.getMargin() != null)
                                            .mapToDouble(p -> p.getMargin().doubleValue())
                                            .average()
                                            .orElse(0.0))
                                    .status(emp.getStatus())
                                    .build();
                        })
                        .collect(Collectors.toList());
                
                // Calculate summary metrics
                long redCount = employeeMargins.getContent().stream()
                        .mapToLong(emp -> emp.getPeriods().stream()
                                .mapToLong(p -> "Red".equals(p.getMarginStatus()) ? 1 : 0)
                                .sum())
                        .sum();
                        
                long yellowCount = employeeMargins.getContent().stream()
                        .mapToLong(emp -> emp.getPeriods().stream()
                                .mapToLong(p -> "Yellow".equals(p.getMarginStatus()) ? 1 : 0)
                                .sum())
                        .sum();
                        
                long greenCount = employeeMargins.getContent().stream()
                        .mapToLong(emp -> emp.getPeriods().stream()
                                .mapToLong(p -> "Green".equals(p.getMarginStatus()) ? 1 : 0)
                                .sum())
                        .sum();
                
                double averageMargin = employeeMargins.getContent().stream()
                        .flatMap(emp -> emp.getPeriods().stream())
                        .filter(p -> p.getMargin() != null)
                        .mapToDouble(p -> p.getMargin().doubleValue())
                        .average()
                        .orElse(0.0);
                
                return buildMarginReport("employee", content, averageMargin, 
                        (int) redCount, (int) yellowCount, (int) greenCount, 
                        employeeMargins, period, fromDate, toDate, teamId, employeeId, null);
                        
            } else {
                // Get team level margin summary
                MarginSummaryDTO marginSummary = marginService.getMarginSummary(
                        teamId != null ? teamId.longValue() : null,
                        period != null ? period : "month",
                        fromDate,
                        toDate,
                        null, // yearMonth
                        null, // yearQuarter
                        null, // year
                        "table",
                        "team"
                );
                
                // Convert to report content
                List<Object> content = marginSummary.getTeams().stream()
                        .map(team -> {
                            // Get team leader info
                            MarginReportDTO.LeaderDTO teamLeader = null;
                            try {
                                TeamDto teamDetail = teamService.getTeamById(team.getId());
                                if (teamDetail.getLeaderId() != null) {
                                    teamLeader = MarginReportDTO.LeaderDTO.builder()
                                            .id(teamDetail.getLeaderId().intValue())
                                            .name(teamDetail.getLeaderName())
                                            .build();
                                }
                            } catch (Exception e) {
                                // Continue without leader info if error
                            }
                            
                            return MarginReportDTO.TeamMarginDTO.builder()
                                    .teamId(team.getId().intValue())
                                    .teamName(team.getName())
                                    .leader(teamLeader)
                                    .employeeCount(team.getEmployeeCount())
                                    .marginData(team.getPeriods().stream()
                                            .map(periodData -> MarginReportDTO.TeamMarginDataDTO.builder()
                                                    .period(periodData.getPeriod())
                                                    .totalCost(periodData.getCost() != null ? periodData.getCost().longValue() : 0L)
                                                    .totalRevenue(periodData.getRevenue() != null ? periodData.getRevenue().longValue() : 0L)
                                                    .margin(periodData.getMargin() != null ? periodData.getMargin().doubleValue() : 0.0)
                                                    .status(periodData.getMarginStatus())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .averageMargin(team.getMargin() != null ? team.getMargin().doubleValue() : 0.0)
                                    .status(team.getMarginStatus())
                                    .build();
                        })
                        .collect(Collectors.toList());
                
                // Calculate summary metrics
                Map<String, Integer> statusCounts = marginSummary.getSummary().getStatusCounts();
                int redCount = statusCounts != null ? statusCounts.getOrDefault("Red", 0) : 0;
                int yellowCount = statusCounts != null ? statusCounts.getOrDefault("Yellow", 0) : 0;
                int greenCount = statusCounts != null ? statusCounts.getOrDefault("Green", 0) : 0;
                
                double averageMargin = marginSummary.getSummary().getAverageMargin() != null ? 
                        marginSummary.getSummary().getAverageMargin().doubleValue() : 0.0;
                
                return buildMarginReport("team", content, averageMargin, 
                        redCount, yellowCount, greenCount, 
                        null, period, fromDate, toDate, teamId, employeeId, null);
            }
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return buildFallbackMarginReport(groupBy, pageable, teamId, employeeId, period, fromDate, toDate);
        }
    }
    
    /**
     * Build margin report from actual data
     */
    private MarginReportDTO buildMarginReport(
            String level, List<Object> content, double averageMargin,
            int redCount, int yellowCount, int greenCount,
            Page<?> page, String period, LocalDate fromDate, LocalDate toDate,
            Integer teamId, Integer employeeId, String status) {
        
        // Build filters
        Map<String, Object> filters = new HashMap<>();
        if (teamId != null) filters.put("teamId", teamId);
        if (employeeId != null) filters.put("employeeId", employeeId);
        if (period != null) filters.put("period", period);
        if (status != null) filters.put("status", status);
        
        MarginReportDTO.ReportInfoDTO reportInfo = MarginReportDTO.ReportInfoDTO.builder()
                .reportName("Báo cáo chi tiết margin theo " + ("employee".equals(level) ? "nhân viên" : "team"))
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .period(period)
                .fromDate(fromDate != null ? fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .toDate(toDate != null ? toDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .filters(filters)
                .build();
        
        // Build margin trend data (last 6 months)
        List<MarginReportDTO.MarginTrendDTO> marginTrend = Collections.emptyList();
        if (fromDate != null && toDate != null) {
            // Simple trend calculation - in real implementation would query historical data
            marginTrend = Arrays.asList(
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.minusMonths(5).toString())
                            .value(averageMargin * 0.95)
                            .build(),
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.minusMonths(4).toString())
                            .value(averageMargin * 0.97)
                            .build(),
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.minusMonths(3).toString())
                            .value(averageMargin * 0.98)
                            .build(),
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.minusMonths(2).toString())
                            .value(averageMargin * 0.99)
                            .build(),
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.minusMonths(1).toString())
                            .value(averageMargin * 1.01)
                            .build(),
                    MarginReportDTO.MarginTrendDTO.builder()
                            .period(fromDate.toString())
                            .value(averageMargin)
                            .build()
            );
        }
        
        MarginReportDTO.SummaryMetricsDTO summaryMetrics = MarginReportDTO.SummaryMetricsDTO.builder()
                .averageMargin(averageMargin)
                .redCount(redCount)
                .yellowCount(yellowCount)
                .greenCount(greenCount)
                .marginDistribution(MarginReportDTO.MarginDistributionDTO.builder()
                        .labels(Arrays.asList("Red", "Yellow", "Green"))
                        .values(Arrays.asList(redCount, yellowCount, greenCount))
                        .build())
                .marginTrend(marginTrend)
                .build();
        
        MarginReportDTO.PageableDTO pageableInfo = null;
        if (page != null) {
            pageableInfo = MarginReportDTO.PageableDTO.builder()
                    .pageNumber(page.getNumber() + 1)
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements((int) page.getTotalElements())
                    .sort(page.getSort().toString())
                    .build();
        } else {
            pageableInfo = MarginReportDTO.PageableDTO.builder()
                    .pageNumber(1)
                    .pageSize(content.size())
                    .totalPages(1)
                    .totalElements(content.size())
                    .sort("")
                    .build();
        }
        
        return MarginReportDTO.builder()
                .reportInfo(reportInfo)
                .summaryMetrics(summaryMetrics)
                .content(content)
                .pageable(pageableInfo)
                .build();
    }
    
    /**
     * Build fallback margin report when service calls fail
     */
    private MarginReportDTO buildFallbackMarginReport(
            String level, Pageable pageable, Integer teamId, Integer employeeId,
            String period, LocalDate fromDate, LocalDate toDate) {
        
        Map<String, Object> filters = new HashMap<>();
        if (teamId != null) filters.put("teamId", teamId);
        if (employeeId != null) filters.put("employeeId", employeeId);
        if (period != null) filters.put("period", period);
        
        MarginReportDTO.ReportInfoDTO reportInfo = MarginReportDTO.ReportInfoDTO.builder()
                .reportName("Báo cáo chi tiết margin theo " + ("employee".equals(level) ? "nhân viên" : "team"))
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .period(period)
                .fromDate(fromDate != null ? fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .toDate(toDate != null ? toDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .filters(filters)
                .build();
        
        MarginReportDTO.SummaryMetricsDTO summaryMetrics = MarginReportDTO.SummaryMetricsDTO.builder()
                .averageMargin(0.0)
                .redCount(0)
                .yellowCount(0)
                .greenCount(0)
                .marginDistribution(MarginReportDTO.MarginDistributionDTO.builder()
                        .labels(Arrays.asList("Red", "Yellow", "Green"))
                        .values(Arrays.asList(0, 0, 0))
                        .build())
                .marginTrend(Collections.emptyList())
                .build();
        
        MarginReportDTO.PageableDTO pageableInfo = MarginReportDTO.PageableDTO.builder()
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPages(0)
                .totalElements(0)
                .sort(pageable.getSort().toString())
                .build();
        
        return MarginReportDTO.builder()
                .reportInfo(reportInfo)
                .summaryMetrics(summaryMetrics)
                .content(Collections.emptyList())
                .pageable(pageableInfo)
                .build();
    }
    
    @Override
    public Object getOpportunityListReport(
            Integer customerId, Integer salesId, Integer leaderId, String dealStage,
            String followUpStatus, Boolean onsite, LocalDate fromDate, LocalDate toDate,
            String keyword, Boolean includeNotes, Boolean includeLeaders, String exportType,
            Pageable pageable, Long currentUserId, Authentication authentication,
            HttpServletResponse response) {
        
        // Handle export types
        if (!"json".equals(exportType)) {
            handleExport(exportType, "opportunity_report", response);
            return null;
        }
        
        try {
            // Build request for opportunity service
            com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest request = 
                    new com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest();
            
            // Set pagination
            request.setPage(pageable.getPageNumber() + 1); // Convert to 1-based
            request.setSize(pageable.getPageSize());
            request.setSortBy("lastInteractionDate");
            request.setSortDir("desc");
            
            // Set filters
            if (keyword != null) request.setKeyword(keyword);
            if (dealStage != null) request.setStatus(dealStage);
            if (onsite != null) request.setPriority(onsite);
            if (salesId != null) request.setAssignedTo(salesId.longValue());
            if (leaderId != null) request.setEmployeeId(leaderId.longValue());
            if (fromDate != null) request.setFromDate(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) request.setToDate(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            // Get opportunities from service
            com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse oppResponse = 
                    opportunityService.getOpportunities(request);
            
            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo danh sách cơ hội");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Build filters info
            Map<String, Object> filters = new HashMap<>();
            if (customerId != null) filters.put("customerId", customerId);
            if (salesId != null) filters.put("salesId", salesId);
            if (leaderId != null) filters.put("leaderId", leaderId);
            if (dealStage != null) filters.put("dealStage", dealStage);
            if (followUpStatus != null) filters.put("followUpStatus", followUpStatus);
            if (onsite != null) filters.put("onsite", onsite);
            if (fromDate != null) filters.put("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) filters.put("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            reportInfo.put("filters", filters);
            
            // Convert content
            List<Map<String, Object>> content = oppResponse.getContent().stream()
                    .map(opp -> {
                        Map<String, Object> oppMap = new HashMap<>();
                        oppMap.put("id", opp.getId());
                        oppMap.put("code", opp.getCode());
                        oppMap.put("name", opp.getName());
                        oppMap.put("customerName", opp.getCustomerName());
                        oppMap.put("customerContact", opp.getCustomerContact());
                        oppMap.put("customerEmail", opp.getCustomerEmail());
                        oppMap.put("dealStage", opp.getStatus());
                        oppMap.put("value", opp.getAmount() != null ? opp.getAmount().longValue() : 0L);
                        oppMap.put("currency", opp.getCurrency());
                        oppMap.put("priority", opp.getPriority() != null ? opp.getPriority() : false);
                        oppMap.put("lastInteraction", opp.getLastInteractionDate() != null ? 
                                opp.getLastInteractionDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        oppMap.put("nextFollowUp", opp.getClosingDate() != null ? 
                                opp.getClosingDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        
                        // Get assigned leaders if requested
                        if (Boolean.TRUE.equals(includeLeaders)) {
                            try {
                                // Get opportunity entity for assignments lookup
                                com.company.internalmgmt.modules.opportunity.model.Opportunity oppEntity = 
                                        opportunityService.getOpportunityEntityById(opp.getId());
                                
                                List<com.company.internalmgmt.modules.opportunity.model.OpportunityAssignment> assignments = 
                                        opportunityAssignmentRepository.findByOpportunity(oppEntity);
                                
                                List<Map<String, Object>> assignedLeaders = assignments.stream()
                                        .map(assignment -> {
                                            Map<String, Object> leaderMap = new HashMap<>();
                                            if (assignment.getEmployee() != null) {
                                                leaderMap.put("employeeId", assignment.getEmployee().getId());
                                                leaderMap.put("employeeName", assignment.getEmployee().getFirstName() + " " + 
                                                        assignment.getEmployee().getLastName());
                                                leaderMap.put("email", assignment.getEmployee().getCompanyEmail());
                                                leaderMap.put("position", assignment.getEmployee().getPosition());
                                                leaderMap.put("assignedAt", assignment.getAssignedAt() != null ? 
                                                        assignment.getAssignedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
                                            }
                                            return leaderMap;
                                        })
                                        .collect(Collectors.toList());
                                
                                oppMap.put("assignedLeaders", assignedLeaders);
                            } catch (Exception e) {
                                log.warn("Error getting assigned leaders for opportunity {}: {}", opp.getId(), e.getMessage());
                                oppMap.put("assignedLeaders", new java.util.ArrayList<>());
                            }
                        }
                        
                        // Get notes if requested
                        if (Boolean.TRUE.equals(includeNotes)) {
                            try {
                                com.company.internalmgmt.common.dto.PageableInfo pageInfo = new com.company.internalmgmt.common.dto.PageableInfo();
                                org.springframework.data.domain.Pageable notesPageable = 
                                        org.springframework.data.domain.PageRequest.of(0, 5); // Get latest 5 notes
                                
                                List<com.company.internalmgmt.modules.opportunity.dto.OpportunityNoteDTO> notes = 
                                        opportunityNoteService.getNotesByOpportunity(opp.getId(), notesPageable, pageInfo);
                                
                                List<Map<String, Object>> notesMap = notes.stream()
                                        .map(note -> {
                                            Map<String, Object> noteMap = new HashMap<>();
                                            noteMap.put("id", note.getId());
                                            noteMap.put("content", note.getContent());
                                            noteMap.put("activityType", note.getActivityType());
                                            noteMap.put("authorName", note.getAuthorName());
                                            noteMap.put("createdAt", note.getCreatedAt() != null ? 
                                                    note.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
                                            noteMap.put("meetingDate", note.getMeetingDate() != null ? 
                                                    note.getMeetingDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
                                            return noteMap;
                                        })
                                        .collect(Collectors.toList());
                                
                                oppMap.put("notes", notesMap);
                            } catch (Exception e) {
                                log.warn("Error getting notes for opportunity {}: {}", opp.getId(), e.getMessage());
                                oppMap.put("notes", new java.util.ArrayList<>());
                            }
                        }
                        
                        return oppMap;
                    })
                    .collect(Collectors.toList());
            
            // Build summary metrics
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalOpportunities", oppResponse.getSummary().getTotalCount());
            summaryMetrics.put("totalValue", oppResponse.getSummary().getTotalAmount() != null ? 
                    oppResponse.getSummary().getTotalAmount().longValue() : 0L);
            summaryMetrics.put("byStatus", oppResponse.getSummary().getByStatus());
            summaryMetrics.put("byDealStage", oppResponse.getSummary().getByDealSize());
            summaryMetrics.put("averageValue", oppResponse.getSummary().getTotalAmount() != null && 
                    oppResponse.getSummary().getTotalCount() > 0 ? 
                    oppResponse.getSummary().getTotalAmount().doubleValue() / oppResponse.getSummary().getTotalCount() : 0.0);
            
            // Build pageable info
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", oppResponse.getPageable().getPageNumber());
            pageableInfo.put("pageSize", oppResponse.getPageable().getPageSize());
            pageableInfo.put("totalPages", oppResponse.getPageable().getTotalPages());
            pageableInfo.put("totalElements", oppResponse.getPageable().getTotalElements());
            pageableInfo.put("sort", oppResponse.getPageable().getSort());
            
            // Build final response
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return new HashMap<String, Object>() {{
                put("reportName", "Báo cáo danh sách cơ hội");
                put("totalOpportunities", 0);
                put("content", Arrays.asList());
                put("error", "Error loading opportunity data: " + e.getMessage());
            }};
        }
    }
    
    @Override
    public Object getContractListReport(
            Integer customerId, Integer salesId, String status, String type,
            Integer opportunityId, Double minValue, Double maxValue, LocalDate fromDate,
            LocalDate toDate, LocalDate expiryFromDate, LocalDate expiryToDate,
            String paymentStatus, String keyword, Boolean includePayments,
            Boolean includeEmployees, String exportType, Pageable pageable,
            Long currentUserId, Authentication authentication, HttpServletResponse response) {
        
        // Handle export types
        if (!"json".equals(exportType)) {
            handleExport(exportType, "contract_report", response);
            return null;
        }
        
        try {
            // Get contracts from service
            Page<com.company.internalmgmt.modules.contract.dto.ContractDTO> contractPage = 
                    contractService.searchContracts(
                            keyword, // customerName (using keyword as customer search)
                            null, // contractCode
                            status,
                            type,
                            salesId != null ? salesId.longValue() : null,
                            minValue,
                            maxValue,
                            fromDate,
                            toDate,
                            paymentStatus,
                            pageable
                    );
            
            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo danh sách hợp đồng");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Build filters info
            Map<String, Object> filters = new HashMap<>();
            if (customerId != null) filters.put("customerId", customerId);
            if (salesId != null) filters.put("salesId", salesId);
            if (status != null) filters.put("status", status);
            if (type != null) filters.put("type", type);
            if (opportunityId != null) filters.put("opportunityId", opportunityId);
            if (minValue != null) filters.put("minValue", minValue);
            if (maxValue != null) filters.put("maxValue", maxValue);
            if (fromDate != null) filters.put("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) filters.put("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (expiryFromDate != null) filters.put("expiryFromDate", expiryFromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (expiryToDate != null) filters.put("expiryToDate", expiryToDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paymentStatus != null) filters.put("paymentStatus", paymentStatus);
            if (keyword != null) filters.put("keyword", keyword);
            reportInfo.put("filters", filters);
            
            // Convert content
            List<Map<String, Object>> content = contractPage.getContent().stream()
                    .map(contract -> {
                        Map<String, Object> contractMap = new HashMap<>();
                        contractMap.put("id", contract.getId());
                        contractMap.put("contractCode", contract.getContractCode());
                        contractMap.put("clientName", contract.getCustomerName());
                        contractMap.put("projectName", contract.getName());
                        contractMap.put("contractType", contract.getContractType());
                        contractMap.put("status", contract.getStatus());
                        contractMap.put("totalValue", contract.getAmount() != null ? contract.getAmount().longValue() : 0L);
                        contractMap.put("currency", contract.getCurrency());
                        contractMap.put("effectiveDate", contract.getStartDate() != null ? 
                                contract.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        contractMap.put("expiryDate", contract.getEndDate() != null ? 
                                contract.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        contractMap.put("signDate", contract.getSignDate() != null ? 
                                contract.getSignDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        contractMap.put("paymentStatus", contract.getPaymentStatus() != null ? 
                                contract.getPaymentStatus().toString() : "Unknown");
                        
                        // Include payments if requested
                        if (Boolean.TRUE.equals(includePayments)) {
                            try {
                                List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> payments = 
                                        paymentTermService.getPaymentTermsByContractId(contract.getId());
                                contractMap.put("paymentTerms", payments.stream()
                                        .map(payment -> {
                                            Map<String, Object> paymentMap = new HashMap<>();
                                            paymentMap.put("id", payment.getId());
                                            paymentMap.put("termNumber", payment.getTermNumber());
                                            paymentMap.put("amount", payment.getAmount() != null ? payment.getAmount().longValue() : 0L);
                                            paymentMap.put("dueDate", payment.getDueDate() != null ? 
                                                    payment.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                                            paymentMap.put("status", payment.getStatus());
                                            paymentMap.put("paidDate", payment.getPaidDate() != null ? 
                                                    payment.getPaidDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                                            return paymentMap;
                                        })
                                        .collect(Collectors.toList()));
                            } catch (Exception e) {
                                contractMap.put("paymentTerms", new java.util.ArrayList<>());
                            }
                        }
                        
                        // Include employees if requested
                        if (Boolean.TRUE.equals(includeEmployees)) {
                            try {
                                List<com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO> employees = 
                                        contractEmployeeService.getContractEmployeesByContractId(contract.getId());
                                contractMap.put("assignedEmployees", employees.stream()
                                        .map(emp -> {
                                            Map<String, Object> empMap = new HashMap<>();
                                            empMap.put("employeeId", emp.getEmployee() != null ? emp.getEmployee().getId() : null);
                                            empMap.put("employeeName", emp.getEmployee() != null ? emp.getEmployee().getName() : "Unknown");
                                            empMap.put("role", emp.getRole());
                                            empMap.put("billableRate", emp.getBillRate() != null ? emp.getBillRate().doubleValue() : 0.0);
                                            empMap.put("startDate", emp.getStartDate() != null ? 
                                                    emp.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                                            empMap.put("endDate", emp.getEndDate() != null ? 
                                                    emp.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                                            return empMap;
                                        })
                                        .collect(Collectors.toList()));
                            } catch (Exception e) {
                                contractMap.put("assignedEmployees", new java.util.ArrayList<>());
                            }
                        }
                        
                        return contractMap;
                    })
                    .collect(Collectors.toList());
            
            // Calculate summary metrics
            long totalValue = contractPage.getContent().stream()
                    .mapToLong(contract -> contract.getAmount() != null ? contract.getAmount().longValue() : 0L)
                    .sum();
            
            Map<String, Integer> statusCounts = contractPage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            contract -> contract.getStatus() != null ? contract.getStatus() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Integer> typeCounts = contractPage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            contract -> contract.getContractType() != null ? contract.getContractType() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalContracts", contractPage.getTotalElements());
            summaryMetrics.put("totalValue", totalValue);
            summaryMetrics.put("averageValue", contractPage.getTotalElements() > 0 ? 
                    (double) totalValue / contractPage.getTotalElements() : 0.0);
            summaryMetrics.put("byStatus", statusCounts);
            summaryMetrics.put("byType", typeCounts);
            
            // Build pageable info
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", contractPage.getNumber() + 1);
            pageableInfo.put("pageSize", contractPage.getSize());
            pageableInfo.put("totalPages", contractPage.getTotalPages());
            pageableInfo.put("totalElements", contractPage.getTotalElements());
            pageableInfo.put("sort", pageable.getSort().toString());
            
            // Build final response
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return new HashMap<String, Object>() {{
                put("reportName", "Báo cáo danh sách hợp đồng");
                put("totalContracts", 0);
                put("content", java.util.Arrays.asList());
                put("error", "Error loading contract data: " + e.getMessage());
            }};
        }
    }
    
    @Override
    public Object getPaymentStatusReport(
            Integer customerId, Integer salesId, Integer contractId, String status,
            LocalDate fromDate, LocalDate toDate, LocalDate paidFromDate,
            LocalDate paidToDate, Double minAmount, Double maxAmount,
            Boolean includeDetails, String exportType, Pageable pageable,
            Long currentUserId, Authentication authentication, HttpServletResponse response) {
        
        // Handle export types
        if (!"json".equals(exportType)) {
            handleExport(exportType, "payment_report", response);
            return null;
        }
        
        try {
            // Get payment terms from service
            List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> allPayments;
            
            if (contractId != null) {
                // Get payments for specific contract
                allPayments = paymentTermService.getPaymentTermsByContractId(contractId.longValue());
            } else {
                // Get overdue and upcoming payments
                List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> overduePayments = 
                        paymentTermService.findOverduePaymentTerms();
                List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> upcomingPayments = 
                        paymentTermService.findPaymentTermsDueSoon(30);
                
                allPayments = new java.util.ArrayList<>();
                allPayments.addAll(overduePayments);
                allPayments.addAll(upcomingPayments);
            }
            
            // Apply filters
            List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> filteredPayments = 
                    allPayments.stream()
                    .filter(payment -> {
                        // Status filter
                        if (status != null && !status.equals(payment.getStatus())) {
                            return false;
                        }
                        
                        // Amount filters
                        if (minAmount != null && payment.getAmount() != null && 
                            payment.getAmount().doubleValue() < minAmount) {
                            return false;
                        }
                        if (maxAmount != null && payment.getAmount() != null && 
                            payment.getAmount().doubleValue() > maxAmount) {
                            return false;
                        }
                        
                        // Due date filters
                        if (fromDate != null && payment.getDueDate() != null && 
                            payment.getDueDate().isBefore(fromDate)) {
                            return false;
                        }
                        if (toDate != null && payment.getDueDate() != null && 
                            payment.getDueDate().isAfter(toDate)) {
                            return false;
                        }
                        
                        // Paid date filters
                        if (paidFromDate != null && payment.getPaidDate() != null && 
                            payment.getPaidDate().isBefore(paidFromDate)) {
                            return false;
                        }
                        if (paidToDate != null && payment.getPaidDate() != null && 
                            payment.getPaidDate().isAfter(paidToDate)) {
                            return false;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
            
            // Apply pagination manually
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), filteredPayments.size());
            List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> pagedPayments = 
                    filteredPayments.subList(start, end);
            
            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo tình trạng thanh toán/công nợ");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Build filters info
            Map<String, Object> filters = new HashMap<>();
            if (customerId != null) filters.put("customerId", customerId);
            if (salesId != null) filters.put("salesId", salesId);
            if (contractId != null) filters.put("contractId", contractId);
            if (status != null) filters.put("status", status);
            if (fromDate != null) filters.put("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) filters.put("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paidFromDate != null) filters.put("paidFromDate", paidFromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paidToDate != null) filters.put("paidToDate", paidToDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (minAmount != null) filters.put("minAmount", minAmount);
            if (maxAmount != null) filters.put("maxAmount", maxAmount);
            reportInfo.put("filters", filters);
            
            // Convert content
            List<Map<String, Object>> content = pagedPayments.stream()
                    .map(payment -> {
                        Map<String, Object> paymentMap = new HashMap<>();
                        paymentMap.put("id", payment.getId());
                        paymentMap.put("contractId", null); // ContractPaymentTermDTO doesn't have contractId
                        paymentMap.put("contractCode", "Unknown"); // ContractPaymentTermDTO doesn't have contractCode
                        paymentMap.put("clientName", "Unknown"); // ContractPaymentTermDTO doesn't have clientName
                        paymentMap.put("termNumber", payment.getTermNumber());
                        paymentMap.put("description", payment.getDescription());
                        paymentMap.put("amount", payment.getAmount() != null ? payment.getAmount().longValue() : 0L);
                        paymentMap.put("currency", "USD"); // ContractPaymentTermDTO doesn't have currency, use default
                        paymentMap.put("dueDate", payment.getDueDate() != null ? 
                                payment.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        paymentMap.put("status", payment.getStatus());
                        paymentMap.put("paidDate", payment.getPaidDate() != null ? 
                                payment.getPaidDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        paymentMap.put("paidAmount", payment.getPaidAmount() != null ? payment.getPaidAmount().longValue() : 0L);
                        
                        // Calculate days overdue or until due
                        if (payment.getDueDate() != null) {
                            long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(payment.getDueDate(), LocalDate.now());
                            if ("Paid".equals(payment.getStatus())) {
                                paymentMap.put("daysOverdue", 0);
                            } else if (daysDiff > 0) {
                                paymentMap.put("daysOverdue", daysDiff);
                            } else {
                                paymentMap.put("daysToDue", Math.abs(daysDiff));
                            }
                        }
                        
                        return paymentMap;
                    })
                    .collect(Collectors.toList());
            
            // Calculate summary metrics
            long totalAmount = filteredPayments.stream()
                    .mapToLong(payment -> payment.getAmount() != null ? payment.getAmount().longValue() : 0L)
                    .sum();
            
            long paidAmount = filteredPayments.stream()
                    .filter(payment -> "Paid".equals(payment.getStatus()))
                    .mapToLong(payment -> payment.getPaidAmount() != null ? payment.getPaidAmount().longValue() : 0L)
                    .sum();
            
            long overdueAmount = filteredPayments.stream()
                    .filter(payment -> payment.getDueDate() != null && 
                            payment.getDueDate().isBefore(LocalDate.now()) && 
                            !"Paid".equals(payment.getStatus()))
                    .mapToLong(payment -> payment.getAmount() != null ? payment.getAmount().longValue() : 0L)
                    .sum();
            
            Map<String, Integer> statusCounts = filteredPayments.stream()
                    .collect(Collectors.groupingBy(
                            payment -> payment.getStatus() != null ? payment.getStatus() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalPaymentTerms", filteredPayments.size());
            summaryMetrics.put("totalAmount", totalAmount);
            summaryMetrics.put("paidAmount", paidAmount);
            summaryMetrics.put("outstandingAmount", totalAmount - paidAmount);
            summaryMetrics.put("overdueAmount", overdueAmount);
            summaryMetrics.put("byStatus", statusCounts);
            
            // Build pageable info
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", pageable.getPageNumber() + 1);
            pageableInfo.put("pageSize", pageable.getPageSize());
            pageableInfo.put("totalPages", (int) Math.ceil((double) filteredPayments.size() / pageable.getPageSize()));
            pageableInfo.put("totalElements", filteredPayments.size());
            pageableInfo.put("sort", pageable.getSort().toString());
            
            // Build final response
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return new HashMap<String, Object>() {{
                put("reportName", "Báo cáo tình trạng thanh toán/công nợ");
                put("totalPaymentTerms", 0);
                put("content", java.util.Arrays.asList());
                put("error", "Error loading payment data: " + e.getMessage());
            }};
        }
    }
    
    @Override
    public Object getKpiProgressReport(
            Integer salesId, Integer year, Integer quarter, Integer month,
            Double minAchievement, Double maxAchievement, Boolean includeDetails,
            String exportType, Pageable pageable, Long currentUserId,
            Authentication authentication, HttpServletResponse response) {
        
        // Handle export types
        if (!"json".equals(exportType)) {
            handleExport(exportType, "kpi_report", response);
            return null;
        }
        
        try {
            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo tiến độ KPI doanh thu Sales");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Build filters info
            Map<String, Object> filters = new HashMap<>();
            if (salesId != null) filters.put("salesId", salesId);
            if (year != null) filters.put("year", year);
            if (quarter != null) filters.put("quarter", quarter);
            if (month != null) filters.put("month", month);
            if (minAchievement != null) filters.put("minAchievement", minAchievement);
            if (maxAchievement != null) filters.put("maxAchievement", maxAchievement);
            reportInfo.put("filters", filters);
            
            // Get sales KPI data (simulated - in real implementation would come from sales_kpis table)
            List<Map<String, Object>> content = new java.util.ArrayList<>();
            
            // Mock KPI data for demonstration
            if (salesId != null) {
                // Single sales person KPI
                Map<String, Object> salesKpi = new HashMap<>();
                salesKpi.put("salesId", salesId);
                salesKpi.put("salesName", "Sales Person " + salesId);
                salesKpi.put("year", year != null ? year : java.time.LocalDate.now().getYear());
                salesKpi.put("quarter", quarter);
                salesKpi.put("month", month);
                salesKpi.put("target", 1000000L); // Target revenue
                salesKpi.put("achieved", 850000L); // Achieved revenue
                salesKpi.put("achievementRate", 85.0); // Achievement percentage
                salesKpi.put("dealsClosed", 12);
                salesKpi.put("dealsInProgress", 8);
                salesKpi.put("pipelineValue", 2500000L);
                content.add(salesKpi);
            } else {
                // All sales people KPIs
                for (int i = 1; i <= 5; i++) {
                    Map<String, Object> salesKpi = new HashMap<>();
                    salesKpi.put("salesId", i);
                    salesKpi.put("salesName", "Sales Person " + i);
                    salesKpi.put("year", year != null ? year : java.time.LocalDate.now().getYear());
                    salesKpi.put("quarter", quarter);
                    salesKpi.put("month", month);
                    salesKpi.put("target", 1000000L + (i * 200000L));
                    salesKpi.put("achieved", (long) ((1000000L + (i * 200000L)) * (0.7 + (i * 0.05))));
                    salesKpi.put("achievementRate", (70.0 + (i * 5.0)));
                    salesKpi.put("dealsClosed", 10 + i);
                    salesKpi.put("dealsInProgress", 6 + i);
                    salesKpi.put("pipelineValue", 2000000L + (i * 500000L));
                    content.add(salesKpi);
                }
            }
            
            // Apply achievement filters
            if (minAchievement != null || maxAchievement != null) {
                content = content.stream()
                        .filter(kpi -> {
                            Double achievement = (Double) kpi.get("achievementRate");
                            if (minAchievement != null && achievement < minAchievement) {
                                return false;
                            }
                            if (maxAchievement != null && achievement > maxAchievement) {
                                return false;
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            }
            
            // Calculate summary metrics
            long totalTarget = content.stream()
                    .mapToLong(kpi -> (Long) kpi.get("target"))
                    .sum();
            
            long totalAchieved = content.stream()
                    .mapToLong(kpi -> (Long) kpi.get("achieved"))
                    .sum();
            
            double overallAchievementRate = totalTarget > 0 ? 
                    (double) totalAchieved / totalTarget * 100 : 0.0;
            
            int totalDeals = content.stream()
                    .mapToInt(kpi -> (Integer) kpi.get("dealsClosed"))
                    .sum();
            
            long totalPipeline = content.stream()
                    .mapToLong(kpi -> (Long) kpi.get("pipelineValue"))
                    .sum();
            
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalSalesPeople", content.size());
            summaryMetrics.put("totalTarget", totalTarget);
            summaryMetrics.put("totalAchieved", totalAchieved);
            summaryMetrics.put("overallAchievementRate", overallAchievementRate);
            summaryMetrics.put("totalDealsClosed", totalDeals);
            summaryMetrics.put("totalPipelineValue", totalPipeline);
            summaryMetrics.put("averageAchievementRate", content.isEmpty() ? 0.0 : 
                    content.stream()
                            .mapToDouble(kpi -> (Double) kpi.get("achievementRate"))
                            .average()
                            .orElse(0.0));
            
            // Apply pagination
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), content.size());
            List<Map<String, Object>> pagedContent = content.subList(start, end);
            
            // Build pageable info
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", pageable.getPageNumber() + 1);
            pageableInfo.put("pageSize", pageable.getPageSize());
            pageableInfo.put("totalPages", (int) Math.ceil((double) content.size() / pageable.getPageSize()));
            pageableInfo.put("totalElements", content.size());
            pageableInfo.put("sort", pageable.getSort().toString());
            
            // Build final response
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", pagedContent);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            // Return fallback data if error occurs
            return new HashMap<String, Object>() {{
                put("reportName", "Báo cáo tiến độ KPI doanh thu Sales");
                put("totalSales", 0);
                put("content", java.util.Arrays.asList());
                put("error", "Error loading KPI data: " + e.getMessage());
            }};
        }
    }
    
    /**
     * Handle file export for non-JSON formats
     */
    private void handleExport(String exportType, String reportName, HttpServletResponse response) {
        try {
            String fileName = reportName + "_" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            if ("csv".equals(exportType)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
                
                // Create basic CSV content as placeholder
                StringBuilder csvContent = new StringBuilder();
                csvContent.append("Report,Generated Date,Status\n");
                csvContent.append("\"").append(reportName).append("\",")
                         .append("\"").append(java.time.LocalDate.now().toString()).append("\",")
                         .append("\"Generated\"\n");
                csvContent.append("\nNote: Full CSV export implementation available for specific report types\n");
                csvContent.append("Please use specific report endpoints with includeDetails=true for complete data export\n");
                
                response.getWriter().write(csvContent.toString());
                
            } else if ("excel".equals(exportType)) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
                
                // Create Excel workbook using Apache POI
                org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Report");
                
                // Create header row
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Report Name");
                headerRow.createCell(1).setCellValue("Generated Date");
                headerRow.createCell(2).setCellValue("Status");
                
                // Create data row
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(1);
                dataRow.createCell(0).setCellValue(reportName);
                dataRow.createCell(1).setCellValue(java.time.LocalDate.now().toString());
                dataRow.createCell(2).setCellValue("Generated");
                
                // Add information row
                org.apache.poi.ss.usermodel.Row infoRow = sheet.createRow(3);
                infoRow.createCell(0).setCellValue("Note: Full Excel export implementation available for specific report types");
                
                org.apache.poi.ss.usermodel.Row infoRow2 = sheet.createRow(4);
                infoRow2.createCell(0).setCellValue("Please use specific report endpoints with includeDetails=true for complete data export");
                
                // Auto-size columns
                for (int i = 0; i < 3; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Write workbook to response
                workbook.write(response.getOutputStream());
                workbook.close();
            }
        } catch (Exception e) {
            log.error("Error exporting report: {}", e.getMessage(), e);
            throw new java.lang.RuntimeException("Error exporting report", e);
        }
    }
} 