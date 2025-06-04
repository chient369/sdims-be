package com.company.internalmgmt.modules.margin.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.dto.request.ImportCostRequestDTO;
import com.company.internalmgmt.modules.margin.dto.request.UpdateCostRequestDTO;
import com.company.internalmgmt.modules.margin.service.MarginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

/**
 * REST controller for managing employee margins.
 */
@RestController
@RequestMapping("/api/v1/margins")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MarginController {

    private final MarginService marginService;

    /**
     * GET /api/v1/margins/employee : Get employee margins with various filters.
     *
     * @param employeeId Optional employee ID to filter by
     * @param teamId Optional team ID to filter by
     * @param period Period type (month, quarter, year)
     * @param fromDate Optional start date for filtering
     * @param toDate Optional end date for filtering
     * @param yearMonth Optional year-month (YYYY-MM) for filtering
     * @param yearQuarter Optional year-quarter (YYYY-QN) for filtering
     * @param year Optional year for filtering
     * @param status Optional margin status (Red, Yellow, Green) for filtering
     * @param page Page number (1-based)
     * @param size Number of records per page
     * @param sortBy Field to sort by
     * @param sortDir Sort direction (asc, desc)
     * @return the ResponseEntity with status 200 (OK) and the page of employee margins in body
     */
    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('margin:read:all', 'margin:read:team')")
    public ResponseEntity<ApiResponse<Page<EmployeeMarginDTO>>> getEmployeeMargins(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) String yearQuarter,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "margin") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Validate parameters
        if (page < 1) {
            ApiResponse<Page<EmployeeMarginDTO>> errorResponse = ApiResponse.error("E2000", "Page number must be at least 1");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (size < 1 || size > 100) {
            ApiResponse<Page<EmployeeMarginDTO>> errorResponse = ApiResponse.error("E2000", "Page size must be between 1 and 100");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Convert to 0-based page index for Spring Pageable
        int pageIndex = page - 1;
        
        Direction direction = sortDir.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));
        
        log.info("Fetching employee margins with page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);
        
        Page<EmployeeMarginDTO> result = marginService.getEmployeeMargins(
                employeeId, teamId, period, fromDate, toDate, yearMonth, yearQuarter, year, status, pageable);
                
        ApiResponse<Page<EmployeeMarginDTO>> apiResponse = ApiResponse.success(result);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * GET /api/v1/margins/summary : Get margin summary data for dashboard.
     *
     * @param teamId Optional team ID to filter by
     * @param period Period type (month, quarter, year)
     * @param fromDate Optional start date for filtering
     * @param toDate Optional end date for filtering
     * @param yearMonth Optional year-month (YYYY-MM) for filtering
     * @param yearQuarter Optional year-quarter (YYYY-QN) for filtering
     * @param year Optional year for filtering
     * @param view View type (table, chart)
     * @param groupBy Group by criteria (team, status)
     * @return the ResponseEntity with status 200 (OK) and the margin summary in body
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('margin-summary:read:all', 'margin-summary:read:team')")
    public ResponseEntity<ApiResponse<MarginSummaryDTO>> getMarginSummary(
            @RequestParam(required = false) Long teamId,
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) String yearQuarter,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "table") String view,
            @RequestParam(defaultValue = "team") String groupBy) {

        MarginSummaryDTO result = marginService.getMarginSummary(
                teamId, period, fromDate, toDate, yearMonth, yearQuarter, year, view, groupBy);
                
        ApiResponse<MarginSummaryDTO> apiResponse = ApiResponse.success(result);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * POST /api/v1/margins/costs/import : Import employee costs from Excel or CSV file.
     *
     * @param file The Excel/CSV file containing cost data
     * @param request The import parameters including month, teamId and overwrite flag
     * @return the ResponseEntity with status 200 (OK) and import results in body
     */
    @PostMapping(value = "/costs/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('employee-cost:import')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importEmployeeCosts(
            @RequestParam MultipartFile file,
            @Valid ImportCostRequestDTO request) {

        Map<String, Object> result = marginService.importEmployeeCosts(file, request);
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(result);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * POST /api/v1/margins/costs : Update employee costs manually.
     *
     * @param request The cost update request containing employee cost data
     * @return the ResponseEntity with status 200 (OK) and update results in body
     */
    @PostMapping("/costs")
    @PreAuthorize("hasAnyAuthority('employee-cost:update:all', 'employee-cost:update:team')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateEmployeeCosts(
            @Valid @RequestBody UpdateCostRequestDTO request) {

        Map<String, Object> result = marginService.updateEmployeeCosts(request);
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(result);
        return ResponseEntity.ok(apiResponse);
    }
} 