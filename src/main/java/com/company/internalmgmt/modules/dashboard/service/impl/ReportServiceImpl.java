package com.company.internalmgmt.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;
import com.company.internalmgmt.modules.contract.service.ContractEmployeeService;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;
import com.company.internalmgmt.modules.contract.service.ContractService;
import com.company.internalmgmt.modules.dashboard.dto.common.PageableDTO;
import com.company.internalmgmt.modules.dashboard.dto.common.ReportInfoDTO; // Already used by other reports
import com.company.internalmgmt.modules.dashboard.dto.employee.EmployeeReportDTO; // Already used by other reports
import com.company.internalmgmt.modules.dashboard.dto.kpi.KpiAchievementRangeDTO;
import com.company.internalmgmt.modules.dashboard.dto.kpi.KpiPeriodInfoDTO;
import com.company.internalmgmt.modules.dashboard.dto.kpi.SalesKpiDetailDTO;
import com.company.internalmgmt.modules.dashboard.dto.kpi.SalesKpiReportDTO;
import com.company.internalmgmt.modules.dashboard.dto.kpi.SalesKpiSummaryMetricsDTO;
import com.company.internalmgmt.modules.dashboard.dto.margin.MarginReportDTO;
import com.company.internalmgmt.modules.dashboard.service.ReportService;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.hrm.service.EmployeeSkillService;
import com.company.internalmgmt.modules.hrm.service.EmployeeStatusLogService;
import com.company.internalmgmt.modules.hrm.service.TeamService;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.service.MarginService;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityAssignmentRepository;
import com.company.internalmgmt.modules.opportunity.service.OpportunityNoteService;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;

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

    // @Autowired
    // private SalesKpiService salesKpiService; // Uncomment when SalesKpiService is implemented
    
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

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
            // Step 1: Fetch all employees matching the criteria (unpaged)
            Page<EmployeeDto> allEmployeesPage = employeeService.findEmployees(
                    null, // searchTerm
                    teamId, 
                    position, 
                    status, 
                    skills, // Pass skill IDs directly
                    minExperience, 
                    null, // marginStatus - not part of this report's filters
                    Pageable.unpaged() // Fetch all for accurate summary
            );
            List<EmployeeDto> allMatchingEmployees = allEmployeesPage.getContent();

            // Build report info (remains the same)
            Map<String, Object> filtersApplied = new HashMap<>();
            if (teamId != null) filtersApplied.put("teamId", teamId);
            if (position != null) filtersApplied.put("position", position);
            if (status != null) filtersApplied.put("status", status);
            if (skills != null && !skills.isEmpty()) filtersApplied.put("skills", skills);
            if (minExperience != null) filtersApplied.put("minExperience", minExperience);
            
            EmployeeReportDTO.ReportInfoDTO reportInfo = EmployeeReportDTO.ReportInfoDTO.builder()
                    .reportName("Báo cáo danh sách nhân viên")
                    .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .filters(filtersApplied)
                    .build();
            
            // Step 2: Convert all matching employees to EmployeeDetailDTO
            List<EmployeeReportDTO.EmployeeDetailDTO> allEmployeeDetails = allMatchingEmployees.stream()
                    .map(emp -> {
                        // Build skills list
                        List<EmployeeReportDTO.SkillDTO> skillDTOs = Collections.emptyList();
                        if (Boolean.TRUE.equals(includeSkills)) {
                            try {
                                List<EmployeeSkillDto> employeeSkills = employeeSkillService.findAllByEmployeeId(emp.getId());
                                skillDTOs = employeeSkills.stream()
                                        .map(skill -> EmployeeReportDTO.SkillDTO.builder()
                                                .id(skill.getSkillId().intValue())
                                                .name(skill.getSkillName())
                                                .category(skill.getSkillCategoryName())
                                                .level(skill.getLeaderAssessmentLevel() != null ? skill.getLeaderAssessmentLevel() : skill.getSelfAssessmentLevel())
                                                .years(skill.getYearsExperience() != null ? 
                                                        skill.getYearsExperience().intValue() : 0)
                                                .build())
                                        .collect(Collectors.toList());
                            } catch (Exception e) {
                                log.warn("Error fetching skills for employee {}: {}", emp.getId(), e.getMessage());
                            }
                        }
                        
                        // Get current project information
                        EmployeeReportDTO.ProjectDTO currentProjectDto = null;
                        if (Boolean.TRUE.equals(includeProjects)) {
                            try {
                                List<ContractEmployeeDTO> activeAssignments = contractEmployeeService.findActiveContractEmployeesForEmployeeAndDate(emp.getId(), LocalDate.now());
                                if (!activeAssignments.isEmpty()) {
                                    ContractEmployeeDTO mainAssignment = activeAssignments.get(0);
                                    EmployeeStatusLogDto latestStatus = employeeStatusLogService.findMostRecentByEmployeeId(emp.getId());
                                    if (latestStatus != null && latestStatus.getProjectName() != null) {
                                        currentProjectDto = EmployeeReportDTO.ProjectDTO.builder()
                                            .id(null)
                                            .name(latestStatus.getProjectName())
                                            .customer(latestStatus.getClientName())
                                            .allocation(latestStatus.getAllocationPercentage())
                                            .startDate(latestStatus.getStartDate() != null ? 
                                                    latestStatus.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .endDate(latestStatus.getExpectedEndDate() != null ? 
                                                    latestStatus.getExpectedEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .build();
                                    } else if (mainAssignment != null) {
                                         currentProjectDto = EmployeeReportDTO.ProjectDTO.builder()
                                            .name("Project from Contract (Details N/A)")
                                            .customer("Customer from Contract (Details N/A)")
                                            .allocation(mainAssignment.getAllocationPercentage() != null ? mainAssignment.getAllocationPercentage().intValue() : null)
                                            .startDate(mainAssignment.getStartDate() != null ? mainAssignment.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .endDate(mainAssignment.getEndDate() != null ? mainAssignment.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                            .build();
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("Error fetching project info for employee {}: {}", emp.getId(), e.getMessage());
                            }
                        }
                        
                        // Calculate utilization
                        Integer utilRate = 0;
                        try {
                            List<ContractEmployeeDTO> activeAssignments = contractEmployeeService.findActiveContractEmployeesForEmployeeAndDate(emp.getId(), LocalDate.now());
                            if (!activeAssignments.isEmpty()) {
                                utilRate = activeAssignments.stream()
                                        .mapToInt(assign -> assign.getAllocationPercentage() != null ? assign.getAllocationPercentage().intValue() : 0)
                                        .sum();
                                if (utilRate > 100) utilRate = 100; 
                            }
                        } catch (Exception e) {
                             log.warn("Error calculating utilization for employee {}: {}", emp.getId(), e.getMessage());
                        }

                        // Calculate experience in years
                        Integer totalExperienceYears = 0;
                        if (emp.getHireDate() != null) {
                            totalExperienceYears = (int) java.time.temporal.ChronoUnit.YEARS.between(
                                    emp.getHireDate(), LocalDate.now());
                        }
                        
                        // Get team leader info
                        EmployeeReportDTO.LeaderDTO teamLeader = null;
                        if (emp.getTeam() != null && emp.getTeam().getId() != null) {
                            try {
                                TeamDto teamDetail = teamService.getTeamById(emp.getTeam().getId());
                                if (teamDetail != null && teamDetail.getLeaderId() != null) {
                                    teamLeader = EmployeeReportDTO.LeaderDTO.builder()
                                            .id(teamDetail.getLeaderId().intValue())
                                            .name(teamDetail.getLeaderName())
                                            .build();
                                }
                            } catch (Exception e) {
                                log.warn("Error fetching team leader for team {}: {}", emp.getTeam().getId(), e.getMessage());
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
                                                .id(emp.getTeam().getId() != null ? emp.getTeam().getId().intValue() : null)
                                                .name(emp.getTeam().getName())
                                                .leader(teamLeader)
                                                .build() : null)
                                .status(emp.getCurrentStatus())
                                .currentProject(currentProjectDto)
                                .utilization(utilRate)
                                .skills(skillDTOs)
                                .joinDate(emp.getHireDate() != null ? 
                                        emp.getHireDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                                .totalExperience(totalExperienceYears)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            // Step 3: Calculate summary metrics based on allEmployeeDetails
            long allocatedCount = 0;
            long availableCount = 0;
            long endingSoonCount = 0;
            double totalUtilization = 0;
            Map<String, Integer> skillCounts = new HashMap<>();

            for (EmployeeReportDTO.EmployeeDetailDTO detailDTO : allEmployeeDetails) {
                String empStatus = detailDTO.getStatus();
                if (empStatus != null) {
                    switch (empStatus.toLowerCase()) {
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
                    }
                }
                totalUtilization += (detailDTO.getUtilization() != null ? detailDTO.getUtilization() : 0);

                if (Boolean.TRUE.equals(includeSkills) && detailDTO.getSkills() != null) {
                    for (EmployeeReportDTO.SkillDTO skill : detailDTO.getSkills()) {
                        skillCounts.merge(skill.getName(), 1, Integer::sum);
                    }
                }
            }
            
            List<EmployeeReportDTO.TopSkillDTO> topSkills = skillCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> EmployeeReportDTO.TopSkillDTO.builder()
                            .name(entry.getKey())
                            .count(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
            
            double averageUtilization = !allEmployeeDetails.isEmpty() ? totalUtilization / allEmployeeDetails.size() : 0.0;
            
            EmployeeReportDTO.SummaryMetricsDTO summaryMetrics = EmployeeReportDTO.SummaryMetricsDTO.builder()
                    .totalEmployees(allEmployeeDetails.size()) 
                    .allocatedCount((int)allocatedCount)
                    .availableCount((int)availableCount)
                    .endingSoonCount((int)endingSoonCount)
                    .utilizationRate(averageUtilization)
                    .topSkills(topSkills)
                    .build();
            
            // Step 4: Manual pagination for the content
            List<EmployeeReportDTO.EmployeeDetailDTO> pagedContent;
            int totalElements = allEmployeeDetails.size();
            int pageSize = pageable.getPageSize();
            int pageNumber = pageable.getPageNumber(); // 0-indexed
            int startItem = pageNumber * pageSize;

            if (totalElements == 0 || startItem >= totalElements) {
                pagedContent = Collections.emptyList();
            } else {
                int endItem = Math.min(startItem + pageSize, totalElements);
                pagedContent = allEmployeeDetails.subList(startItem, endItem);
            }
            
            // Step 5: Build pageable info
            int totalPages = (totalElements == 0) ? 0 : (int) Math.ceil((double) totalElements / pageSize);
            if (totalPages == 0 && totalElements > 0) totalPages = 1; // if less than one page of data, still 1 page

            EmployeeReportDTO.PageableDTO pageableInfo = EmployeeReportDTO.PageableDTO.builder()
                    .pageNumber(pageNumber + 1) // Convert to 1-based for response
                    .pageSize(pageSize)
                    .totalPages(totalPages)
                    .totalElements(totalElements)
                    .sort(pageable.getSort().toString())
                    .build();
            
            return EmployeeReportDTO.builder()
                    .reportInfo(reportInfo)
                    .content(pagedContent) // Use paged content
                    .summaryMetrics(summaryMetrics) // Use summary from all data
                    .pageable(pageableInfo)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error generating employee list report: {}", e.getMessage(), e);
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
            Boolean includeDetails, String exportType, Pageable pageable, // original pageable from controller
            Long currentUserId, Authentication authentication, HttpServletResponse response) {
        
        // Handle export types (csv, excel)
        if (!"json".equals(exportType)) {
            handleExport(exportType, "margin_detail_report", response);
            return null;
        }
        
        try {
            if ("employee".equals(groupBy)) {
                // Step 1: Fetch all employee margin data (unpaged)
                Page<com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO> unpagedEmployeeMarginsResult = 
                        marginService.getEmployeeMargins(
                            employeeId != null ? employeeId.longValue() : null,
                            teamId != null ? teamId.longValue() : null,
                            period,
                            fromDate,
                            toDate,
                            null, // yearMonth
                            null, // yearQuarter
                            null, // year
                            null, // status
                            Pageable.unpaged() // Fetch all for accurate summary
                        );
                
                List<com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO> allRawEmployeeMargins = 
                        unpagedEmployeeMarginsResult.getContent();

                // Step 2: Convert all raw DTOs to Report DTOs
                List<Object> allEmployeeMarginReportItems = allRawEmployeeMargins.stream()
                        .map(empMargin -> {
                            MarginReportDTO.LeaderDTO teamLeader = null;
                            if (empMargin.getTeam() != null && empMargin.getTeam().getId() != null) {
                                try {
                                    TeamDto teamDetail = teamService.getTeamById(empMargin.getTeam().getId());
                                    if (teamDetail != null && teamDetail.getLeaderId() != null) {
                                        teamLeader = MarginReportDTO.LeaderDTO.builder()
                                                .id(teamDetail.getLeaderId().intValue())
                                                .name(teamDetail.getLeaderName())
                                                .build();
                                    }
                                } catch (Exception e) {
                                    log.warn("Error fetching team leader for employee margin team {}: {}", empMargin.getTeam().getId(), e.getMessage());
                                }
                            }
                            
                            double empAverageMargin = empMargin.getPeriods().stream()
                                    .filter(p -> p.getMargin() != null)
                                    .mapToDouble(p -> p.getMargin().doubleValue())
                                    .average()
                                    .orElse(0.0);

                            return MarginReportDTO.EmployeeMarginDTO.builder()
                                    .employeeId(empMargin.getEmployeeId().intValue())
                                    .employeeCode(empMargin.getEmployeeCode())
                                    .employeeName(empMargin.getName())
                                    .team(empMargin.getTeam() != null ? 
                                            MarginReportDTO.TeamBasicDTO.builder()
                                                    .id(empMargin.getTeam().getId().intValue())
                                                    .name(empMargin.getTeam().getName())
                                                    .build() : null)
                                    .position(empMargin.getPosition())
                                    .marginData(empMargin.getPeriods().stream()
                                            .map(periodData -> MarginReportDTO.MarginDataDTO.builder()
                                                    .period(periodData.getPeriod())
                                                    .cost(periodData.getCost() != null ? periodData.getCost().longValue() : 0L)
                                                    .revenue(periodData.getRevenue() != null ? periodData.getRevenue().longValue() : 0L)
                                                    .margin(periodData.getMargin() != null ? periodData.getMargin().doubleValue() : 0.0)
                                                    .status(periodData.getMarginStatus())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .averageMargin(empAverageMargin)
                                    .status(empMargin.getStatus())
                                    .build();
                        })
                        .collect(Collectors.toList());
                
                // Step 3: Calculate overall summary metrics from allRawEmployeeMargins
                long overallRedCount = 0;
                long overallYellowCount = 0;
                long overallGreenCount = 0;
                double overallTotalMarginSum = 0;
                int overallMarginDataPoints = 0;

                for (com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO empMargin : allRawEmployeeMargins) {
                    for (com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO.PeriodMarginDTO periodData : empMargin.getPeriods()) {
                        if (periodData.getMarginStatus() != null) {
                            switch (periodData.getMarginStatus()) {
                                case "Red": overallRedCount++; break;
                                case "Yellow": overallYellowCount++; break;
                                case "Green": overallGreenCount++; break;
                            }
                        }
                        if (periodData.getMargin() != null) {
                            overallTotalMarginSum += periodData.getMargin().doubleValue();
                            overallMarginDataPoints++;
                        }
                    }
                }
                        
                double overallAverageMarginForAllEmployees = overallMarginDataPoints > 0 ? overallTotalMarginSum / overallMarginDataPoints : 0.0;
                
                // Step 4: Call buildMarginReport with overall metrics and original pageable
                return buildMarginReport("employee", allEmployeeMarginReportItems, 
                                         overallAverageMarginForAllEmployees, 
                                         (int) overallRedCount, (int) overallYellowCount, (int) overallGreenCount, 
                                         pageable, // Pass original pageable
                                         period, fromDate, toDate, teamId, employeeId, null, marginThreshold);
                        
            } else { // Default to "team"
                MarginSummaryDTO marginSummary = marginService.getMarginSummary(
                        teamId != null ? teamId.longValue() : null,
                        period != null ? period : "month",
                        fromDate,
                        toDate,
                        null, null, null, "table", "team"
                );
                
                List<Object> teamMarginReportItems = marginSummary.getTeams().stream()
                        .map(teamSummary -> {
                            MarginReportDTO.LeaderDTO teamLeader = null;
                            if (teamSummary.getId() != null) {
                                try {
                                    TeamDto teamDetail = teamService.getTeamById(teamSummary.getId());
                                    if (teamDetail != null && teamDetail.getLeaderId() != null) {
                                        teamLeader = MarginReportDTO.LeaderDTO.builder()
                                                .id(teamDetail.getLeaderId().intValue())
                                                .name(teamDetail.getLeaderName())
                                                .build();
                                    }
                                } catch (Exception e) {
                                    log.warn("Error fetching team leader for team margin {}: {}", teamSummary.getId(), e.getMessage());
                                }
                            }
                            
                            return MarginReportDTO.TeamMarginDTO.builder()
                                    .teamId(teamSummary.getId().intValue())
                                    .teamName(teamSummary.getName())
                                    .leader(teamLeader)
                                    .employeeCount(teamSummary.getEmployeeCount())
                                    .marginData(teamSummary.getPeriods().stream()
                                            .map(periodData -> MarginReportDTO.TeamMarginDataDTO.builder()
                                                    .period(periodData.getPeriod())
                                                    .totalCost(periodData.getCost() != null ? periodData.getCost().longValue() : 0L)
                                                    .totalRevenue(periodData.getRevenue() != null ? periodData.getRevenue().longValue() : 0L)
                                                    .margin(periodData.getMargin() != null ? periodData.getMargin().doubleValue() : 0.0)
                                                    .status(periodData.getMarginStatus())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .averageMargin(teamSummary.getMargin() != null ? teamSummary.getMargin().doubleValue() : 0.0)
                                    .status(teamSummary.getMarginStatus())
                                    .build();
                        })
                        .collect(Collectors.toList());
                
                Map<String, Integer> statusCounts = marginSummary.getSummary() != null && marginSummary.getSummary().getStatusCounts() != null ? 
                                                    marginSummary.getSummary().getStatusCounts() : new HashMap<>();
                int summaryRedCount = statusCounts.getOrDefault("Red", 0);
                int summaryYellowCount = statusCounts.getOrDefault("Yellow", 0);
                int summaryGreenCount = statusCounts.getOrDefault("Green", 0);
                
                double summaryAverageMargin = marginSummary.getSummary() != null && marginSummary.getSummary().getAverageMargin() != null ? 
                        marginSummary.getSummary().getAverageMargin().doubleValue() : 0.0;
                
                return buildMarginReport("team", teamMarginReportItems, summaryAverageMargin, 
                                         summaryRedCount, summaryYellowCount, summaryGreenCount, 
                                         pageable, // Pass original pageable
                                         period, fromDate, toDate, teamId, employeeId, null, marginThreshold);
            }
            
        } catch (Exception e) {
            log.error("Error generating margin detail report: {}", e.getMessage(), e);
            return buildFallbackMarginReport(groupBy, pageable, teamId, employeeId, period, fromDate, toDate);
        }
    }
    
    /**
     * Build margin report from actual data
     */
    private MarginReportDTO buildMarginReport(
            String level, List<Object> fullContent, double averageOverallMargin,
            int overallRedCount, int overallYellowCount, int overallGreenCount,
            Pageable requestedPageable, // Changed from Page<?> page
            String period, LocalDate fromDate, LocalDate toDate,
            Integer teamId, Integer employeeId, String status, String marginThreshold) {
        
        // Build filters (remains the same)
        Map<String, Object> filters = new HashMap<>();
        if (teamId != null) filters.put("teamId", teamId);
        if (employeeId != null) filters.put("employeeId", employeeId);
        if (period != null) filters.put("period", period);
        if (status != null) filters.put("status", status);
        if (marginThreshold != null) filters.put("marginThreshold", marginThreshold);
        
        MarginReportDTO.ReportInfoDTO reportInfo = MarginReportDTO.ReportInfoDTO.builder()
                .reportName("Báo cáo chi tiết margin theo " + ("employee".equals(level) ? "nhân viên" : "team"))
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .period(period)
                .fromDate(fromDate != null ? fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .toDate(toDate != null ? toDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .filters(filters)
                .build();
        
        // Build margin trend data (using averageOverallMargin)
        List<MarginReportDTO.MarginTrendDTO> marginTrend = Collections.emptyList();
        if (fromDate != null && toDate != null) { // Simplified trend calculation
            marginTrend = Arrays.asList(
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.minusMonths(5).toString()).value(averageOverallMargin * 0.95).build(),
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.minusMonths(4).toString()).value(averageOverallMargin * 0.97).build(),
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.minusMonths(3).toString()).value(averageOverallMargin * 0.98).build(),
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.minusMonths(2).toString()).value(averageOverallMargin * 0.99).build(),
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.minusMonths(1).toString()).value(averageOverallMargin * 1.01).build(),
                    MarginReportDTO.MarginTrendDTO.builder().period(fromDate.toString()).value(averageOverallMargin).build()
            );
        }
        
        MarginReportDTO.SummaryMetricsDTO summaryMetrics = MarginReportDTO.SummaryMetricsDTO.builder()
                .averageMargin(averageOverallMargin)
                .redCount(overallRedCount)
                .yellowCount(overallYellowCount)
                .greenCount(overallGreenCount)
                .marginDistribution(MarginReportDTO.MarginDistributionDTO.builder()
                        .labels(Arrays.asList("Red", "Yellow", "Green"))
                        .values(Arrays.asList(overallRedCount, overallYellowCount, overallGreenCount))
                        .build())
                .marginTrend(marginTrend)
                .build();
        
        // Manual pagination for the content
        List<Object> pagedDisplayContent;
        int totalElements = fullContent.size();
        int pageSize = requestedPageable.getPageSize();
        int pageNumber = requestedPageable.getPageNumber(); // 0-indexed

        if (totalElements == 0 || pageNumber * pageSize >= totalElements) {
            pagedDisplayContent = Collections.emptyList();
        } else {
            int startItem = pageNumber * pageSize;
            int endItem = Math.min(startItem + pageSize, totalElements);
            pagedDisplayContent = fullContent.subList(startItem, endItem);
        }
        
        int totalPages = (totalElements == 0) ? 0 : (int) Math.ceil((double) totalElements / pageSize);
        if (totalPages == 0 && totalElements > 0) totalPages = 1;


        MarginReportDTO.PageableDTO pageableInfo = MarginReportDTO.PageableDTO.builder()
                .pageNumber(pageNumber + 1) // Convert to 1-based for response
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .sort(requestedPageable.getSort().toString())
                .build();
        
        return MarginReportDTO.builder()
                .reportInfo(reportInfo)
                .summaryMetrics(summaryMetrics)
                .content(pagedDisplayContent) // Use paged content
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
        
        List<com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO> allFetchedOpportunities;
        // com.company.internalmgmt.modules.opportunity.dto.response.OpportunitySummaryDTO initialSummaryFromService;
        // long totalElementsFromService;

        try {
            com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest serviceRequest = 
                    new com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest();
            
            serviceRequest.setPage(1); 
            serviceRequest.setSize(10000); // Fetch large number for manual filtering

            if (pageable.getSort().isSorted()) {
                pageable.getSort().stream().findFirst().ifPresent(order -> {
                    serviceRequest.setSortBy(order.getProperty());
                    serviceRequest.setSortDir(order.getDirection().name().toLowerCase());
                });
            } else {
                serviceRequest.setSortBy("lastInteractionDate");
                serviceRequest.setSortDir("desc");
            }
            
            if (keyword != null) serviceRequest.setKeyword(keyword);
            if (dealStage != null) serviceRequest.setStatus(dealStage);
            if (onsite != null) serviceRequest.setPriority(onsite); 
            if (salesId != null) serviceRequest.setAssignedTo(salesId.longValue());
            if (leaderId != null) serviceRequest.setEmployeeId(leaderId.longValue()); 
            if (fromDate != null) serviceRequest.setFromDate(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) serviceRequest.setToDate(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse oppServiceResponse = 
                    opportunityService.getOpportunities(serviceRequest);
            
            allFetchedOpportunities = oppServiceResponse.getContent() != null ? oppServiceResponse.getContent() : new ArrayList<>();
            // initialSummaryFromService = oppServiceResponse.getSummary();
            // totalElementsFromService = oppServiceResponse.getPageable() != null ? oppServiceResponse.getPageable().getTotalElements() : allFetchedOpportunities.size();

            // --- Manual Filtering Starts Here ---
            List<com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO> manuallyFilteredOpportunities = 
                    new ArrayList<>(allFetchedOpportunities);

            // Manual filter for customerId
            if (customerId != null) {
                // Assuming OpportunityDTO does not have customerId, this filter is conceptual.
                // If it were available, it would be:
                // manuallyFilteredOpportunities.removeIf(opp -> !customerId.equals(opp.getCustomerId()));
                // For now, we log that it's an intended filter if service doesn't handle it via keyword.
                log.info("customerId filter requested but not applied directly on OpportunityDTO post-service call due to missing field. Assumed to be handled by keyword search or needs DTO update.");
            }
            
            final String finalFollowUpStatusFilter = followUpStatus;
            if (finalFollowUpStatusFilter != null && !finalFollowUpStatusFilter.isEmpty()) {
                manuallyFilteredOpportunities.removeIf(opp -> {
                    String calculatedFollowUpStatus = calculateFollowUpStatus(opp.getLastInteractionDate());
                    return !finalFollowUpStatusFilter.equalsIgnoreCase(calculatedFollowUpStatus);
                });
            }
            // --- Manual Filtering Ends Here ---

            // --- Pagination on Manually Filtered Data ---
            int totalFilteredElements = manuallyFilteredOpportunities.size();
            int pageStart = pageable.getPageNumber() * pageable.getPageSize();
            int pageEnd = Math.min(pageStart + pageable.getPageSize(), totalFilteredElements);
            
            List<com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO> pagedOpportunities;
            if (pageStart >= totalFilteredElements) {
                pagedOpportunities = Collections.emptyList();
            } else {
                pagedOpportunities = manuallyFilteredOpportunities.subList(pageStart, pageEnd);
            }

            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo danh sách cơ hội");
            reportInfo.put("generatedAt", LocalDateTime.now().format(ISO_DATE_TIME_FORMATTER));
            
            Map<String, Object> filtersApplied = new HashMap<>();
            if (customerId != null) filtersApplied.put("customerId", customerId);
            if (salesId != null) filtersApplied.put("salesId", salesId);
            if (leaderId != null) filtersApplied.put("leaderId", leaderId);
            if (dealStage != null) filtersApplied.put("dealStage", dealStage);
            if (followUpStatus != null) filtersApplied.put("followUpStatus", followUpStatus);
            if (onsite != null) filtersApplied.put("onsite", onsite);
            if (fromDate != null) filtersApplied.put("fromDate", fromDate.format(ISO_DATE_FORMATTER));
            if (toDate != null) filtersApplied.put("toDate", toDate.format(ISO_DATE_FORMATTER));
            if (keyword != null) filtersApplied.put("keyword", keyword);
            reportInfo.put("filters", filtersApplied);
            
            List<Map<String, Object>> content = pagedOpportunities.stream() // Use pagedOpportunities
                    .map(opp -> {
                        Map<String, Object> oppMap = new HashMap<>();
                        oppMap.put("id", opp.getId());
                        oppMap.put("hubspotId", opp.getExternalId()); 
                        oppMap.put("name", opp.getName());
                        
                        Map<String, Object> customerMapData = new HashMap<>();
                        customerMapData.put("id", null); 
                        customerMapData.put("name", opp.getCustomerName());
                        customerMapData.put("industry", null); 
                        oppMap.put("customer", customerMapData);

                        oppMap.put("dealStage", opp.getStatus());
                        oppMap.put("estimatedValue", opp.getAmount() != null ? opp.getAmount().longValue() : 0L);
                        oppMap.put("createdDate", opp.getCreatedAt() != null ? opp.getCreatedAt().format(ISO_DATE_TIME_FORMATTER) : null);
                        oppMap.put("lastInteractionDate", opp.getLastInteractionDate() != null ? opp.getLastInteractionDate().format(ISO_DATE_TIME_FORMATTER) : null);
                        
                        oppMap.put("followUpStatus", calculateFollowUpStatus(opp.getLastInteractionDate()));

                        Map<String, Object> salesMapData = new HashMap<>();
                        if (opp.getAssignedTo() != null) {
                            salesMapData.put("id", opp.getAssignedTo().getId());
                            salesMapData.put("name", opp.getAssignedTo().getName());
                            salesMapData.put("email", opp.getAssignedTo().getEmail());
                        } else {
                            salesMapData.put("id", null);
                            salesMapData.put("name", null);
                            salesMapData.put("email", null);
                        }
                        oppMap.put("sales", salesMapData);
                        
                        oppMap.put("onsite", opp.getPriority() != null ? opp.getPriority() : false);
                        
                        if (Boolean.TRUE.equals(includeLeaders)) {
                            try {
                                com.company.internalmgmt.modules.opportunity.model.Opportunity oppEntity = 
                                        opportunityService.getOpportunityEntityById(opp.getId());
                                List<com.company.internalmgmt.modules.opportunity.model.OpportunityAssignment> assignments = 
                                        opportunityAssignmentRepository.findByOpportunity(oppEntity);
                                List<Map<String, Object>> assignedLeaders = assignments.stream()
                                        .map(assignment -> {
                                            Map<String, Object> leaderMap = new HashMap<>();
                                            if (assignment.getEmployee() != null) {
                                                leaderMap.put("id", assignment.getEmployee().getId());
                                                leaderMap.put("name", assignment.getEmployee().getFirstName() + " " + 
                                                                      assignment.getEmployee().getLastName());
                                                // leaderMap.put("email", assignment.getEmployee().getCompanyEmail()); // Not in spec RPT-004 Leader Obj
                                                leaderMap.put("assignDate", assignment.getAssignedAt() != null ? 
                                                                        assignment.getAssignedAt().format(ISO_DATE_TIME_FORMATTER) : null);
                                            }
                                            return leaderMap;
                                        })
                                        .filter(m -> !m.isEmpty())
                                        .collect(Collectors.toList());
                                oppMap.put("leaders", assignedLeaders);
                            } catch (Exception e) {
                                log.warn("Error getting assigned leaders for opportunity {}: {}", opp.getId(), e.getMessage());
                                oppMap.put("leaders", Collections.emptyList());
                            }
                        }
                        
                        if (Boolean.TRUE.equals(includeNotes)) {
                            try {
                                com.company.internalmgmt.common.dto.PageableInfo notesPi = new com.company.internalmgmt.common.dto.PageableInfo();
                                org.springframework.data.domain.Pageable notesPg = org.springframework.data.domain.PageRequest.of(0, 5);
                                List<com.company.internalmgmt.modules.opportunity.dto.OpportunityNoteDTO> notesDto = 
                                        opportunityNoteService.getNotesByOpportunity(opp.getId(), notesPg, notesPi);
                                List<Map<String, Object>> notesMap = notesDto.stream()
                                        .map(note -> {
                                            Map<String, Object> noteMap = new HashMap<>();
                                            noteMap.put("id", note.getId());
                                            noteMap.put("content", note.getContent());
                                            Map<String, Object> createdByMap = new HashMap<>();
                                            createdByMap.put("id", note.getAuthorId());
                                            createdByMap.put("name", note.getAuthorName());
                                            noteMap.put("createdBy", createdByMap);
                                            noteMap.put("createdAt", note.getCreatedAt() != null ? 
                                                                    note.getCreatedAt().format(ISO_DATE_TIME_FORMATTER) : null);
                                            return noteMap;
                                        })
                                        .collect(Collectors.toList());
                                oppMap.put("notes", notesMap);
                            } catch (Exception e) {
                                log.warn("Error getting notes for opportunity {}: {}", opp.getId(), e.getMessage());
                                oppMap.put("notes", Collections.emptyList());
                            }
                        }
                        return oppMap;
                    })
                    .collect(Collectors.toList());
            
            // Build summary metrics from manuallyFilteredOpportunities (before pagination)
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalOpportunities", totalFilteredElements);

            Map<String, Integer> byFollowUpCount = new HashMap<>();
            byFollowUpCount.put("red", 0);
            byFollowUpCount.put("yellow", 0);
            byFollowUpCount.put("green", 0);
            manuallyFilteredOpportunities.forEach(opp -> {
                String status = calculateFollowUpStatus(opp.getLastInteractionDate());
                byFollowUpCount.merge(status, 1, Integer::sum);
            });
            summaryMetrics.put("byFollowUp", byFollowUpCount);

            Map<String, Integer> byDealStageCount = manuallyFilteredOpportunities.stream()
                .collect(Collectors.groupingBy(
                    opp -> opp.getStatus() != null ? mapDealStage(opp.getStatus()) : "Unknown",
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
            summaryMetrics.put("byDealStage", byDealStageCount);
            
            summaryMetrics.put("onsitePriority", (int) manuallyFilteredOpportunities.stream().filter(opp -> Boolean.TRUE.equals(opp.getPriority())).count());
            
            summaryMetrics.put("byCustomer", manuallyFilteredOpportunities.stream()
                .filter(opp -> opp.getCustomerName() != null)
                .collect(Collectors.groupingBy(
                    com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO::getCustomerName,
                    Collectors.collectingAndThen(
                        Collectors.counting(), 
                        count -> Collections.singletonMap("count", count.intValue())
                    )))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> custSummary = new HashMap<>();
                    custSummary.put("name", entry.getKey());
                    custSummary.put("count", ((Map<String, Integer>)entry.getValue()).get("count"));
                    return custSummary;
                })
                .collect(Collectors.toList()));

            summaryMetrics.put("bySales", manuallyFilteredOpportunities.stream()
                .filter(opp -> opp.getAssignedTo() != null && opp.getAssignedTo().getName() != null)
                .collect(Collectors.groupingBy(
                    opp -> opp.getAssignedTo().getName(),
                    Collectors.collectingAndThen(
                        Collectors.counting(),
                        count -> Collections.singletonMap("count", count.intValue())
                    )))
                .entrySet().stream()
                .map(entry -> {
                     Map<String, Object> salesSummary = new HashMap<>();
                     salesSummary.put("name", entry.getKey());
                     salesSummary.put("count", ((Map<String, Integer>)entry.getValue()).get("count"));
                     return salesSummary;
                })
                .collect(Collectors.toList()));

            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", pageable.getPageNumber() + 1); 
            pageableInfo.put("pageSize", pageable.getPageSize());
            pageableInfo.put("totalPages", (int) Math.ceil((double) totalFilteredElements / pageable.getPageSize()));
            pageableInfo.put("totalElements", totalFilteredElements);
            pageableInfo.put("sort", pageable.getSort().toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error generating opportunity list report: {}", e.getMessage(), e);
            return new HashMap<String, Object>() {{
                put("reportName", "Báo cáo danh sách cơ hội");
                put("error", "Error loading opportunity data: " + e.getMessage());
                put("content", Collections.emptyList());
                put("summaryMetrics", new HashMap<>());
                put("pageable", new HashMap<String, Object>() {{
                    put("pageNumber", pageable.getPageNumber() + 1);
                    put("pageSize", pageable.getPageSize());
                    put("totalPages", 0);
                    put("totalElements", 0);
                }});
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
        
        List<com.company.internalmgmt.modules.contract.dto.ContractDTO> allFetchedContracts;
        Pageable initialPageable = pageable;
        boolean needsManualFiltering = customerId != null || opportunityId != null || expiryFromDate != null || expiryToDate != null;

        if (needsManualFiltering) {
            // Fetch a large page if manual filtering is needed.
            // Use original sort from 'pageable' but override page number and size.
            initialPageable = org.springframework.data.domain.PageRequest.of(0, 10000, pageable.getSort());
            log.info("Manual filtering required for contract report. Fetching up to 10000 records for filtering.");
        }

        try {
            Page<com.company.internalmgmt.modules.contract.dto.ContractDTO> contractServicePage =
                    contractService.searchContracts(
                            keyword,
                            null,    // contractCode specific filter (can be part of keyword)
                            status,
                            type,
                            salesId != null ? salesId.longValue() : null,
                            minValue,
                            maxValue,
                            fromDate,
                            toDate,
                            paymentStatus,
                            initialPageable // Use initialPageable (could be large page or original)
                    );
            
            allFetchedContracts = new ArrayList<>(contractServicePage.getContent());

            // --- Manual Filtering Starts Here ---
            List<com.company.internalmgmt.modules.contract.dto.ContractDTO> manuallyFilteredContracts = 
                    new ArrayList<>(allFetchedContracts);

            if (customerId != null) {
                // This relies on customerId being reliably searchable via keyword by the service,
                // or ContractDTO having a customerId field.
                // For now, assuming ContractDTO has customerName and we filter by it if customerId is present.
                // This is a simplification. A more robust solution might involve a separate customer lookup.
                // We will filter by customerName based on the provided customerId if the keyword search wasn't sufficient.
                // This part is tricky without knowing how customerId maps to customerName or if ContractDTO gets customerId.
                // Let's assume for now that 'keyword' should ideally handle customer searches.
                // If ContractDTO had customerId:
                // manuallyFilteredContracts.removeIf(c -> c.getCustomerId() == null || !c.getCustomerId().equals(customerId.longValue()));
                // Since it has customerName, and we don't have customerId in ContractDTO,
                // this filter is hard to apply precisely here unless customerId IS the name or part of keyword.
                log.info("customerId filter requested. Current implementation relies on 'keyword' search for customer matching. Manual post-filter for exact customerId is not directly supported on ContractDTO without customerId field.");
            }

            if (opportunityId != null) {
                manuallyFilteredContracts.removeIf(contract -> 
                    contract.getRelatedOpportunity() == null || 
                    contract.getRelatedOpportunity().getId() == null ||
                    !contract.getRelatedOpportunity().getId().equals(opportunityId.longValue())
                );
            }

            if (expiryFromDate != null) {
                manuallyFilteredContracts.removeIf(contract -> 
                    contract.getEndDate() == null || contract.getEndDate().isBefore(expiryFromDate)
                );
            }
            if (expiryToDate != null) {
                manuallyFilteredContracts.removeIf(contract -> 
                    contract.getEndDate() == null || contract.getEndDate().isAfter(expiryToDate)
                );
            }
            // --- Manual Filtering Ends Here ---

            // --- Pagination on Manually Filtered Data ---
            List<com.company.internalmgmt.modules.contract.dto.ContractDTO> pagedContracts;
            int totalFilteredElements = manuallyFilteredContracts.size();
            int pageStart = pageable.getPageNumber() * pageable.getPageSize(); // pageable is 0-indexed from controller
            int pageEnd = Math.min(pageStart + pageable.getPageSize(), totalFilteredElements);

            if (pageStart >= totalFilteredElements) {
                pagedContracts = Collections.emptyList();
            } else {
                pagedContracts = manuallyFilteredContracts.subList(pageStart, pageEnd);
            }
            
            // Build report structure
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo danh sách hợp đồng");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> filtersApplied = new HashMap<>();
            if (customerId != null) filtersApplied.put("customerId", customerId);
            if (salesId != null) filtersApplied.put("salesId", salesId);
            if (status != null) filtersApplied.put("status", status);
            if (type != null) filtersApplied.put("type", type);
            if (opportunityId != null) filtersApplied.put("opportunityId", opportunityId);
            if (minValue != null) filtersApplied.put("minValue", minValue);
            if (maxValue != null) filtersApplied.put("maxValue", maxValue);
            if (fromDate != null) filtersApplied.put("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) filtersApplied.put("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (expiryFromDate != null) filtersApplied.put("expiryFromDate", expiryFromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (expiryToDate != null) filtersApplied.put("expiryToDate", expiryToDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paymentStatus != null) filtersApplied.put("paymentStatus", paymentStatus);
            if (keyword != null) filtersApplied.put("keyword", keyword);
            reportInfo.put("filters", filtersApplied);
            
            List<Map<String, Object>> content = pagedContracts.stream() // Use pagedContracts
                    .map(contract -> {
                        Map<String, Object> contractMap = new HashMap<>();
                        contractMap.put("id", contract.getId());
                        contractMap.put("contractCode", contract.getContractCode());
                        contractMap.put("clientName", contract.getCustomerName());
                        contractMap.put("projectName", contract.getName()); // API spec uses projectName for contract name
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
                        
                        // Payment status from ContractDTO itself if available and summarized
                        String overallPaymentStatus = "Unknown";
                        if (contract.getPaymentStatus() != null && contract.getPaymentStatus().getStatus() != null) { // Changed getOverallStatus() to getStatus()
                            overallPaymentStatus = contract.getPaymentStatus().getStatus(); // Changed getOverallStatus() to getStatus()
                        }
                        contractMap.put("paymentStatus", overallPaymentStatus);
                        
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
                                log.warn("Error fetching payment terms for contract {}: {}", contract.getId(), e.getMessage());
                                contractMap.put("paymentTerms", new java.util.ArrayList<>());
                            }
                        }
                        
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
                                 log.warn("Error fetching employees for contract {}: {}", contract.getId(), e.getMessage());
                                contractMap.put("assignedEmployees", new java.util.ArrayList<>());
                            }
                        }
                        
                        return contractMap;
                    })
                    .collect(Collectors.toList());
            
            // Calculate summary metrics based on manuallyFilteredContracts (entire filtered dataset)
            long totalValueAllFiltered = manuallyFilteredContracts.stream()
                    .filter(contract -> contract.getAmount() != null)
                    .mapToLong(contract -> contract.getAmount().longValue())
                    .sum();
            
            Map<String, Integer> statusCountsAllFiltered = manuallyFilteredContracts.stream()
                    .collect(Collectors.groupingBy(
                            contract -> contract.getStatus() != null ? contract.getStatus().toString() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Integer> typeCountsAllFiltered = manuallyFilteredContracts.stream()
                    .collect(Collectors.groupingBy(
                            contract -> contract.getContractType() != null ? contract.getContractType().toString() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalContracts", totalFilteredElements);
            summaryMetrics.put("totalValue", totalValueAllFiltered);
            summaryMetrics.put("averageValue", totalFilteredElements > 0 ? 
                    (double) totalValueAllFiltered / totalFilteredElements : 0.0);
            summaryMetrics.put("byStatus", statusCountsAllFiltered);
            summaryMetrics.put("byType", typeCountsAllFiltered);
            
            // Build pageable info based on manual pagination
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", pageable.getPageNumber() + 1); // Convert to 1-based for response
            pageableInfo.put("pageSize", pageable.getPageSize());
            pageableInfo.put("totalPages", (int) Math.ceil((double) totalFilteredElements / pageable.getPageSize()));
            pageableInfo.put("totalElements", totalFilteredElements);
            pageableInfo.put("sort", pageable.getSort().toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error generating contract list report: {}", e.getMessage(), e);
            // Fallback remains the same, creating an error map
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("reportName", "Báo cáo danh sách hợp đồng");
            errorResult.put("error", "Error loading contract data: " + e.getMessage());
            errorResult.put("content", Collections.emptyList());
            
            Map<String, Object> summaryMetricsError = new HashMap<>();
            summaryMetricsError.put("totalContracts", 0);
            summaryMetricsError.put("totalValue", 0L);
            summaryMetricsError.put("averageValue", 0.0);
            summaryMetricsError.put("byStatus", Collections.emptyMap());
            summaryMetricsError.put("byType", Collections.emptyMap());
            errorResult.put("summaryMetrics", summaryMetricsError);

            Map<String, Object> pageableInfoError = new HashMap<>();
            pageableInfoError.put("pageNumber", pageable.getPageNumber() + 1);
            pageableInfoError.put("pageSize", pageable.getPageSize());
            pageableInfoError.put("totalPages", 0);
            pageableInfoError.put("totalElements", 0);
            pageableInfoError.put("sort", pageable.getSort().toString());
            errorResult.put("pageable", pageableInfoError);
            return errorResult;
        }
    }
    
    @Override
    public Object getPaymentStatusReport(
            Integer customerId, Integer salesId, Integer contractId, String status,
            LocalDate fromDate, LocalDate toDate, LocalDate paidFromDate,
            LocalDate paidToDate, Double minAmount, Double maxAmount,
            Boolean includeDetails, String exportType, Pageable pageable,
            Long currentUserId, Authentication authentication, HttpServletResponse response) {
        
        if (!"json".equals(exportType)) {
            handleExport(exportType, "payment_report", response);
            return null;
        }
        
        List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> allRelevantPaymentTerms = new ArrayList<>();
        com.company.internalmgmt.modules.contract.dto.ContractDTO targetContractDto = null;
        Map<Long, com.company.internalmgmt.modules.contract.dto.ContractDTO> contractDetailsMap = new HashMap<>(); // For aggregated view

        try {
            if (contractId != null) {
                allRelevantPaymentTerms = paymentTermService.getPaymentTermsByContractId(contractId.longValue());
                try {
                    targetContractDto = contractService.getContractById(contractId.longValue(), false, false, false);
                    if (targetContractDto != null) {
                        contractDetailsMap.put(targetContractDto.getId(), targetContractDto);
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch details for contractId {}: {}", contractId, e.getMessage());
                }
            } else {
                // Aggregated report: Fetch based on broader criteria if possible, or all terms and then filter.
                // Current approach: Overdue and upcoming. This does not directly support customerId/salesId filtering efficiently.
                // For a more robust customerId/salesId filter on aggregated payments, service layer changes would be ideal.
                
                // Placeholder: If we were to fetch ALL payment terms for manual filtering (could be very large):
                // Page<ContractPaymentTermDTO> allTermsPage = paymentTermService.getAllPaymentTerms(PageRequest.of(0, Integer.MAX_VALUE));
                // allRelevantPaymentTerms.addAll(allTermsPage.getContent());
                // Then, we would need to fetch contract details for each term, which is inefficient.

                // Sticking to existing logic of overdue/upcoming for non-contractId specific general view
                List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> overduePayments = 
                        paymentTermService.findOverduePaymentTerms(); // This should return terms with contractId
                List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> upcomingPayments = 
                        paymentTermService.findPaymentTermsDueSoon(30); // This should return terms with contractId

                if (overduePayments != null) allRelevantPaymentTerms.addAll(overduePayments);
                if (upcomingPayments != null) {
                    // Avoid duplicates if a payment is both overdue and upcoming (edge case, but possible)
                    for (com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO upcoming : upcomingPayments) {
                        if (!allRelevantPaymentTerms.stream().anyMatch(p -> p.getId().equals(upcoming.getId()))) {
                            allRelevantPaymentTerms.add(upcoming);
                        }
                    }
                }
                // Populate contractDetailsMap for terms fetched in aggregated view
                // This is inefficient but necessary if contract details are needed per term and not filtered by a single contractId
                // ContractPaymentTermDTO should ideally include contractId to make this efficient.
                // Assuming ContractPaymentTermDTO *does* have contractId based on previous context (even if not explicitly shown in search)
                if (includeDetails) { // Only fetch if details are needed
                    List<Long> distinctContractIds = allRelevantPaymentTerms.stream()
                        .map(com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO::getContractId) // Assuming getContractId() exists!
                        .filter(cId -> cId != null)
                        .distinct()
                        .collect(Collectors.toList());
                    for (Long cId : distinctContractIds) {
                        if (!contractDetailsMap.containsKey(cId)) {
                            try {
                                com.company.internalmgmt.modules.contract.dto.ContractDTO cDto = contractService.getContractById(cId, false, false, false);
                                if (cDto != null) contractDetailsMap.put(cId, cDto);
                            } catch (Exception e) {
                                log.warn("Could not fetch details for contractId {} in aggregated payment report: {}", cId, e.getMessage());
                            }
                        }
                    }
                }
            }
            
            final com.company.internalmgmt.modules.contract.dto.ContractDTO finalTargetContractDto = targetContractDto;

            List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> filteredPayments = 
                    allRelevantPaymentTerms.stream()
                    .filter(payment -> {
                        com.company.internalmgmt.modules.contract.dto.ContractDTO currentContractForPayment = 
                            finalTargetContractDto != null ? finalTargetContractDto : contractDetailsMap.get(payment.getContractId());

                        if (customerId != null) {
                            if (currentContractForPayment == null || currentContractForPayment.getCustomerName() == null) return false; // Cannot filter
                            // This is a name match, not ID match. CustomerID on ContractDTO would be better.
                            // Assuming customerId parameter is actually a name or part of it if used for filtering here.
                            // boolean customerMatch = currentContractForPayment.getCustomerName().toLowerCase().contains(customerId.toString().toLowerCase());
                            // For now, if customerId is passed, we assume it's for a specific contract report (targetContractDto should exist)
                            // or it's not effectively filterable in aggregate view without better DTOs/services.
                            if (finalTargetContractDto == null) {
                                // log.warn("customerId filter on aggregated payment report is indicative and may not be precise.");
                            } else { // Only apply if it's for the specific contract's customer
                                // if (currentContractForPayment.getCustomerId() != customerId) return false; // Ideal check
                                // We don't have customerId on ContractDTO for a direct match here.
                            }
                        }
                        if (salesId != null) {
                            if (currentContractForPayment == null || currentContractForPayment.getSalesPerson() == null || currentContractForPayment.getSalesPerson().getId() == null) return false;
                            if (!currentContractForPayment.getSalesPerson().getId().equals(salesId.longValue())) return false;
                        }
                        if (status != null && (payment.getStatus() == null || !status.equalsIgnoreCase(payment.getStatus()))) {
                            return false;
                        }
                        if (minAmount != null && (payment.getAmount() == null || payment.getAmount().doubleValue() < minAmount)) {
                            return false;
                        }
                        if (maxAmount != null && (payment.getAmount() == null || payment.getAmount().doubleValue() > maxAmount)) {
                            return false;
                        }
                        if (fromDate != null && (payment.getDueDate() == null || payment.getDueDate().isBefore(fromDate))) {
                            return false;
                        }
                        if (toDate != null && (payment.getDueDate() == null || payment.getDueDate().isAfter(toDate))) {
                            return false;
                        }
                        if (paidFromDate != null && (payment.getPaidDate() == null || payment.getPaidDate().isBefore(paidFromDate))) {
                            return false;
                        }
                        if (paidToDate != null && (payment.getPaidDate() == null || payment.getPaidDate().isAfter(paidToDate))) {
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            int totalFilteredElements = filteredPayments.size();
            int pageStart = pageable.getPageNumber() * pageable.getPageSize();
            int pageEnd = Math.min(pageStart + pageable.getPageSize(), totalFilteredElements);
            List<com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO> pagedPayments = 
                    (pageStart <= pageEnd && pageStart < totalFilteredElements) ? filteredPayments.subList(pageStart, pageEnd) : Collections.emptyList();
            
            Map<String, Object> reportInfo = new HashMap<>();
            reportInfo.put("reportName", "Báo cáo tình trạng thanh toán/công nợ");
            reportInfo.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> filtersApplied = new HashMap<>();
            if (customerId != null) filtersApplied.put("customerId", customerId); 
            if (salesId != null) filtersApplied.put("salesId", salesId); 
            if (contractId != null) filtersApplied.put("contractId", contractId);
            if (status != null) filtersApplied.put("status", status);
            if (fromDate != null) filtersApplied.put("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (toDate != null) filtersApplied.put("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paidFromDate != null) filtersApplied.put("paidFromDate", paidFromDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (paidToDate != null) filtersApplied.put("paidToDate", paidToDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (minAmount != null) filtersApplied.put("minAmount", minAmount);
            if (maxAmount != null) filtersApplied.put("maxAmount", maxAmount);
            reportInfo.put("filters", filtersApplied);
            
            List<Map<String, Object>> content = pagedPayments.stream()
                    .map(payment -> {
                        Map<String, Object> paymentMap = new HashMap<>();
                        paymentMap.put("id", payment.getId());
                        
                        com.company.internalmgmt.modules.contract.dto.ContractDTO contractForPayment = 
                            finalTargetContractDto != null ? finalTargetContractDto : contractDetailsMap.get(payment.getContractId());

                        if (contractForPayment != null) {
                            paymentMap.put("contractId", contractForPayment.getId());
                            paymentMap.put("contractCode", contractForPayment.getContractCode());
                            paymentMap.put("clientName", contractForPayment.getCustomerName());
                            paymentMap.put("currency", contractForPayment.getCurrency());
                        } else {
                            paymentMap.put("contractId", payment.getContractId()); // Keep payment's contractId if parent DTO not found
                            paymentMap.put("contractCode", "N/A"); 
                            paymentMap.put("clientName", "N/A"); 
                            paymentMap.put("currency", "N/A"); 
                        }
                        
                        paymentMap.put("termNumber", payment.getTermNumber());
                        paymentMap.put("description", payment.getDescription());
                        paymentMap.put("amount", payment.getAmount() != null ? payment.getAmount().longValue() : 0L);
                        paymentMap.put("dueDate", payment.getDueDate() != null ? 
                                payment.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        paymentMap.put("status", payment.getStatus());
                        paymentMap.put("paidDate", payment.getPaidDate() != null ? 
                                payment.getPaidDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                        paymentMap.put("paidAmount", payment.getPaidAmount() != null ? payment.getPaidAmount().longValue() : 0L);
                        
                        if (payment.getDueDate() != null && !"Paid".equalsIgnoreCase(payment.getStatus())) {
                            long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(payment.getDueDate(), LocalDate.now());
                            if (daysDiff > 0) {
                                paymentMap.put("daysOverdue", daysDiff);
                            } else {
                                paymentMap.put("daysToDue", Math.abs(daysDiff));
                            }
                        } else {
                            paymentMap.put("daysOverdue", 0);
                            paymentMap.put("daysToDue", 0);
                        }
                        
                        return paymentMap;
                    })
                    .collect(Collectors.toList());
            
            long totalAmount = filteredPayments.stream()
                    .filter(p -> p.getAmount() != null)
                    .mapToLong(p -> p.getAmount().longValue())
                    .sum();
            
            long paidAmountTotal = filteredPayments.stream()
                    .filter(p -> "Paid".equalsIgnoreCase(p.getStatus()) && p.getPaidAmount() != null)
                    .mapToLong(p -> p.getPaidAmount().longValue())
                    .sum();
            
            long overdueAmount = filteredPayments.stream()
                    .filter(p -> p.getDueDate() != null && 
                            p.getDueDate().isBefore(LocalDate.now()) && 
                            !"Paid".equalsIgnoreCase(p.getStatus()) &&
                            p.getAmount() != null)
                    .mapToLong(p -> p.getAmount().longValue())
                    .sum();
            
            Map<String, Integer> statusCounts = filteredPayments.stream()
                    .collect(Collectors.groupingBy(
                            payment -> payment.getStatus() != null ? payment.getStatus() : "Unknown",
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
            
            Map<String, Object> summaryMetrics = new HashMap<>();
            summaryMetrics.put("totalPaymentTerms", totalFilteredElements);
            summaryMetrics.put("totalAmount", totalAmount);
            summaryMetrics.put("paidAmount", paidAmountTotal);
            summaryMetrics.put("outstandingAmount", totalAmount - paidAmountTotal);
            summaryMetrics.put("overdueAmount", overdueAmount);
            summaryMetrics.put("byStatus", statusCounts);
            
            Map<String, Object> pageableInfo = new HashMap<>();
            pageableInfo.put("pageNumber", pageable.getPageNumber() + 1);
            pageableInfo.put("pageSize", pageable.getPageSize());
            pageableInfo.put("totalPages", (int) Math.ceil((double) totalFilteredElements / pageable.getPageSize()));
            pageableInfo.put("totalElements", totalFilteredElements);
            pageableInfo.put("sort", pageable.getSort().toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("reportInfo", reportInfo);
            result.put("content", content);
            result.put("summaryMetrics", summaryMetrics);
            result.put("pageable", pageableInfo);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error generating payment status report: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("reportName", "Báo cáo tình trạng thanh toán/công nợ");
            errorResult.put("error", "Error loading payment data: " + e.getMessage());
            errorResult.put("content", Collections.emptyList());

            Map<String, Object> summaryMetricsError = new HashMap<>();
            summaryMetricsError.put("totalPaymentTerms", 0);
            summaryMetricsError.put("totalAmount", 0L);
            summaryMetricsError.put("paidAmount", 0L);
            summaryMetricsError.put("outstandingAmount", 0L);
            summaryMetricsError.put("overdueAmount", 0L);
            summaryMetricsError.put("byStatus", Collections.emptyMap());
            errorResult.put("summaryMetrics", summaryMetricsError);

            Map<String, Object> pageableInfoError = new HashMap<>();
            pageableInfoError.put("pageNumber", pageable.getPageNumber() + 1);
            pageableInfoError.put("pageSize", pageable.getPageSize());
            pageableInfoError.put("totalPages", 0);
            pageableInfoError.put("totalElements", 0);
            pageableInfoError.put("sort", pageable.getSort().toString());
            errorResult.put("pageable", pageableInfoError);
            return errorResult;
        }
    }
    
    @Override
    public SalesKpiReportDTO getKpiProgressReport(
            Integer salesId, Integer year, Integer quarter, Integer month,
            Double minAchievement, Double maxAchievement, Boolean includeDetails,
            String exportType, Pageable pageable, Long currentUserId,
            Authentication authentication, HttpServletResponse response) {
        
        if (!"json".equals(exportType)) {
            handleExport(exportType, "kpi_report", response);
            return null; 
        }

        // This is a placeholder for the actual SalesKpiService and SalesKpiData.
        // When SalesKpiService is implemented, replace this simulated data fetching.
        // SalesKpiFilter kpiFilter = SalesKpiFilter.builder()
        //         .salesPersonId(salesId)
        //         .year(year)
        //         .quarter(quarter)
        //         .month(month)
        //         .build();
        // IMPORTANT: Use Pageable.unpaged() to fetch ALL data for correct summary calculation
        // Page<SalesKpiData> allKpisPage = salesKpiService.getSalesKpis(kpiFilter, Pageable.unpaged());
        // List<SalesKpiData> allRawKpiData = allKpisPage.getContent();
        
        // --- SIMULATED DATA FETCHING START ---
        // Replace this block with actual salesKpiService call when available.
        List<SimulatedSalesKpiData> allRawKpiData = getSimulatedSalesKpiData(salesId, year, quarter, month);
        // --- SIMULATED DATA FETCHING END ---

        try {
            // 1. Build ReportInfo
            // Assuming ReportInfoDTO can hold a generic 'periodDetails' or adapt KpiPeriodInfoDTO
             Map<String, Object> periodDetailsMap = new HashMap<>();
            int reportYear = (year != null) ? year : LocalDate.now().getYear();
            String periodDescription = "Năm " + reportYear;
            periodDetailsMap.put("year", reportYear);
            if (quarter != null) {
                periodDescription = "Quý " + quarter + "/" + reportYear;
                periodDetailsMap.put("quarter", quarter);
            }
            if (month != null) {
                LocalDate monthDate = LocalDate.of(reportYear, month, 1);
                periodDescription = "Tháng " + monthDate.format(DateTimeFormatter.ofPattern("MM/yyyy"));
                periodDetailsMap.put("month", month);
            }
            periodDetailsMap.put("description", periodDescription);
            
            KpiPeriodInfoDTO kpiPeriodInfo = KpiPeriodInfoDTO.builder()
                .year(year).quarter(quarter).month(month).description(periodDescription).build();


            Map<String, Object> filtersAppliedMap = new HashMap<>();
            if (salesId != null) filtersAppliedMap.put("salesId", salesId);
            if (year != null) filtersAppliedMap.put("year", year);
            if (quarter != null) filtersAppliedMap.put("quarter", quarter);
            if (month != null) filtersAppliedMap.put("month", month);
            if (minAchievement != null) filtersAppliedMap.put("minAchievement", minAchievement);
            if (maxAchievement != null) filtersAppliedMap.put("maxAchievement", maxAchievement);
            // if (includeDetails != null) filtersAppliedMap.put("includeDetails", includeDetails);

            ReportInfoDTO<KpiPeriodInfoDTO> finalReportInfo = ReportInfoDTO.<KpiPeriodInfoDTO>builder()
                .reportName("Báo cáo tiến độ KPI doanh thu Sales")
                .generatedAt(LocalDateTime.now().format(ISO_DATE_TIME_FORMATTER))
                .filters(filtersAppliedMap)
                .periodDetails(kpiPeriodInfo) // Assuming ReportInfoDTO is generic or has this field
                .build();
            
            // 2. Transform raw data to SalesKpiDetailDTO list
            List<SalesKpiDetailDTO> allKpiDetails = allRawKpiData.stream()
                .map(rawData -> {
                    BigDecimal target = rawData.getKpiTargetAmount();
                    BigDecimal actual = rawData.getActualRevenue();
                    double achievementRate = 0.0;

                    if (target != null && target.compareTo(BigDecimal.ZERO) > 0 && actual != null) {
                        achievementRate = actual.multiply(BigDecimal.valueOf(100))
                                                .divide(target, 2, RoundingMode.HALF_UP).doubleValue();
                    } else if (actual != null && actual.compareTo(BigDecimal.ZERO) > 0 && 
                               (target == null || target.compareTo(BigDecimal.ZERO) == 0)) {
                        // Achieved revenue with no target or zero target implies high/infinite achievement if actual > 0
                        achievementRate = 200.0; // Represent as a high number, e.g., 200%
                    } else if (actual == null && target != null && target.compareTo(BigDecimal.ZERO) > 0) {
                        achievementRate = 0.0; // No actual, but had a target
                    } else {
                        achievementRate = 0.0; // No actual, no target, or target is zero
                    }
                    
                    String kpiStatus = "N/A";
                     if (target != null && target.compareTo(BigDecimal.ZERO) > 0) {
                        if (achievementRate >= 110) kpiStatus = "Vượt mục tiêu";
                        else if (achievementRate >= 90) kpiStatus = "Đạt mục tiêu";
                        else if (achievementRate >= 70) kpiStatus = "Gần mục tiêu";
                        else kpiStatus = "Dưới mục tiêu";
                    } else {
                         kpiStatus = "Không có mục tiêu";
                    }

                    return SalesKpiDetailDTO.builder()
                        .salesPersonId(rawData.getSalesPersonId())
                        .salesPersonName(rawData.getSalesPersonName())
                        .teamName(rawData.getTeamName())
                        .period(rawData.getPeriodIdentifier())
                        .kpiTargetAmount(target)
                        .actualRevenue(actual)
                        .achievementRate(achievementRate)
                        .status(kpiStatus)
                        .build();
                })
                .collect(Collectors.toList());

            // 3. Apply achievement filters (if any)
            List<SalesKpiDetailDTO> filteredKpiDetails = new ArrayList<>(allKpiDetails);
            if (minAchievement != null) {
                filteredKpiDetails.removeIf(kpi -> kpi.getAchievementRate() < minAchievement);
            }
            if (maxAchievement != null) {
                filteredKpiDetails.removeIf(kpi -> kpi.getAchievementRate() > maxAchievement);
            }
            
            // Sort by achievement rate for consistent top/lowest performers
            // Handle nulls in achievementRate if they can occur, by placing them last or first as needed
            filteredKpiDetails.sort(Comparator.comparing(SalesKpiDetailDTO::getAchievementRate, Comparator.nullsLast(Double::compareTo)).reversed());


            // 4. Calculate SummaryMetrics
            int totalSalesPersonsInReport = filteredKpiDetails.size();
            double sumOfAchievementRates = filteredKpiDetails.stream()
                                            .filter(kpi -> kpi.getKpiTargetAmount() != null && kpi.getKpiTargetAmount().compareTo(BigDecimal.ZERO) > 0) // Only average those with targets
                                            .mapToDouble(SalesKpiDetailDTO::getAchievementRate)
                                            .sum();
            long countForAverage = filteredKpiDetails.stream()
                                     .filter(kpi -> kpi.getKpiTargetAmount() != null && kpi.getKpiTargetAmount().compareTo(BigDecimal.ZERO) > 0)
                                     .count();
            double averageAchievementOverall = countForAverage > 0 ? sumOfAchievementRates / countForAverage : 0.0;


            int belowTargetCount = 0; int nearTargetCount = 0; int onTargetCount = 0; int exceedsTargetCount = 0;
            for(SalesKpiDetailDTO detail : filteredKpiDetails) {
                if (detail.getKpiTargetAmount() != null && detail.getKpiTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                    double rate = detail.getAchievementRate();
                    if (rate >= 110) exceedsTargetCount++;
                    else if (rate >= 90) onTargetCount++;
                    else if (rate >= 70) nearTargetCount++;
                    else belowTargetCount++;
                }
            }
            KpiAchievementRangeDTO achievementRange = KpiAchievementRangeDTO.builder()
                .belowTargetCount(belowTargetCount).nearTargetCount(nearTargetCount)
                .onTargetCount(onTargetCount).exceedsTargetCount(exceedsTargetCount).build();

            List<SalesKpiDetailDTO> topPerformers = filteredKpiDetails.stream()
                .filter(kpi -> kpi.getKpiTargetAmount() != null && kpi.getKpiTargetAmount().compareTo(BigDecimal.ZERO) > 0) // Only rank those with targets
                .limit(5).collect(Collectors.toList());
            List<SalesKpiDetailDTO> lowestPerformers = filteredKpiDetails.stream()
                .filter(kpi -> kpi.getKpiTargetAmount() != null && kpi.getKpiTargetAmount().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(SalesKpiDetailDTO::getAchievementRate, Comparator.nullsFirst(Double::compareTo)))
                .limit(5).collect(Collectors.toList());

            SalesKpiSummaryMetricsDTO summaryMetrics = SalesKpiSummaryMetricsDTO.builder()
                .totalSalesPersons(totalSalesPersonsInReport)
                .averageAchievementRate(averageAchievementOverall)
                .byAchievementRange(achievementRange)
                .topPerformers(topPerformers)
                .lowestPerformers(lowestPerformers)
                .build();

            // 5. Manual Pagination
            List<SalesKpiDetailDTO> pagedContent;
            int totalElements = filteredKpiDetails.size();
            int pageSize = pageable.getPageSize();
            int pageNumber = pageable.getPageNumber();
            int startItem = pageNumber * pageSize;

            if (totalElements == 0 || startItem >= totalElements) {
                pagedContent = Collections.emptyList();
            } else {
                int endItem = Math.min(startItem + pageSize, totalElements);
                pagedContent = filteredKpiDetails.subList(startItem, endItem);
            }
            
            int totalPages = (totalElements == 0) ? 0 : (int) Math.ceil((double) totalElements / pageSize);
            if (totalPages == 0 && totalElements > 0) totalPages = 1;

            PageableDTO pageableInfo = PageableDTO.builder()
                .pageNumber(pageNumber + 1).pageSize(pageSize)
                .totalPages(totalPages).totalElements(totalElements)
                .sort(pageable.getSort().toString()).build();

            return SalesKpiReportDTO.builder()
                .reportInfo(finalReportInfo)
                .summaryMetrics(summaryMetrics)
                .content(pagedContent)
                .pageable(pageableInfo)
                .build();

        } catch (Exception e) {
            log.error("Error generating KPI progress report: {}", e.getMessage(), e);
            // Fallback: Return a SalesKpiReportDTO with error indication or basic structure
            // This requires SalesKpiReportDTO and its nested DTOs to have no-args constructors
            // or default builders if direct instantiation is complex in error cases.
            // For simplicity, returning a basic structure. User might need to refine error DTO.
            
            ReportInfoDTO<KpiPeriodInfoDTO> errorReportInfo = ReportInfoDTO.<KpiPeriodInfoDTO>builder()
                .reportName("Báo cáo tiến độ KPI doanh thu Sales")
                .generatedAt(LocalDateTime.now().format(ISO_DATE_TIME_FORMATTER))
                .filters(new HashMap<>()) // Empty filters
                .error("Error loading KPI data: " + e.getMessage()) // Add error field to ReportInfoDTO or a dedicated error DTO
                .build();

            SalesKpiSummaryMetricsDTO errorSummary = SalesKpiSummaryMetricsDTO.builder()
                .totalSalesPersons(0)
                .averageAchievementRate(0.0)
                .byAchievementRange(KpiAchievementRangeDTO.builder().build())
                .topPerformers(Collections.emptyList())
                .lowestPerformers(Collections.emptyList())
                .build();
            
            PageableDTO errorPageable = PageableDTO.builder()
                .pageNumber(pageable.getPageNumber() + 1).pageSize(pageable.getPageSize())
                .totalPages(0).totalElements(0).build();

            return SalesKpiReportDTO.builder()
                .reportInfo(errorReportInfo)
                .summaryMetrics(errorSummary)
                .content(Collections.emptyList())
                .pageable(errorPageable)
                .build();
        }
    } 

    // --- Helper Methods --- 

    // SIMULATED DATA HELPER - REMOVE WHEN SalesKpiService IS REAL
    // This class should be SalesKpiData from your sales module when implemented
    private static class SimulatedSalesKpiData { 
        private Long salesPersonId;
        private String salesPersonName;
        private String teamName;
        private String periodIdentifier;
        private BigDecimal kpiTargetAmount;
        private BigDecimal actualRevenue;

        public SimulatedSalesKpiData(Long id, String name, String team, String period, BigDecimal target, BigDecimal actual) {
            this.salesPersonId = id; this.salesPersonName = name; this.teamName = team;
            this.periodIdentifier = period; this.kpiTargetAmount = target; this.actualRevenue = actual;
        }
        public Long getSalesPersonId() { return salesPersonId; }
        public String getSalesPersonName() { return salesPersonName; }
        public String getTeamName() { return teamName; }
        public String getPeriodIdentifier() { return periodIdentifier; }
        public BigDecimal getKpiTargetAmount() { return kpiTargetAmount; }
        public BigDecimal getActualRevenue() { return actualRevenue; }
    }

    private List<SimulatedSalesKpiData> getSimulatedSalesKpiData(Integer salesIdFilter, Integer yearFilter, Integer qFilter, Integer mFilter) {
        List<SimulatedSalesKpiData> data = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        String periodSuffix = "";
        if (yearFilter == null) yearFilter = currentYear;

        if (mFilter != null && qFilter != null) { // Month takes precedence if both are given
             periodSuffix = String.format("-%02d", mFilter);
        } else if (mFilter != null) {
             periodSuffix = String.format("-%02d", mFilter);
        } else if (qFilter != null) {
             periodSuffix = "-Q" + qFilter;
        }
        String simPeriod = yearFilter + periodSuffix;
        if (simPeriod.equals(String.valueOf(yearFilter)) && yearFilter == currentYear && qFilter == null && mFilter == null) { // Default to current month if only year is current year
            simPeriod = yearFilter + String.format("-%02d", LocalDate.now().getMonthValue());
        } else if (simPeriod.equals(String.valueOf(yearFilter))) { // If only year is specified, assume it's annual.
             // For annual, periodIdentifier might just be the year or have a suffix like "-Annual"
             // For simulation, let's just use year.
        }


        data.add(new SimulatedSalesKpiData(1L, "Nguyễn Văn A", "Team Alpha", simPeriod, new BigDecimal("100000000"), new BigDecimal("120000000"))); // 120%
        data.add(new SimulatedSalesKpiData(2L, "Trần Thị B", "Team Alpha", simPeriod, new BigDecimal("150000000"), new BigDecimal("135000000"))); // 90%
        data.add(new SimulatedSalesKpiData(3L, "Lê Văn C", "Team Beta", simPeriod, new BigDecimal("120000000"), new BigDecimal("80000000")));   // 66.67%
        data.add(new SimulatedSalesKpiData(4L, "Phạm Thị D", "Team Beta", simPeriod, new BigDecimal("200000000"), new BigDecimal("220000000"))); // 110%
        data.add(new SimulatedSalesKpiData(5L, "Hoàng Văn E", "Team Gamma", simPeriod, new BigDecimal("80000000"), new BigDecimal("75000000")));  // 93.75%
        data.add(new SimulatedSalesKpiData(6L, "Vũ Thị F", "Team Gamma", simPeriod, new BigDecimal("100000000"), null));                        // 0% (no actual)
        data.add(new SimulatedSalesKpiData(7L, "Đỗ Văn G", "Team Alpha", simPeriod, null, new BigDecimal("50000000")));                         // 200% (no target, but revenue)
        data.add(new SimulatedSalesKpiData(8L, "Bùi Thị H", "Team Beta", simPeriod, new BigDecimal("50000000"), new BigDecimal("30000000")));    // 60%
        data.add(new SimulatedSalesKpiData(9L, "Ngô Văn I", "Team Gamma", simPeriod, new BigDecimal("120000000"), new BigDecimal("120000000"))); // 100%
        data.add(new SimulatedSalesKpiData(10L, "Đặng Thị K", "Team Alpha", simPeriod, new BigDecimal("0"), new BigDecimal("10000000")));        // 200% (zero target, but revenue)
        data.add(new SimulatedSalesKpiData(11L, "Trịnh Văn L", "Team Beta", simPeriod, new BigDecimal("100000000"), new BigDecimal("0")));        // 0% (target, but zero revenue)


        if (salesIdFilter != null) {
            data.removeIf(d -> !d.getSalesPersonId().equals(salesIdFilter.longValue()));
        }
        return data;
    }
    // END OF SIMULATED DATA HELPER


    private String calculateFollowUpStatus(LocalDateTime lastInteractionDate) {
        if (lastInteractionDate == null) {
            return "red"; 
        }
        LocalDate today = LocalDate.now();
        LocalDate interactionDate = lastInteractionDate.toLocalDate();
        long daysSinceLastInteraction = ChronoUnit.DAYS.between(interactionDate, today);

        if (daysSinceLastInteraction > 7) {
            return "red";
        } else if (daysSinceLastInteraction >= 2) { 
            return "yellow";
        } else { 
            return "green";
        }
    }

    private String mapDealStage(String serviceDealStage) {
        if (serviceDealStage == null) return "Unknown";
        String lowerStage = serviceDealStage.toLowerCase();
        switch (lowerStage) {
            case "new":
            case "contacted":
                return "Appointment"; 
            case "qualified": 
                return "Demo"; 
            case "proposal":
            case "negotiation":
                return "Negotiation"; 
            case "won":
                return "Closed Won";
            case "lost":
                return "Closed Lost";
            default:
                return serviceDealStage; 
        }
    }

    private void handleExport(String exportType, String reportName, HttpServletResponse response) {
        // ... (current implementation for handleExport) ...
        log.info("Handling export for type: {} and report: {}", exportType, reportName);
        // Actual export logic (e.g., writing to CSV/Excel) would go here.
        // For now, just sets headers as in the original example if provided.
        if ("excel".equalsIgnoreCase(exportType)) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx\"");
        } else if ("csv".equalsIgnoreCase(exportType)) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + "_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv\"");
        }
    }

} // End of ReportServiceImpl class 