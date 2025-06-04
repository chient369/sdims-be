package com.company.internalmgmt.modules.hrm.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.internalmgmt.common.dto.PageResponseDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeRequest;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.service.EmployeeService;
import com.company.internalmgmt.modules.hrm.model.Employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * REST controller for managing employees
 */
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    
    /**
     * GET /api/v1/employees : Get all employees with filtering options
     *
     * @param keyword search term for name or employee code (optional)
     * @param teamId filter by team ID (optional)
     * @param position filter by position (optional)
     * @param status filter by status (optional)
     * @param skills filter by skill IDs (optional)
     * @param minExperience filter by minimum experience years (optional)
     * @param marginStatus filter by margin status (optional)
     * @param page page number (default 1)
     * @param size page size (default 10)
     * @param sortBy sort field (default "name")
     * @param sortDir sort direction (default "asc")
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping
    @Operation(summary = "Get all employees", description = "Get all employees with filtering options")
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team', 'employee:read:basic', 'employee:read:own')")
    public ResponseEntity<PageResponseDto<?>> getAllEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<Integer> skills,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) String marginStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("REST request to get employees with filters: keyword={}, teamId={}, status={}", keyword, teamId, status);
        
        // Validate parameters
        if (page < 1) {
            return ResponseEntity.badRequest().body(PageResponseDto.error("E2000", "Page number must be at least 1"));
        }
        
        if (size < 1 || size > 100) {
            return ResponseEntity.badRequest().body(PageResponseDto.error("E2000", "Page size must be between 1 and 100"));
        }
        
        if (status != null && !isValidStatus(status)) {
            return ResponseEntity.badRequest().body(PageResponseDto.error("E2000", "Invalid status value"));
        }
        
        if (marginStatus != null && !isValidMarginStatus(marginStatus)) {
            return ResponseEntity.badRequest().body(PageResponseDto.error("E2000", "Invalid margin status value"));
        }
        
        // Convert to 0-based page index for Spring Pageable
        int pageIndex = page - 1;
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));
        
        // Call service with all filter parameters
        Page<EmployeeDto> employeesPage = employeeService.findEmployees(keyword, teamId, position, status, 
                skills, minExperience, marginStatus, pageable);
        
        // Create a sort string in the format "field,direction"
        String sortString = sortBy + "," + sortDir.toLowerCase();
        
        // Create the response
        return ResponseEntity.ok(
                PageResponseDto.success(
                        employeesPage.getContent(),
                        page, // Return the 1-based page number
                        size,
                        employeesPage.getTotalElements(),
                        employeesPage.getTotalPages(),
                        sortString
                )
        );
    }
    
    /**
     * Check if status is valid
     */
    private boolean isValidStatus(String status) {
        return status.matches("^(Allocated|Available|EndingSoon|OnLeave|Resigned)$");
    }
    
    /**
     * Check if margin status is valid
     */
    private boolean isValidMarginStatus(String marginStatus) {
        return marginStatus.matches("^(Red|Yellow|Green)$");
    }
    
    /**
     * GET /api/v1/employees/{id} : Get employee by ID
     *
     * @param id the ID of the employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employee, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID", description = "Get detailed information about an employee by ID")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        log.info("REST request to get Employee by ID: {}", id);
        
        EmployeeDto employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * GET /api/v1/employees/code/{code} : Get employee by employee code
     *
     * @param code the code of the employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employee, or with status 404 (Not Found)
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get an employee by code", description = "Get detailed information about an employee by employee code")
    public ResponseEntity<EmployeeDto> getEmployeeByCode(@PathVariable String code) {
        log.info("REST request to get Employee by code: {}", code);
        
        EmployeeDto employee = employeeService.findByEmployeeCode(code);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * POST /api/v1/employees : Create a new employee
     *
     * @param request the employee to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employee
     */
    @PostMapping
    @Operation(summary = "Create a new employee", description = "Create a new employee")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        log.info("REST request to create Employee: {}", request);
        
        EmployeeDto result = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * PUT /api/v1/employees/{id} : Update an existing employee
     *
     * @param id the ID of the employee to update
     * @param request the employee to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employee
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an employee", description = "Update an existing employee")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        
        log.info("REST request to update Employee: {}", id);
        
        System.out.println("request: " + request);

        EmployeeDto result = employeeService.update(id, request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * DELETE /api/v1/employees/{id} : Delete an employee
     *
     * @param id the ID of the employee to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Delete an employee by ID (soft delete)")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("REST request to delete Employee: {}", id);
        
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * PUT /api/v1/employees/{id}/status : Update employee status
     *
     * @param id the ID of the employee to update
     * @param request the status update request
     * @return the ResponseEntity with status 200 (OK) and with body the updated employee
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update employee status", description = "Update the status of an employee (available, allocated, etc.)")
    public ResponseEntity<EmployeeDto> updateEmployeeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        
        log.info("REST request to update Employee status: {}, status: {}", id, request.getStatus());
        
        employeeService.updateStatus(id, request);
        EmployeeDto result = employeeService.findById(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/v1/employees/search/suggest : Suggest employees based on skills
     *
     * @param skillIds the skill IDs to match
     * @param minExperience the minimum years of experience
     * @return the ResponseEntity with status 200 (OK) and with body the list of suggested employees
     */
    @GetMapping("/search/suggest")
    @Operation(summary = "Suggest employees by skills", description = "Find employees that match the requested skills")
    public ResponseEntity<List<EmployeeDto>> suggestEmployeesBySkills(
            @RequestParam List<Long> skillIds,
            @RequestParam(required = false) Double minExperience) {
        
        log.info("REST request to suggest employees by skills: {}, min experience: {}", skillIds, minExperience);
        
        List<EmployeeDto> employees = employeeService.suggestEmployeesBySkills(skillIds, minExperience);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * POST /api/v1/employees/import : Import employees from file
     *
     * @param file the file to import
     * @return the ResponseEntity with status 200 (OK) and with body the list of imported employees
     */
    @PostMapping("/import")
    @Operation(summary = "Import employees", description = "Import employees from Excel file")
    public ResponseEntity<String> importEmployees(@RequestParam("file") MultipartFile file) {
        try {
            log.info("REST request to import employees from file");
            
            List<Employee> importedEmployees = employeeService.importEmployees(file.getBytes());
            return ResponseEntity.ok("Successfully imported " + importedEmployees.size() + " employees");
        } catch (Exception e) {
            log.error("Error importing employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import employees: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/v1/employees/export : Export employees to file
     *
     * @param filters the filters to apply
     * @return the ResponseEntity with status 200 (OK) and with body the exported file
     */
    @GetMapping("/export")
    @Operation(summary = "Export employees", description = "Export employees to Excel file based on filters")
    public ResponseEntity<byte[]> exportEmployees(
            @RequestParam(required = false) String filters) {
        
        log.info("REST request to export employees with filters: {}", filters);
        
        byte[] fileContent = employeeService.exportEmployees(filters);
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=employees.xlsx")
                .body(fileContent);
    }
} 
