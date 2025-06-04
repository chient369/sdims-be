package com.company.internalmgmt.modules.margin.service;

import com.company.internalmgmt.modules.margin.dto.EmployeeMarginDTO;
import com.company.internalmgmt.modules.margin.dto.MarginSummaryDTO;
import com.company.internalmgmt.modules.margin.dto.request.ImportCostRequestDTO;
import com.company.internalmgmt.modules.margin.dto.request.UpdateCostRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MarginService {

    /**
     * Get employee margins with filters and pagination
     *
     * @param employeeId Optional filter for specific employee
     * @param teamId Optional filter for specific team
     * @param period Period type (month, quarter, year)
     * @param fromDate From date filter
     * @param toDate To date filter
     * @param yearMonth Specific year-month filter (YYYY-MM)
     * @param yearQuarter Specific year-quarter filter (YYYY-QN)
     * @param year Specific year filter
     * @param status Filter by margin status (Red, Yellow, Green)
     * @param pageable Pagination parameters
     * @return Page of employee margins
     */
    Page<EmployeeMarginDTO> getEmployeeMargins(
            Long employeeId,
            Long teamId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year,
            String status,
            Pageable pageable);

    /**
     * Get detailed margin data for a specific employee
     *
     * @param employeeId Employee ID
     * @param period Period type (month, quarter, year)
     * @param fromDate From date filter
     * @param toDate To date filter
     * @param yearMonth Specific year-month filter (YYYY-MM)
     * @param yearQuarter Specific year-quarter filter (YYYY-QN)
     * @param year Specific year filter
     * @return Employee margin details
     */
    EmployeeMarginDTO getEmployeeMarginDetail(
            Long employeeId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year);

    /**
     * Get summary margin data grouped by team or status
     *
     * @param teamId Optional filter for specific team
     * @param period Period type (month, quarter, year)
     * @param fromDate From date filter
     * @param toDate To date filter
     * @param yearMonth Specific year-month filter (YYYY-MM)
     * @param yearQuarter Specific year-quarter filter (YYYY-QN)
     * @param year Specific year filter
     * @param view View type (table, chart)
     * @param groupBy Group by parameter (team, status)
     * @return Margin summary data
     */
    MarginSummaryDTO getMarginSummary(
            Long teamId,
            String period,
            LocalDate fromDate,
            LocalDate toDate,
            String yearMonth,
            String yearQuarter,
            Integer year,
            String view,
            String groupBy);

    /**
     * Import employee costs from file
     *
     * @param file Excel or CSV file with cost data
     * @param request Import parameters
     * @return Import result with success/error counts
     */
    Map<String, Object> importEmployeeCosts(MultipartFile file, ImportCostRequestDTO request);

    /**
     * Update employee costs manually
     *
     * @param request Update request with employee costs
     * @return Update result with success/error counts
     */
    Map<String, Object> updateEmployeeCosts(UpdateCostRequestDTO request);

    /**
     * Calculate margin status based on margin percentage
     *
     * @param margin Margin percentage
     * @return Status string: "Red", "Yellow", or "Green"
     */
    String calculateMarginStatus(BigDecimal margin);

    /**
     * Get system-defined margin thresholds
     *
     * @return Map containing red and yellow thresholds
     */
    Map<String, BigDecimal> getMarginThresholds();
} 