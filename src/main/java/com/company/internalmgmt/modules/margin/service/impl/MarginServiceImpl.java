package com.company.internalmgmt.modules.margin.service.impl;

import com.company.internalmgmt.common.exception.AccessDeniedException;
import com.company.internalmgmt.common.exception.BadRequestException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.common.model.SystemConfig;
import com.company.internalmgmt.common.repository.SystemConfigRepository;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.Team;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.TeamRepository;
import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.dto.request.ImportCostRequestDTO;
import com.company.internalmgmt.modules.margin.dto.request.UpdateCostRequestDTO;
import com.company.internalmgmt.modules.margin.model.EmployeeCost;
import com.company.internalmgmt.modules.margin.model.EmployeeRevenue;
import com.company.internalmgmt.modules.margin.repository.EmployeeCostRepository;
import com.company.internalmgmt.modules.margin.repository.EmployeeRevenueRepository;
import com.company.internalmgmt.modules.margin.service.MarginService;
import com.company.internalmgmt.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarginServiceImpl implements MarginService {

    private final EmployeeCostRepository employeeCostRepository;
    private final EmployeeRevenueRepository employeeRevenueRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final SystemConfigRepository systemConfigRepository;
    
    // Default margin thresholds
    private static final BigDecimal RED_THRESHOLD = BigDecimal.valueOf(20.0);
    private static final BigDecimal YELLOW_THRESHOLD = BigDecimal.valueOf(30.0);

    @Override
    public Page<EmployeeMarginDTO> getEmployeeMargins(
            Long employeeId,
            Long teamId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year,
            String status,
            Pageable pageable) {
        
        log.info("Getting employee margins with filters: employeeId={}, teamId={}, period={}, status={}", 
                employeeId, teamId, period, status);
        
        // 1. Check user access permissions
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAllAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin:read:all"));
        boolean hasTeamAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin:read:team"));
        
        // If user doesn't have all access and tries to access data outside their team, throw exception
        if (!hasAllAccess && teamId != null && !userHasAccessToTeam(teamId)) {
            throw new AccessDeniedException("You don't have permission to access margin data for this team");
        }
        
        // 2. Parse and validate date parameters
        DateRange dateRange = parseDateParameters(period, fromDate, toDate, yearMonth, yearQuarter, year);
        
        // 3. Get employee IDs based on filters
        List<Long> employeeIds = getEmployeeIdsBasedOnFilters(employeeId, teamId, hasAllAccess, hasTeamAccess);
        
        log.debug("Employee IDs based on filters: {}", employeeIds);

        if (employeeIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        // 4. Get costs and revenues
        Map<EmployeeKey, EmployeeCost> costMap = getCostsForPeriod(employeeIds, dateRange);
        log.debug("Cost map for employeeIds {}: {}", employeeIds, costMap.size());
        Map<EmployeeKey, List<EmployeeRevenue>> revenueMap = getRevenuesForPeriod(employeeIds, dateRange);
        log.debug("Revenue map for employeeIds {}: {}", employeeIds, revenueMap.size());

        
        // 5. Calculate margins and create DTOs
        List<EmployeeMarginDTO> marginDTOs = new ArrayList<>();
        
        for (Long empId : employeeIds) {
            // Skip if no data for this employee
            if (!hasDataForEmployee(empId, costMap, revenueMap)) {
                continue;
            }
            
            EmployeeMarginDTO dto = createEmployeeMarginDTO(empId, period, costMap, revenueMap, dateRange);
            
            // Filter by status if requested
            if (status != null && !status.isEmpty()) {
                // Check if any period has the requested status
                boolean hasMatchingStatus = dto.getPeriods().stream()
                        .anyMatch(p -> status.equalsIgnoreCase(p.getMarginStatus()));
                
                if (!hasMatchingStatus) {
                    continue;
                }
            }
            
            marginDTOs.add(dto);
        }
        
        // 6. Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), marginDTOs.size());
        
        // Sort results if needed
        if (pageable.getSort().isSorted()) {
            sortMarginDTOs(marginDTOs, pageable);
        }
        
        List<EmployeeMarginDTO> pagedResult = start < end 
                ? marginDTOs.subList(start, end) 
                : Collections.emptyList();
                
        return new PageImpl<>(pagedResult, pageable, marginDTOs.size());
    }

    @Override
    public EmployeeMarginDTO getEmployeeMarginDetail(
            Long employeeId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year) {
        
        log.info("Getting employee margin detail for employeeId={}, period={}", employeeId, period);
        
        if (employeeId == null) {
            throw new BadRequestException("Employee ID is required");
        }
        
        // 1. Validate user has access to this employee's data
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAllAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin:read:all"));
        boolean hasTeamAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin:read:team"));
                
        // Get employee's team ID
        Long employeeTeamId = getEmployeeTeamId(employeeId);
        
        if (!hasAllAccess && employeeTeamId != null && !userHasAccessToTeam(employeeTeamId)) {
            throw new AccessDeniedException("You don't have permission to access margin data for this employee");
        }
        
        // 2. Parse and validate date filters
        DateRange dateRange = parseDateParameters(period, fromDate, toDate, yearMonth, yearQuarter, year);
        
        // 3. Query detailed costs and revenues for the period
        Map<EmployeeKey, EmployeeCost> costMap = getCostsForPeriod(
                Collections.singletonList(employeeId), dateRange);
        log.debug("Cost map for employee {}: {}", employeeId, costMap.size());
                
        Map<EmployeeKey, List<EmployeeRevenue>> revenueMap = getRevenuesForPeriod(
                Collections.singletonList(employeeId), dateRange);
        log.debug("Revenue map for employee {}: {}", employeeId, revenueMap.size());
        // Check if there's any data
        if (costMap.isEmpty() && revenueMap.isEmpty()) {
            throw new ResourceNotFoundException("No margin data found for employee ID: " + employeeId);
        }
        
        // 4. Calculate margins for each period and determine status
        // 5. Map to detailed DTO
        return createEmployeeMarginDTO(employeeId, period, costMap, revenueMap, dateRange);
    }

    @Override
    public MarginSummaryDTO getMarginSummary(
            Long teamId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year,
            String view,
            String groupBy) {
        
        log.info("Getting margin summary with filters: teamId={}, period={}, groupBy={}", 
                teamId, period, groupBy);
        
        // 1. Validate user's access to team data
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAllAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin-summary:read:all"));
        boolean hasTeamAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("margin-summary:read:team"));
                
        if (!hasAllAccess && teamId != null && !userHasAccessToTeam(teamId)) {
            throw new AccessDeniedException("You don't have permission to access margin data for this team");
        }
        
        // 2. Parse and validate date filters
        DateRange dateRange = parseDateParameters(period, fromDate, toDate, yearMonth, yearQuarter, year);
        
        // 3. Get team IDs based on filters and user access
        List<Long> teamIds = getTeamIdsBasedOnFilters(teamId, hasAllAccess, hasTeamAccess);
        
        if (teamIds.isEmpty()) {
            // Return empty summary
            return createEmptySummary(period, dateRange);
        }
        
        // 4. Query aggregated costs and revenues by team/period
        Map<TeamPeriodKey, BigDecimal> teamCosts = getTeamCostsByPeriod(teamIds, dateRange);
        Map<TeamPeriodKey, BigDecimal> teamRevenues = getTeamRevenuesByPeriod(teamIds, dateRange);
        
        // Get employee counts for each team
        Map<Long, Integer> teamEmployeeCounts = getTeamEmployeeCounts(teamIds);
        
        // 5. Calculate team margins and build DTO
        MarginSummaryDTO summaryDTO = buildMarginSummary(
                teamIds, period, dateRange, teamCosts, teamRevenues, teamEmployeeCounts, groupBy);
        
        // Apply view-specific formatting if needed (chart vs table)
        applyViewFormatting(summaryDTO, view);
        
        return summaryDTO;
    }

    @Override
    @Transactional
    public Map<String, Object> importEmployeeCosts(MultipartFile file, ImportCostRequestDTO request) {
        log.info("Importing employee costs from file for month: {}", request.getMonth());
        
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required and cannot be empty");
        }
        
        // Parse month from request
        YearMonth month;
        try {
            month = YearMonth.parse(request.getMonth());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid month format. Expected YYYY-MM");
        }
        
        // Check if user has access to team
        Long teamId = request.getTeamId();
        if (teamId != null && !userHasAccessToTeam(teamId)) {
            throw new AccessDeniedException("You don't have permission to import costs for this team");
        }
        
        // Initialize result map
        Map<String, Object> result = new HashMap<>();
        result.put("processed", 0);
        result.put("created", 0);
        result.put("updated", 0);
        result.put("skipped", 0);
        result.put("errors", new ArrayList<String>());
        
        try {
            // Parse file based on type (Excel or CSV)
            List<EmployeeCost> costs = parseEmployeeCostsFromFile(file, month.getYear(), month.getMonthValue());
            
            // Process each cost
            for (EmployeeCost cost : costs) {
                try {
                    // Increment processed count
                    result.put("processed", (int)result.get("processed") + 1);
                    
                    // Skip if employee doesn't exist
                    if (!employeeExists(cost.getEmployeeId())) {
                        ((List<String>)result.get("errors")).add(
                                "Employee ID " + cost.getEmployeeId() + " not found");
                        result.put("skipped", (int)result.get("skipped") + 1);
                        continue;
                    }
                    
                    // Check if cost already exists for this employee and period
                    Optional<EmployeeCost> existingCost = employeeCostRepository.findByEmployeeIdAndYearAndMonth(
                            cost.getEmployeeId(), cost.getYear(), cost.getMonth());
                    
                    if (existingCost.isPresent()) {
                        if (request.getOverwrite()) {
                            // Update existing cost
                            EmployeeCost updated = existingCost.get();
                            updated.setCostAmount(cost.getCostAmount());
                            updated.setBasicSalary(cost.getBasicSalary());
                            updated.setAllowance(cost.getAllowance());
                            updated.setOvertime(cost.getOvertime());
                            updated.setOtherCosts(cost.getOtherCosts());
                            updated.setCurrency(cost.getCurrency());
                            updated.setNote(cost.getNote());
                            
                            employeeCostRepository.save(updated);
                            result.put("updated", (int)result.get("updated") + 1);
                        } else {
                            // Skip if overwrite is false
                            result.put("skipped", (int)result.get("skipped") + 1);
                        }
                    } else {
                        // Save new cost
                        employeeCostRepository.save(cost);
                        result.put("created", (int)result.get("created") + 1);
                    }
                } catch (Exception e) {
                    log.error("Error processing cost for employee ID {}: {}", 
                            cost.getEmployeeId(), e.getMessage());
                    ((List<String>)result.get("errors")).add(
                            "Error processing employee ID " + cost.getEmployeeId() + ": " + e.getMessage());
                    result.put("skipped", (int)result.get("skipped") + 1);
                }
            }
            
            log.info("Import completed: processed={}, created={}, updated={}, skipped={}, errors={}",
                    result.get("processed"), result.get("created"), result.get("updated"), 
                    result.get("skipped"), ((List<String>)result.get("errors")).size());
                    
            return result;
            
        } catch (Exception e) {
            log.error("Failed to import employee costs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to import employee costs: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> updateEmployeeCosts(UpdateCostRequestDTO request) {
        log.info("Updating employee costs manually for month: {}", request.getMonth());
        
        // Parse month from request
        YearMonth month;
        try {
            month = YearMonth.parse(request.getMonth());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid month format. Expected YYYY-MM");
        }
        
        // Initialize result map
        Map<String, Object> result = new HashMap<>();
        result.put("processed", 0);
        result.put("created", 0);
        result.put("updated", 0);
        result.put("skipped", 0);
        result.put("errors", new ArrayList<String>());
        
        // Process each cost entry
        for (UpdateCostRequestDTO.EmployeeCostEntry entry : request.getEmployees()) {
            try {
                // Increment processed count
                result.put("processed", (int)result.get("processed") + 1);
                
                // Determine employee ID (either directly or by code)
                Long employeeId = entry.getEmployeeId();
                
                // If employeeId is null but employeeCode is provided, look up by code
                if (employeeId == null && entry.getEmployeeCode() != null) {
                    Optional<Employee> employee = employeeRepository.findByEmployeeCode(entry.getEmployeeCode());
                    if (employee.isPresent()) {
                        employeeId = employee.get().getId();
                    } else {
                        ((List<String>)result.get("errors")).add(
                                "Employee Code " + entry.getEmployeeCode() + " not found");
                        result.put("skipped", (int)result.get("skipped") + 1);
                        continue;
                    }
                }
                
                // Skip if employee doesn't exist
                if (employeeId == null || !employeeExists(employeeId)) {
                    String errorMessage = employeeId == null ? 
                            "Employee ID is required if employee code is not provided" : 
                            "Employee ID " + employeeId + " not found";
                    ((List<String>)result.get("errors")).add(errorMessage);
                    result.put("skipped", (int)result.get("skipped") + 1);
                    continue;
                }
                
                // Check if cost already exists for this employee and period
                Optional<EmployeeCost> existingCost = employeeCostRepository.findByEmployeeIdAndYearAndMonth(
                        employeeId, month.getYear(), month.getMonthValue());
                
                if (existingCost.isPresent()) {
                    if (request.getOverwrite()) {
                        // Update existing cost
                        EmployeeCost updated = existingCost.get();
                        
                        // Update basic salary and other components
                        updated.setBasicSalary(entry.getBasicCost());
                        updated.setAllowance(entry.getAllowance());
                        updated.setOvertime(entry.getOvertime());
                        updated.setOtherCosts(entry.getOtherCosts());
                        
                        // Calculate total cost
                        BigDecimal totalCost = entry.getBasicCost()
                                .add(entry.getAllowance() != null ? entry.getAllowance() : BigDecimal.ZERO)
                                .add(entry.getOvertime() != null ? entry.getOvertime() : BigDecimal.ZERO)
                                .add(entry.getOtherCosts() != null ? entry.getOtherCosts() : BigDecimal.ZERO);
                                
                        updated.setCostAmount(totalCost);
                        updated.setCurrency(entry.getCurrency());
                        updated.setNote(entry.getNote());
                        
                        // Update audit information
                        updated.setUpdatedBy(getCurrentUserId());
                        
                        employeeCostRepository.save(updated);
                        result.put("updated", (int)result.get("updated") + 1);
                    } else {
                        // Skip if overwrite is false
                        result.put("skipped", (int)result.get("skipped") + 1);
                    }
                } else {
                    // Create new cost
                    BigDecimal totalCost = entry.getBasicCost()
                            .add(entry.getAllowance() != null ? entry.getAllowance() : BigDecimal.ZERO)
                            .add(entry.getOvertime() != null ? entry.getOvertime() : BigDecimal.ZERO)
                            .add(entry.getOtherCosts() != null ? entry.getOtherCosts() : BigDecimal.ZERO);
                    
                    // Get current user ID for audit
                    Long currentUserId = getCurrentUserId();
                    
                    EmployeeCost newCost = EmployeeCost.builder()
                            .employeeId(employeeId)
                            .year(month.getYear())
                            .month(month.getMonthValue())
                            .basicSalary(entry.getBasicCost())
                            .allowance(entry.getAllowance())
                            .overtime(entry.getOvertime())
                            .otherCosts(entry.getOtherCosts())
                            .costAmount(totalCost)
                            .currency(entry.getCurrency())
                            .note(entry.getNote())
                            .createdBy(currentUserId)
                            .updatedBy(currentUserId)
                            .build();
                            
                    employeeCostRepository.save(newCost);
                    result.put("created", (int)result.get("created") + 1);
                }
            } catch (Exception e) {
                log.error("Error processing cost for employee: {}", e.getMessage());
                ((List<String>)result.get("errors")).add("Error processing employee: " + e.getMessage());
                result.put("skipped", (int)result.get("skipped") + 1);
            }
        }
        
        log.info("Update completed: processed={}, created={}, updated={}, skipped={}, errors={}",
                result.get("processed"), result.get("created"), result.get("updated"), 
                result.get("skipped"), ((List<String>)result.get("errors")).size());
                
        return result;
    }

    @Override
    public String calculateMarginStatus(BigDecimal margin) {
        if (margin == null) {
            return "Unknown";
        }
        
        Map<String, BigDecimal> thresholds = getMarginThresholds();
        BigDecimal redThreshold = thresholds.get("red");
        BigDecimal yellowThreshold = thresholds.get("yellow");
        
        if (margin.compareTo(redThreshold) <= 0) {
            return "Red";
        } else if (margin.compareTo(yellowThreshold) <= 0) {
            return "Yellow";
        } else {
            return "Green";
        }
    }

    @Override
    public Map<String, BigDecimal> getMarginThresholds() {
        Map<String, BigDecimal> thresholds = new HashMap<>();
        
        try {
            // Get thresholds from system_configs table
            Optional<SystemConfig> redThresholdConfig = systemConfigRepository.findByConfigKey("margin.threshold.red");
            Optional<SystemConfig> yellowThresholdConfig = systemConfigRepository.findByConfigKey("margin.threshold.yellow");
            
            BigDecimal redThreshold = RED_THRESHOLD;
            BigDecimal yellowThreshold = YELLOW_THRESHOLD;
            
            if (redThresholdConfig.isPresent()) {
                try {
                    redThreshold = new BigDecimal(redThresholdConfig.get().getConfigValue());
                    log.debug("Loaded red threshold from config: {}", redThreshold);
                } catch (NumberFormatException e) {
                    log.warn("Invalid red threshold value in config: {}, using default: {}", 
                            redThresholdConfig.get().getConfigValue(), RED_THRESHOLD);
                }
            } else {
                log.debug("Red threshold not found in config, using default: {}", RED_THRESHOLD);
            }
            
            if (yellowThresholdConfig.isPresent()) {
                try {
                    yellowThreshold = new BigDecimal(yellowThresholdConfig.get().getConfigValue());
                    log.debug("Loaded yellow threshold from config: {}", yellowThreshold);
                } catch (NumberFormatException e) {
                    log.warn("Invalid yellow threshold value in config: {}, using default: {}", 
                            yellowThresholdConfig.get().getConfigValue(), YELLOW_THRESHOLD);
                }
            } else {
                log.debug("Yellow threshold not found in config, using default: {}", YELLOW_THRESHOLD);
            }
            
            thresholds.put("red", redThreshold);
            thresholds.put("yellow", yellowThreshold);
            
            log.info("Using margin thresholds: red={}, yellow={}", 
                    thresholds.get("red"), thresholds.get("yellow"));
            
        } catch (Exception e) {
            log.warn("Failed to load margin thresholds from config, using defaults: {}", e.getMessage());
            thresholds.put("red", RED_THRESHOLD);
            thresholds.put("yellow", YELLOW_THRESHOLD);
        }
        
        return thresholds;
    }
    
    /**
     * Tính margin từ doanh thu và chi phí
     * @param revenue doanh thu
     * @param cost chi phí
     * @return margin tính theo phần trăm
     */
    private BigDecimal calculateMargin(BigDecimal revenue, BigDecimal cost) {
        if (revenue.compareTo(BigDecimal.ZERO) == 0) {
            // Nếu không có doanh thu, margin là 0
            return BigDecimal.ZERO;
        }
        
        if (cost.compareTo(BigDecimal.ZERO) == 0) {
            // Nếu không có chi phí nhưng có doanh thu, margin là 100%
            return new BigDecimal("100.0");
        }
        
        // Margin = (Revenue - Cost) / Revenue * 100
        return revenue.subtract(cost)
                .multiply(new BigDecimal("100"))
                .divide(revenue, 2, RoundingMode.HALF_UP);
    }
    
    // Helper methods for getEmployeeMargins
    
    private boolean userHasAccessToTeam(Long teamId) {
        Employee currentEmployee = getCurrentEmployee();
        if (currentEmployee == null || currentEmployee.getTeam() == null) {
            return false;
        }
        
        Team userTeam = currentEmployee.getTeam();
        
        // Check if the target team is the user's team
        if (userTeam.getId().equals(teamId)) {
            return true;
        }
        
        // Check if the target team is a child team of the user's team
        Team targetTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        // Check if user's team is the parent of the target team
        if (targetTeam.getParentTeam() != null && 
                targetTeam.getParentTeam().getId().equals(userTeam.getId())) {
            return true;
        }
        
        return false;
    }
    
    private List<Long> getEmployeeIdsBasedOnFilters(Long employeeId, Long teamId, boolean hasAllAccess, boolean hasTeamAccess) {
        log.debug("Getting employee IDs based on filters: employeeId={}, teamId={}", employeeId, teamId);
        
        // If specific employee ID is provided, just return that
        if (employeeId != null) {
            return Collections.singletonList(employeeId);
        }

        List<Long> employeeIds = new ArrayList<>();
        
        // If team ID is provided, get all employees in that team
        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
            
            log.debug("Finding employees in team: {}", team.getName());
            Page<Employee> employees = employeeRepository.findByTeam_Id(team.getId(), Pageable.unpaged());
            
            employeeIds.addAll(employees.getContent().stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList()));
            
            // Also get employees from child teams
            List<Team> childTeams = teamRepository.findByParentTeam_Id(teamId);
            for (Team childTeam : childTeams) {
                Page<Employee> childTeamEmployees = employeeRepository.findByTeam_Id(childTeam.getId(), Pageable.unpaged());
                employeeIds.addAll(childTeamEmployees.getContent().stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList()));
            }
            
            return employeeIds;
        }
        
        // No filters, apply access controls
        if (hasAllAccess) {
            // User has access to all employees
            Page<Employee> allEmployees = employeeRepository.findAll(Pageable.unpaged());
            return allEmployees.getContent().stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList());
        } else if (hasTeamAccess) {
            // User has access only to their team
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null || currentEmployee.getTeam() == null) {
                return Collections.emptyList();
            }
            
            Team userTeam = currentEmployee.getTeam();
            Page<Employee> teamEmployees = employeeRepository.findByTeam_Id(userTeam.getId(), Pageable.unpaged());
            
            employeeIds.addAll(teamEmployees.getContent().stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList()));
            
            // Also get employees from child teams if current user is a leader
            List<Team> childTeams = teamRepository.findByParentTeam_Id(userTeam.getId());
            for (Team childTeam : childTeams) {
                log.debug("Getting employees from child team: {}", childTeam.getName());
                Page<Employee> childTeamEmployees = employeeRepository.findByTeam_Id(childTeam.getId(), Pageable.unpaged());
                employeeIds.addAll(childTeamEmployees.getContent().stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList()));
            }
            
            return employeeIds;
        }
        
        // User has no team access, return their own employee ID only
        Employee currentEmployee = getCurrentEmployee();
        return currentEmployee != null 
                ? Collections.singletonList(currentEmployee.getId()) 
                : Collections.emptyList();
    }
    
    /**
     * Lấy thông tin nhân viên của người dùng hiện tại
     * 
     * @return đối tượng Employee của người dùng hiện tại, hoặc null nếu không tìm thấy
     */
    private Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Employee> userEmployee = employeeRepository.findByCompanyEmail(username);
        if (userEmployee.isPresent()) {
            return userEmployee.get();
        }
        return null;
    }
    
    private static class DateRange {
        int startYear;
        int startMonth;
        int endYear;
        int endMonth;
    }
    
    private DateRange parseDateParameters(String period, LocalDate fromDate, LocalDate toDate, 
                                        String yearMonth, String yearQuarter, Integer year) {
        DateRange range = new DateRange();
        
        // Current year and month as defaults
        LocalDate now = LocalDate.now();
        range.endYear = now.getYear();
        range.endMonth = now.getMonthValue();
        range.startYear = range.endYear;
        range.startMonth = range.endMonth;
        
        // Parse specific date filters
        if (yearMonth != null && !yearMonth.isEmpty()) {
            try {
                YearMonth ym = YearMonth.parse(yearMonth);
                range.startYear = range.endYear = ym.getYear();
                range.startMonth = range.endMonth = ym.getMonthValue();
                return range;
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid yearMonth format: " + yearMonth + ". Expected format: YYYY-MM");
            }
        }
        
        if (yearQuarter != null && !yearQuarter.isEmpty()) {
            // Format: YYYY-QN (e.g., 2025-Q1)
            try {
                String[] parts = yearQuarter.split("-Q");
                int quarterYear = Integer.parseInt(parts[0]);
                int quarter = Integer.parseInt(parts[1]);
                
                if (quarter < 1 || quarter > 4) {
                    throw new BadRequestException("Invalid quarter: " + quarter + ". Expected: 1-4");
                }
                
                range.startYear = range.endYear = quarterYear;
                range.startMonth = (quarter - 1) * 3 + 1;  // Q1: 1, Q2: 4, Q3: 7, Q4: 10
                range.endMonth = range.startMonth + 2;     // End of quarter
                return range;
            } catch (Exception e) {
                throw new BadRequestException("Invalid yearQuarter format: " + yearQuarter + ". Expected format: YYYY-QN");
            }
        }
        
        if (year != null) {
            range.startYear = range.endYear = year;
            range.startMonth = 1;
            range.endMonth = 12;
            return range;
        }
        
        // Use fromDate and toDate if provided
        if (fromDate != null) {
            range.startYear = fromDate.getYear();
            range.startMonth = fromDate.getMonthValue();
        }
        
        if (toDate != null) {
            range.endYear = toDate.getYear();
            range.endMonth = toDate.getMonthValue();
        }
        
        // If no specific filters but period is specified, adjust range
        if (yearMonth == null && yearQuarter == null && year == null && fromDate == null && toDate == null) {
            if ("quarter".equals(period)) {
                // Current quarter
                int currentQuarter = (range.endMonth - 1) / 3 + 1;
                range.startMonth = (currentQuarter - 1) * 3 + 1;
            } else if ("year".equals(period)) {
                // Current year
                range.startMonth = 1;
            }
        }
        
        return range;
    }
    
    private static class EmployeeKey {
        final Long employeeId;
        final int year;
        final int month;
        
        EmployeeKey(Long employeeId, int year, int month) {
            this.employeeId = employeeId;
            this.year = year;
            this.month = month;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeKey that = (EmployeeKey) o;
            return year == that.year && 
                   month == that.month && 
                   Objects.equals(employeeId, that.employeeId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(employeeId, year, month);
        }
    }
    
    private Map<EmployeeKey, EmployeeCost> getCostsForPeriod(List<Long> employeeIds, DateRange range) {
        // Lấy dữ liệu chi phí thực tế từ repository
        Map<EmployeeKey, EmployeeCost> result = new HashMap<>();
        
        List<EmployeeCost> costs;
        
        // Nếu khoảng thời gian nằm trong cùng một năm
        if (range.startYear == range.endYear) {
            costs = employeeCostRepository.findByEmployeeIdsAndPeriod(
                    employeeIds, range.startYear, range.startMonth, range.endMonth);
        } else {
            // Nếu khoảng thời gian trải dài nhiều năm
            costs = employeeCostRepository.findByEmployeeIdsAndDateRange(
                    employeeIds, range.startYear, range.startMonth, 
                    range.endYear, range.endMonth);
        }
        
        // Đưa dữ liệu vào map để tìm kiếm nhanh
        for (EmployeeCost cost : costs) {
            EmployeeKey key = new EmployeeKey(cost.getEmployeeId(), cost.getYear(), cost.getMonth());
            result.put(key, cost);
        }
        
        return result;
    }
    
    private Map<EmployeeKey, List<EmployeeRevenue>> getRevenuesForPeriod(List<Long> employeeIds, DateRange range) {
        // Lấy dữ liệu doanh thu thực tế từ repository
        Map<EmployeeKey, List<EmployeeRevenue>> result = new HashMap<>();
        
        List<EmployeeRevenue> revenues;
        
        // Nếu khoảng thời gian nằm trong cùng một năm
        if (range.startYear == range.endYear) {
            revenues = employeeRevenueRepository.findByEmployeeIdsAndPeriod(
                    employeeIds, range.startYear, range.startMonth, range.endMonth);
        } else {
            // Nếu khoảng thời gian trải dài nhiều năm
            revenues = employeeRevenueRepository.findByEmployeeIdsAndDateRange(
                    employeeIds, range.startYear, range.startMonth, 
                    range.endYear, range.endMonth);
        }
        
        // Đưa dữ liệu vào map để tìm kiếm nhanh
        for (EmployeeRevenue revenue : revenues) {
            EmployeeKey key = new EmployeeKey(revenue.getEmployeeId(), revenue.getYear(), revenue.getMonth());
            
            // Thêm revenue vào danh sách cho employee trong kỳ
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<>());
            }
            result.get(key).add(revenue);
        }
        
        return result;
    }
    
    private boolean hasDataForEmployee(Long employeeId, 
                                     Map<EmployeeKey, EmployeeCost> costMap, 
                                     Map<EmployeeKey, List<EmployeeRevenue>> revenueMap) {
        // Check if there's any cost or revenue data for this employee
        return costMap.keySet().stream().anyMatch(key -> key.employeeId.equals(employeeId)) ||
               revenueMap.keySet().stream().anyMatch(key -> key.employeeId.equals(employeeId));
    }
    
    private EmployeeMarginDTO createEmployeeMarginDTO(Long employeeId, String period,
                                                     Map<EmployeeKey, EmployeeCost> costMap,
                                                     Map<EmployeeKey, List<EmployeeRevenue>> revenueMap,
                                                     DateRange dateRange) {
        // Lấy thông tin employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Tạo DTO cơ bản
        EmployeeMarginDTO dto = EmployeeMarginDTO.builder()
                .employeeId(employeeId)
                .employeeCode(employee.getEmployeeCode())
                .name(employee.getFirstName() + " " + employee.getLastName())
                .position(employee.getPosition())
                .team(getTeamForEmployee(employeeId))
                .status(employee.getCurrentStatus())
                .periods(new ArrayList<>())
                .build();
        
        // Xác định khoảng thời gian cần hiển thị
        for (int year = dateRange.startYear; year <= dateRange.endYear; year++) {
            int startMonth = (year == dateRange.startYear) ? dateRange.startMonth : 1;
            int endMonth = (year == dateRange.endYear) ? dateRange.endMonth : 12;
            
            for (int month = startMonth; month <= endMonth; month++) {
                // Lấy chi phí tháng
                EmployeeKey key = new EmployeeKey(employeeId, year, month);
                EmployeeCost cost = costMap.get(key);
                BigDecimal costAmount = (cost != null) ? cost.getCostAmount() : BigDecimal.ZERO;
                
                // Lấy doanh thu tháng
                List<EmployeeRevenue> monthRevenues = revenueMap.getOrDefault(key, Collections.emptyList());
                BigDecimal revenueAmount = monthRevenues.stream()
                        .map(EmployeeRevenue::getCalculatedRevenue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Tính margin
                BigDecimal margin = calculateMargin(revenueAmount, costAmount);
                String marginStatus = calculateMarginStatus(margin);
                
                // Tạo PeriodMarginDTO
                EmployeeMarginDTO.PeriodMarginDTO periodDTO = EmployeeMarginDTO.PeriodMarginDTO.builder()
                        .period(String.format("%d-%02d", year, month))
                        .periodLabel(formatPeriodLabel(year, month, period))
                        .cost(costAmount)
                        .revenue(revenueAmount)
                        .margin(margin)
                        .marginStatus(marginStatus)
                        .build();
                
                dto.getPeriods().add(periodDTO);
            }
        }
        
        return dto;
    }
    
    private String formatPeriodLabel(int year, int month, String periodType) {
        if ("month".equals(periodType)) {
            return String.format("Tháng %d/%d", month, year);
        } else if ("quarter".equals(periodType)) {
            int quarter = (month - 1) / 3 + 1;
            return String.format("Q%d %d", quarter, year);
        } else { // year
            return String.valueOf(year);
        }
    }
    
    private void sortMarginDTOs(List<EmployeeMarginDTO> marginDTOs, Pageable pageable) {
        // Implementation depends on sort criteria
        // This is a placeholder implementation
    }
    
    /**
     * Get team ID for an employee
     */
    private Long getEmployeeTeamId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        if (employee.getTeam() == null) {
            return null;
        }
        
        return employee.getTeam().getId();
    }

    /**
     * Get list of team IDs based on filters and user access
     */
    private List<Long> getTeamIdsBasedOnFilters(Long teamId, boolean hasAllAccess, boolean hasTeamAccess) {
        log.debug("Getting team IDs based on filters: teamId={}", teamId);
        
        // If a specific team ID is provided, just return that and its child teams
        if (teamId != null) {
            List<Long> teamIds = new ArrayList<>();
            teamIds.add(teamId);
            
            // Also include child teams
            List<Team> childTeams = teamRepository.findByParentTeam_Id(teamId);
            for (Team childTeam : childTeams) {
                teamIds.add(childTeam.getId());
            }
            
            return teamIds;
        }
        
        // No specific team, apply access controls
        List<Long> teamIds = new ArrayList<>();
        
        if (hasAllAccess) {
            // User has access to all teams
            List<Team> allTeams = teamRepository.findAll();
            return allTeams.stream()
                    .map(Team::getId)
                    .collect(Collectors.toList());
        } else if (hasTeamAccess) {
            // User has access to their team and child teams
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null || currentEmployee.getTeam() == null) {
                return Collections.emptyList();
            }
            
            // Add user's team
            Team userTeam = currentEmployee.getTeam();
            teamIds.add(userTeam.getId());
            
            // Add child teams if user is a leader
            List<Team> childTeams = teamRepository.findByParentTeam_Id(userTeam.getId());
            for (Team childTeam : childTeams) {
                teamIds.add(childTeam.getId());
            }
        }
        
        return teamIds;
    }
    
    /**
     * Create empty summary when no data is available
     */
    private MarginSummaryDTO createEmptySummary(String period, DateRange dateRange) {
        String periodLabel = formatPeriodLabel(dateRange.startYear, dateRange.startMonth, period);
        
        MarginSummaryDTO.SummaryDTO summary = MarginSummaryDTO.SummaryDTO.builder()
                .period(period)
                .periodLabel(periodLabel)
                .totalTeams(0)
                .totalEmployees(0)
                .averageCost(BigDecimal.ZERO)
                .averageRevenue(BigDecimal.ZERO)
                .averageMargin(BigDecimal.ZERO)
                .build();
                
        return MarginSummaryDTO.builder()
                .summary(summary)
                .teams(Collections.emptyList())
                .build();
    }
    
    private static class TeamPeriodKey {
        final Long teamId;
        final int year;
        final int month;
        
        TeamPeriodKey(Long teamId, int year, int month) {
            this.teamId = teamId;
            this.year = year;
            this.month = month;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TeamPeriodKey that = (TeamPeriodKey) o;
            return year == that.year && 
                   month == that.month && 
                   Objects.equals(teamId, that.teamId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(teamId, year, month);
        }
        
        String getPeriodString() {
            return String.format("%04d-%02d", year, month);
        }
    }
    
    /**
     * Get aggregated costs by team and period
     */
    private Map<TeamPeriodKey, BigDecimal> getTeamCostsByPeriod(List<Long> teamIds, DateRange dateRange) {
        Map<TeamPeriodKey, BigDecimal> result = new HashMap<>();
        
        // Get all employees from these teams
        List<Long> allEmployeeIds = new ArrayList<>();
        Map<Long, Team> teamMap = new HashMap<>();
        
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
            teamMap.put(teamId, team);
            
            Page<Employee> employeesPage = employeeRepository.findByTeam_Id(team.getId(), Pageable.unpaged());
            allEmployeeIds.addAll(employeesPage.getContent().stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList()));
        }
        
        if (allEmployeeIds.isEmpty()) {
            return result;
        }
        
        // Get costs for all these employees in the date range
        List<EmployeeCost> costs;
        if (dateRange.startYear == dateRange.endYear) {
            costs = employeeCostRepository.findByEmployeeIdsAndPeriod(
                    allEmployeeIds, dateRange.startYear, dateRange.startMonth, dateRange.endMonth);
        } else {
            costs = employeeCostRepository.findByEmployeeIdsAndDateRange(
                    allEmployeeIds, dateRange.startYear, dateRange.startMonth, 
                    dateRange.endYear, dateRange.endMonth);
        }
        
        // Group by team and period
        Map<Long, Long> employeeTeamMap = new HashMap<>();
        
        for (EmployeeCost cost : costs) {
            Long employeeId = cost.getEmployeeId();
            
            // Get team for this employee
            Long teamId = employeeTeamMap.computeIfAbsent(
                    employeeId, id -> getTeamIdForEmployee(id, teamMap));
            
            if (teamId == null) {
                // Employee not in any team, skip
                continue;
            }
            
            // Only include costs for employees in the requested teams
            if (!teamIds.contains(teamId)) {
                continue;
            }
            
            TeamPeriodKey key = new TeamPeriodKey(teamId, cost.getYear(), cost.getMonth());
            BigDecimal currentTotal = result.getOrDefault(key, BigDecimal.ZERO);
            result.put(key, currentTotal.add(cost.getCostAmount()));
        }
        
        return result;
    }
    
    /**
     * Lấy team ID cho nhân viên cụ thể dựa vào danh sách team đã lấy
     */
    private Long getTeamIdForEmployee(Long employeeId, Map<Long, Team> teamMap) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        if (employee.getTeam() == null) {
            return null;
        }
        
        return employee.getTeam().getId();
    }
    
    /**
     * Get aggregated revenues by team and period
     */
    private Map<TeamPeriodKey, BigDecimal> getTeamRevenuesByPeriod(List<Long> teamIds, DateRange dateRange) {
        Map<TeamPeriodKey, BigDecimal> result = new HashMap<>();
        
        // Get all employees from these teams
        List<Long> allEmployeeIds = new ArrayList<>();
        Map<Long, Team> teamMap = new HashMap<>();
        
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
            teamMap.put(teamId, team);
            
            Page<Employee> employeesPage = employeeRepository.findByTeam_Id(team.getId(), Pageable.unpaged());
            allEmployeeIds.addAll(employeesPage.getContent().stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList()));
        }
        
        if (allEmployeeIds.isEmpty()) {
            return result;
        }
        
        // Get revenues for all these employees in the date range
        List<EmployeeRevenue> revenues;
        if (dateRange.startYear == dateRange.endYear) {
            revenues = employeeRevenueRepository.findByEmployeeIdsAndPeriod(
                    allEmployeeIds, dateRange.startYear, dateRange.startMonth, dateRange.endMonth);
        } else {
            revenues = employeeRevenueRepository.findByEmployeeIdsAndDateRange(
                    allEmployeeIds, dateRange.startYear, dateRange.startMonth, 
                    dateRange.endYear, dateRange.endMonth);
        }
        
        // Group by team and period
        Map<Long, Long> employeeTeamMap = new HashMap<>();
        
        for (EmployeeRevenue revenue : revenues) {
            Long employeeId = revenue.getEmployeeId();
            
            // Get team for this employee
            Long teamId = employeeTeamMap.computeIfAbsent(
                    employeeId, id -> getTeamIdForEmployee(id, teamMap));
            
            if (teamId == null) {
                // Employee not in any team, skip
                continue;
            }
            
            // Only include revenues for employees in the requested teams
            if (!teamIds.contains(teamId)) {
                continue;
            }
            
            TeamPeriodKey key = new TeamPeriodKey(teamId, revenue.getYear(), revenue.getMonth());
            BigDecimal currentTotal = result.getOrDefault(key, BigDecimal.ZERO);
            result.put(key, currentTotal.add(revenue.getCalculatedRevenue()));
        }
        
        return result;
    }
    
    /**
     * Get employee counts for each team
     */
    private Map<Long, Integer> getTeamEmployeeCounts(List<Long> teamIds) {
        Map<Long, Integer> counts = new HashMap<>();
        
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
            
            Page<Employee> employeesPage = employeeRepository.findByTeam_Id(team.getId(), Pageable.unpaged());
            int count = employeesPage.getContent().size();
            counts.put(teamId, count);
        }
        
        return counts;
    }
    
    /**
     * Build the margin summary DTO
     */
    private MarginSummaryDTO buildMarginSummary(
            List<Long> teamIds, 
            String period, 
            DateRange dateRange,
            Map<TeamPeriodKey, BigDecimal> teamCosts,
            Map<TeamPeriodKey, BigDecimal> teamRevenues,
            Map<Long, Integer> teamEmployeeCounts,
            String groupBy) {
        
        // Create team margin DTOs
        List<MarginSummaryDTO.TeamMarginDTO> teamMargins = new ArrayList<>();
        
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalEmployees = 0;
        
        // Tạo danh sách tất cả các kỳ trong khoảng thời gian đã chọn
        List<YearMonth> allPeriods = new ArrayList<>();
        for (int year = dateRange.startYear; year <= dateRange.endYear; year++) {
            int startMonth = (year == dateRange.startYear) ? dateRange.startMonth : 1;
            int endMonth = (year == dateRange.endYear) ? dateRange.endMonth : 12;
            
            for (int month = startMonth; month <= endMonth; month++) {
                allPeriods.add(YearMonth.of(year, month));
            }
        }
        
        // Lấy thông tin team từ database
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId).orElse(null);
            if (team == null) {
                continue;
            }
            
            String teamName = team.getName();
            int employeeCount = teamEmployeeCounts.getOrDefault(teamId, 0);
            totalEmployees += employeeCount;
            
            // Calculate totals for this team
            BigDecimal teamTotalCost = BigDecimal.ZERO;
            BigDecimal teamTotalRevenue = BigDecimal.ZERO;
            
            // Create period margins
            List<MarginSummaryDTO.PeriodMarginDTO> periodMargins = new ArrayList<>();
            Map<String, Integer> statusCounts = new HashMap<>();
            statusCounts.put("Red", 0);
            statusCounts.put("Yellow", 0);
            statusCounts.put("Green", 0);
            
            // Tạo dữ liệu cho các trend
            List<BigDecimal> marginTrend = new ArrayList<>();
            List<String> periodLabels = new ArrayList<>();
            
            for (YearMonth yearMonth : allPeriods) {
                int year = yearMonth.getYear();
                int month = yearMonth.getMonthValue();
                TeamPeriodKey key = new TeamPeriodKey(teamId, year, month);
                
                BigDecimal cost = teamCosts.getOrDefault(key, BigDecimal.ZERO);
                BigDecimal revenue = teamRevenues.getOrDefault(key, BigDecimal.ZERO);
                BigDecimal margin = calculateMargin(revenue, cost);
                String marginStatus = calculateMarginStatus(margin);
                
                // Cập nhật số lượng theo status
                statusCounts.put(marginStatus, statusCounts.getOrDefault(marginStatus, 0) + 1);
                
                // Add to totals
                teamTotalCost = teamTotalCost.add(cost);
                teamTotalRevenue = teamTotalRevenue.add(revenue);
                
                // Add to trend data
                marginTrend.add(margin);
                periodLabels.add(formatPeriodLabel(year, month, "month"));
                
                // Create period margin
                MarginSummaryDTO.PeriodMarginDTO periodMargin = MarginSummaryDTO.PeriodMarginDTO.builder()
                        .period(key.getPeriodString())
                        .periodLabel(formatPeriodLabel(year, month, period))
                        .cost(cost)
                        .revenue(revenue)
                        .margin(margin)
                        .marginStatus(marginStatus)
                        .build();
                        
                periodMargins.add(periodMargin);
            }
            
            // Calculate overall margin for this team
            BigDecimal teamMargin = calculateMargin(teamTotalRevenue, teamTotalCost);
            String teamMarginStatus = calculateMarginStatus(teamMargin);
            
            // Tạo đối tượng trend
            MarginSummaryDTO.TeamMarginDTO.TrendsDTO trendsDTO = MarginSummaryDTO.TeamMarginDTO.TrendsDTO.builder()
                    .margin(marginTrend)
                    .periods(periodLabels)
                    .build();
            
            // Add to global totals
            totalCost = totalCost.add(teamTotalCost);
            totalRevenue = totalRevenue.add(teamTotalRevenue);
            
            // Create team margin DTO
            MarginSummaryDTO.TeamMarginDTO teamMarginDTO = MarginSummaryDTO.TeamMarginDTO.builder()
                    .id(teamId)
                    .name(teamName)
                    .employeeCount(employeeCount)
                    .cost(teamTotalCost)
                    .revenue(teamTotalRevenue)
                    .margin(teamMargin)
                    .marginStatus(teamMarginStatus)
                    .statusCounts(statusCounts)
                    .periods(periodMargins)
                    .trends(trendsDTO)
                    .build();
                    
            teamMargins.add(teamMarginDTO);
        }
        
        // Calculate overall averages
        BigDecimal averageCost = totalEmployees > 0 ? 
                totalCost.divide(BigDecimal.valueOf(totalEmployees), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
        BigDecimal averageRevenue = totalEmployees > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalEmployees), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
        BigDecimal averageMargin = calculateMargin(totalRevenue, totalCost);
        
        // Tính tổng số lượng theo trạng thái
        Map<String, Integer> totalStatusCounts = new HashMap<>();
        totalStatusCounts.put("Red", 0);
        totalStatusCounts.put("Yellow", 0);
        totalStatusCounts.put("Green", 0);
        
        for (MarginSummaryDTO.TeamMarginDTO team : teamMargins) {
            if (team.getStatusCounts() != null) {
                for (Map.Entry<String, Integer> entry : team.getStatusCounts().entrySet()) {
                    totalStatusCounts.put(entry.getKey(), 
                            totalStatusCounts.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        }
        
        // Create summary
        String periodLabel = formatPeriodLabel(dateRange.startYear, dateRange.startMonth, period);
        
        MarginSummaryDTO.SummaryDTO summary = MarginSummaryDTO.SummaryDTO.builder()
                .period(period)
                .periodLabel(periodLabel)
                .totalTeams(teamIds.size())
                .totalEmployees(totalEmployees)
                .averageCost(averageCost)
                .averageRevenue(averageRevenue)
                .averageMargin(averageMargin)
                .statusCounts(totalStatusCounts)
                .build();
        
        // Group by status if requested
        if ("status".equals(groupBy)) {
            teamMargins = groupByStatus(teamMargins);
        }
        
        return MarginSummaryDTO.builder()
                .summary(summary)
                .teams(teamMargins)
                .build();
    }
    
    /**
     * Group team margins by status instead of by team
     */
    private List<MarginSummaryDTO.TeamMarginDTO> groupByStatus(List<MarginSummaryDTO.TeamMarginDTO> teamMargins) {
        // Khi nhóm theo status, chúng ta tạo 3 nhóm: Red, Yellow, Green
        // và gộp các số liệu của team có cùng status
        Map<String, MarginSummaryDTO.TeamMarginDTO> statusGroups = new HashMap<>();
        
        // Khởi tạo 3 nhóm status
        String[] statuses = {"Red", "Yellow", "Green"};
        for (String status : statuses) {
            MarginSummaryDTO.TeamMarginDTO groupDTO = MarginSummaryDTO.TeamMarginDTO.builder()
                    .id(0L) // ID = 0 cho các nhóm status
                    .name(status)
                    .employeeCount(0)
                    .cost(BigDecimal.ZERO)
                    .revenue(BigDecimal.ZERO)
                    .margin(BigDecimal.ZERO)
                    .marginStatus(status)
                    .statusCounts(new HashMap<>())
                    .periods(new ArrayList<>())
                    .build();
            
            statusGroups.put(status, groupDTO);
        }
        
        // Gộp các team có cùng status
        for (MarginSummaryDTO.TeamMarginDTO team : teamMargins) {
            String status = team.getMarginStatus();
            if (status == null || !statusGroups.containsKey(status)) {
                continue;
            }
            
            MarginSummaryDTO.TeamMarginDTO group = statusGroups.get(status);
            
            // Cộng dồn số lượng nhân viên
            group.setEmployeeCount(group.getEmployeeCount() + team.getEmployeeCount());
            
            // Cộng dồn chi phí và doanh thu
            group.setCost(group.getCost().add(team.getCost()));
            group.setRevenue(group.getRevenue().add(team.getRevenue()));
            
            // Cập nhật margin (tính lại dựa trên tổng doanh thu và chi phí)
            BigDecimal newMargin = calculateMargin(group.getRevenue(), group.getCost());
            group.setMargin(newMargin);
            
            // Cập nhật status counts
            if (team.getStatusCounts() != null) {
                for (Map.Entry<String, Integer> entry : team.getStatusCounts().entrySet()) {
                    Integer currentCount = group.getStatusCounts().getOrDefault(entry.getKey(), 0);
                    group.getStatusCounts().put(entry.getKey(), currentCount + entry.getValue());
                }
            }
            
            // Gộp các period nếu cần
            // (Trong thực tế, có thể cần logic phức tạp hơn để gộp dữ liệu theo từng kỳ)
        }
        
        // Chuyển từ map về list và chỉ lấy các nhóm có dữ liệu
        return statusGroups.values().stream()
                .filter(group -> group.getEmployeeCount() > 0)
                .collect(Collectors.toList());
    }
    
    /**
     * Apply view-specific formatting
     */
    private void applyViewFormatting(MarginSummaryDTO summaryDTO, String view) {
        if ("chart".equals(view)) {
            // Đối với biểu đồ, chúng ta có thể thêm dữ liệu thống kê, series, ... nếu cần
            // Ví dụ: thêm trend data cho mỗi team
            
            // Không cần thay đổi gì vì dữ liệu cho chart đã được tính toán trong buildMarginSummary
        } else {
            // Đối với bảng, không cần thay đổi gì vì format mặc định đã phù hợp
        }
    }
    
    /**
     * Parse employee costs from uploaded file
     */
    private List<EmployeeCost> parseEmployeeCostsFromFile(MultipartFile file, int year, int month) {
        log.info("Parsing employee costs from file for {}-{}", year, month);
        List<EmployeeCost> costs = new ArrayList<>();
        
        try {
            // Xác định loại file (Excel hoặc CSV) dựa vào phần mở rộng
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new BadRequestException("Invalid file name");
            }
            
            if (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls")) {
                // Xử lý file Excel
                costs = parseExcelFile(file, year, month);
            } else if (originalFilename.endsWith(".csv")) {
                // Xử lý file CSV
                costs = parseCsvFile(file, year, month);
            } else {
                throw new BadRequestException("Unsupported file format. Please upload Excel or CSV file");
            }
            
            log.info("Successfully parsed {} employee cost records", costs.size());
            return costs;
            
        } catch (Exception e) {
            log.error("Error parsing employee costs file: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to parse file: " + e.getMessage());
        }
    }
    
    /**
     * Parse Excel file to extract employee costs
     */
    private List<EmployeeCost> parseExcelFile(MultipartFile file, int year, int month) {
        List<EmployeeCost> costs = new ArrayList<>();
        
        try {
            // Use Apache POI to read Excel file
            org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file.getInputStream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            
            // Expected columns: Employee ID, Basic Salary, Allowance, Overtime, Other Costs, Currency, Note
            int rowNum = 0;
            for (org.apache.poi.ss.usermodel.Row row : sheet) {
                if (rowNum == 0) {
                    // Skip header row
                    rowNum++;
                    continue;
                }
                
                try {
                    // Read Employee ID (column 0)
                    org.apache.poi.ss.usermodel.Cell employeeIdCell = row.getCell(0);
                    if (employeeIdCell == null) {
                        log.warn("Skipping row {} - Employee ID is empty", rowNum);
                        continue;
                    }
                    
                    Long employeeId;
                    if (employeeIdCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                        employeeId = (long) employeeIdCell.getNumericCellValue();
                    } else {
                        employeeId = Long.parseLong(employeeIdCell.getStringCellValue());
                    }
                    
                    // Validate employee exists
                    if (!employeeExists(employeeId)) {
                        log.warn("Skipping row {} - Employee ID {} not found", rowNum, employeeId);
                        continue;
                    }
                    
                    // Read Basic Salary (column 1)
                    BigDecimal basicSalary = getCellValueAsBigDecimal(row.getCell(1));
                    
                    // Read Allowance (column 2)
                    BigDecimal allowance = getCellValueAsBigDecimal(row.getCell(2));
                    
                    // Read Overtime (column 3)
                    BigDecimal overtime = getCellValueAsBigDecimal(row.getCell(3));
                    
                    // Read Other Costs (column 4)
                    BigDecimal otherCosts = getCellValueAsBigDecimal(row.getCell(4));
                    
                    // Read Currency (column 5)
                    String currency = getCellValueAsString(row.getCell(5));
                    if (currency == null || currency.trim().isEmpty()) {
                        currency = "USD"; // Default currency
                    }
                    
                    // Read Note (column 6)
                    String note = getCellValueAsString(row.getCell(6));
                    
                    // Calculate total cost
                    BigDecimal totalCost = basicSalary.add(allowance).add(overtime).add(otherCosts);
                    
                    // Create EmployeeCost object
                    EmployeeCost cost = EmployeeCost.builder()
                            .employeeId(employeeId)
                            .year(year)
                            .month(month)
                            .basicSalary(basicSalary)
                            .allowance(allowance)
                            .overtime(overtime)
                            .otherCosts(otherCosts)
                            .costAmount(totalCost)
                            .currency(currency)
                            .note(note != null ? note : "Imported from Excel file")
                            .build();
                    
                    costs.add(cost);
                    
                } catch (Exception e) {
                    log.warn("Error parsing row {}: {}", rowNum, e.getMessage());
                }
                
                rowNum++;
            }
            
            workbook.close();
            log.info("Successfully parsed {} employee cost records from Excel file", costs.size());
            
        } catch (Exception e) {
            log.error("Error parsing Excel file: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }
        
        return costs;
    }
    
    /**
     * Helper method to get cell value as BigDecimal
     */
    private BigDecimal getCellValueAsBigDecimal(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return BigDecimal.ZERO;
        }
        
        try {
            if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
            }
        } catch (Exception e) {
            log.warn("Error parsing cell value as BigDecimal: {}", e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Helper method to get cell value as String
     */
    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return null;
        }
        
        try {
            if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                return String.valueOf((long) cell.getNumericCellValue());
            }
        } catch (Exception e) {
            log.warn("Error parsing cell value as String: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Parse CSV file to extract employee costs
     */
    private List<EmployeeCost> parseCsvFile(MultipartFile file, int year, int month) {
        List<EmployeeCost> costs = new ArrayList<>();
        
        try {
            // Use BufferedReader to read CSV file
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            
            String line;
            int rowNum = 0;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    // Skip header row
                    isHeader = false;
                    rowNum++;
                    continue;
                }
                
                try {
                    // Split CSV line by comma, handling quoted values
                    String[] values = parseCsvLine(line);
                    
                    if (values.length < 5) {
                        log.warn("Skipping row {} - insufficient columns (expected at least 5, got {})", rowNum, values.length);
                        continue;
                    }
                    
                    // Parse Employee ID (column 0)
                    Long employeeId;
                    try {
                        employeeId = Long.parseLong(values[0].trim());
                    } catch (NumberFormatException e) {
                        log.warn("Skipping row {} - invalid Employee ID: {}", rowNum, values[0]);
                        continue;
                    }
                    
                    // Validate employee exists
                    if (!employeeExists(employeeId)) {
                        log.warn("Skipping row {} - Employee ID {} not found", rowNum, employeeId);
                        continue;
                    }
                    
                    // Parse Basic Salary (column 1)
                    BigDecimal basicSalary = parseBigDecimalFromString(values[1]);
                    
                    // Parse Allowance (column 2)
                    BigDecimal allowance = parseBigDecimalFromString(values[2]);
                    
                    // Parse Overtime (column 3)
                    BigDecimal overtime = parseBigDecimalFromString(values[3]);
                    
                    // Parse Other Costs (column 4)
                    BigDecimal otherCosts = parseBigDecimalFromString(values[4]);
                    
                    // Parse Currency (column 5, optional)
                    String currency = "USD"; // Default
                    if (values.length > 5 && values[5] != null && !values[5].trim().isEmpty()) {
                        currency = values[5].trim();
                    }
                    
                    // Parse Note (column 6, optional)
                    String note = "Imported from CSV file";
                    if (values.length > 6 && values[6] != null && !values[6].trim().isEmpty()) {
                        note = values[6].trim();
                    }
                    
                    // Calculate total cost
                    BigDecimal totalCost = basicSalary.add(allowance).add(overtime).add(otherCosts);
                    
                    // Create EmployeeCost object
                    EmployeeCost cost = EmployeeCost.builder()
                            .employeeId(employeeId)
                            .year(year)
                            .month(month)
                            .basicSalary(basicSalary)
                            .allowance(allowance)
                            .overtime(overtime)
                            .otherCosts(otherCosts)
                            .costAmount(totalCost)
                            .currency(currency)
                            .note(note)
                            .build();
                    
                    costs.add(cost);
                    
                } catch (Exception e) {
                    log.warn("Error parsing CSV row {}: {}", rowNum, e.getMessage());
                }
                
                rowNum++;
            }
            
            reader.close();
            log.info("Successfully parsed {} employee cost records from CSV file", costs.size());
            
        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }
        
        return costs;
    }
    
    /**
     * Parse CSV line handling quoted values and commas within quotes
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        result.add(currentField.toString());
        
        return result.toArray(new String[0]);
    }
    
    /**
     * Helper method to parse BigDecimal from string
     */
    private BigDecimal parseBigDecimalFromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Remove any quotes and trim
            String cleanValue = value.replace("\"", "").trim();
            return cleanValue.isEmpty() ? BigDecimal.ZERO : new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("Error parsing BigDecimal from string '{}': {}", value, e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Check if employee exists
     */
    private boolean employeeExists(Long employeeId) {
        if (employeeId == null) {
            return false;
        }
        
        return employeeRepository.findById(employeeId).isPresent();
    }

    private EmployeeMarginDTO.TeamDTO getTeamForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        if (employee.getTeam() == null) {
            return null;
        }
        
        Team team = employee.getTeam();
        
        return EmployeeMarginDTO.TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .build();
    }

    /**
     * Get the ID of the currently authenticated user
     * 
     * @return the user ID, or null if not authenticated
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getId();
            }
            return null;
        } catch (Exception e) {
            log.warn("Could not get current user ID: {}", e.getMessage());
            return null;
        }
    }
} 
