package com.company.internalmgmt.modules.contract.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.util.SecurityUtils;
import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;
import com.company.internalmgmt.modules.contract.service.ContractEmployeeService;

/**
 * REST controller for managing contract employees
 */
@RestController
@RequestMapping("/api/v1/contracts/employees")
public class ContractEmployeeController {

    @Autowired
    private ContractEmployeeService contractEmployeeService;
    
    /**
     * GET /api/v1/contracts/employees/:id : Get a specific contract employee by ID
     * 
     * @param id the ID of the contract employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and the contract employee in body
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('contract-link:read:all', 'contract-link:read:own', 'contract-link:read:assigned')")
    public ResponseEntity<ApiResponse<ContractEmployeeDTO>> getContractEmployeeById(@PathVariable Long id) {
        ContractEmployeeDTO contractEmployee = contractEmployeeService.getContractEmployeeById(id);
        
        return ResponseEntity.ok(ApiResponse.success(contractEmployee));
    }
    
    /**
     * POST /api/v1/contracts/employees : Assign an employee to a contract
     * 
     * @param contractId the ID of the contract
     * @param employeeId the ID of the employee
     * @param role the role of the employee in the contract
     * @param startDate the start date of assignment
     * @param endDate the end date of assignment
     * @param allocationPercentage the allocation percentage
     * @param billRate the bill rate
     * @param authentication the authentication object
     * @return the ResponseEntity with status 201 (Created) and the new assignment in body
     */
    @PostMapping
    //@PreAuthorize("hasAnyAuthority('contract-link:update:all', 'contract-link:update:own', 'contract-link:update:assigned')")
    public ResponseEntity<ApiResponse<ContractEmployeeDTO>> assignEmployeeToContract(
            @RequestParam Long contractId,
            @RequestParam Long employeeId,
            @RequestParam(required = false) String role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "100") BigDecimal allocationPercentage,
            @RequestParam(required = false) BigDecimal billRate,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractEmployeeDTO result = contractEmployeeService.assignEmployeeToContract(
                contractId, employeeId, role, startDate, endDate, allocationPercentage, billRate, currentUserId);
        
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/v1/contracts/employees/:id : Update a contract employee assignment
     * 
     * @param id the ID of the contract employee to update
     * @param role the role of the employee in the contract
     * @param startDate the start date of assignment
     * @param endDate the end date of assignment
     * @param allocationPercentage the allocation percentage
     * @param billRate the bill rate
     * @param authentication the authentication object
     * @return the ResponseEntity with status 200 (OK) and the updated assignment in body
     */
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('contract-link:update:all', 'contract-link:update:own', 'contract-link:update:assigned')")
    public ResponseEntity<ApiResponse<ContractEmployeeDTO>> updateContractEmployee(
            @PathVariable Long id,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal allocationPercentage,
            @RequestParam(required = false) BigDecimal billRate,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractEmployeeDTO result = contractEmployeeService.updateContractEmployeeAssignment(
                id, role, startDate, endDate, allocationPercentage, billRate, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * DELETE /api/v1/contracts/employees/:id : Remove an employee from a contract
     * 
     * @param id the ID of the contract employee to delete
     * @param authentication the authentication object
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('contract-link:update:all', 'contract-link:update:own')")
    public ResponseEntity<Void> removeEmployeeFromContract(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        contractEmployeeService.removeEmployeeFromContract(id, currentUserId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/v1/contracts/employees/employee/:employeeId : Get contracts for a specific employee
     * 
     * @param employeeId the ID of the employee
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of contract assignments in body
     */
    @GetMapping("/employee/{employeeId}")
    //@PreAuthorize("hasAnyAuthority('contract-link:read:all', 'contract-link:read:own', 'contract-link:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractEmployeeDTO>>> getContractsByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractEmployeeDTO> contracts = contractEmployeeService.getContractsByEmployeeIdPaged(employeeId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
} 