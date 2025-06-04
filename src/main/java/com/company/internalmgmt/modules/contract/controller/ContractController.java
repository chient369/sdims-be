package com.company.internalmgmt.modules.contract.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.util.SecurityUtils;
import com.company.internalmgmt.modules.contract.dto.ContractDTO;
import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;
import com.company.internalmgmt.modules.contract.dto.ContractFileDTO;
import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;
import com.company.internalmgmt.modules.contract.dto.request.ContractCreateRequest;
import com.company.internalmgmt.modules.contract.dto.request.ContractUpdateRequest;
import com.company.internalmgmt.modules.contract.service.ContractEmployeeService;
import com.company.internalmgmt.modules.contract.service.ContractFileService;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;
import com.company.internalmgmt.modules.contract.service.ContractService;

/**
 * REST controller for managing contracts
 */
@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;
    
    @Autowired
    private ContractPaymentTermService paymentTermService;
    
    @Autowired
    private ContractEmployeeService contractEmployeeService;
    
    @Autowired
    private ContractFileService contractFileService;
    
    /**
     * GET /api/v1/contracts : Get all contracts with optional filtering
     * 
     * @param customerName       filter by customer name
     * @param contractCode       filter by contract code
     * @param status             filter by contract status
     * @param contractType       filter by contract type
     * @param salesId            filter by sales person ID
     * @param minAmount          filter by minimum amount
     * @param maxAmount          filter by maximum amount
     * @param fromDate           filter by start date from
     * @param toDate             filter by start date to
     * @param paymentStatus      filter by payment status
     * @param page               page number (1-based, will be converted to 0-based)
     * @param size               page size
     * @param sortBy             field to sort by
     * @param sortDirection      sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the list of contracts in body
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getAllContracts(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String contractCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) Long salesId,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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
        
        Page<ContractDTO> contracts = contractService.searchContracts(
                customerName, contractCode, status, contractType, salesId, 
                minAmount, maxAmount, fromDate, toDate, paymentStatus, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
    
    /**
     * GET /api/v1/contracts/:id : Get a specific contract by ID
     * 
     * @param id                  the ID of the contract to retrieve
     * @param includePaymentTerms whether to include payment terms
     * @param includeEmployees    whether to include employees
     * @param includeFiles        whether to include files
     * @return the ResponseEntity with status 200 (OK) and the contract in body
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<ContractDTO>> getContractById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "true") Boolean includePaymentTerms,
            @RequestParam(required = false, defaultValue = "true") Boolean includeEmployees,
            @RequestParam(required = false, defaultValue = "true") Boolean includeFiles) {
        
        ContractDTO contract = contractService.getContractById(id, includePaymentTerms, includeEmployees, includeFiles);
        
        return ResponseEntity.ok(ApiResponse.success(contract));
    }
    
    /**
     * GET /api/v1/contracts/code/:contractCode : Get a specific contract by code
     * 
     * @param contractCode the code of the contract to retrieve
     * @return the ResponseEntity with status 200 (OK) and the contract in body
     */
    @GetMapping("/code/{contractCode}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<ContractDTO>> getContractByCode(@PathVariable String contractCode) {
        ContractDTO contract = contractService.getContractByCode(contractCode);
        
        return ResponseEntity.ok(ApiResponse.success(contract));
    }
    
    /**
     * POST /api/v1/contracts : Create a new contract
     * 
     * @param request the contract to create
     * @param authentication the authentication object
     * @return the ResponseEntity with status 201 (Created) and the new contract in body
     */
    @PostMapping
    @PreAuthorize("hasAuthority('contract:create')")
    public ResponseEntity<ApiResponse<ContractDTO>> createContract(
            @Valid @RequestBody ContractCreateRequest request,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractDTO result = contractService.createContract(request, currentUserId);
        
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/v1/contracts/:id : Update an existing contract
     * 
     * @param id the ID of the contract to update
     * @param request the contract to update
     * @param authentication the authentication object
     * @return the ResponseEntity with status 200 (OK) and the updated contract in body
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('contract:update:all', 'contract:update:own')")
    public ResponseEntity<ApiResponse<ContractDTO>> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractUpdateRequest request,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractDTO result = contractService.updateContract(id, request, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * DELETE /api/v1/contracts/:id : Delete a contract
     * 
     * @param id the ID of the contract to delete
     * @param authentication the authentication object
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('contract:delete')")
    public ResponseEntity<Void> deleteContract(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        contractService.deleteContract(id, currentUserId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/v1/contracts/:id/payment-terms : Get payment terms for a contract
     * 
     * @param id the ID of the contract
     * @return the ResponseEntity with status 200 (OK) and the list of payment terms in body
     */
    @GetMapping("/{id}/payment-terms")
    //@PreAuthorize("hasAnyAuthority('payment-term:read:all', 'payment-term:read:own', 'payment-term:read:assigned')")
    public ResponseEntity<ApiResponse<List<ContractPaymentTermDTO>>> getPaymentTermsByContractId(@PathVariable Long id) {
        List<ContractPaymentTermDTO> paymentTerms = paymentTermService.getPaymentTermsByContractId(id);
        
        return ResponseEntity.ok(ApiResponse.success(paymentTerms));
    }
    
    /**
     * GET /api/v1/contracts/:id/employees : Get employees assigned to a contract
     * 
     * @param id the ID of the contract
     * @return the ResponseEntity with status 200 (OK) and the list of contract employees in body
     */
    @GetMapping("/{id}/employees")
    //@PreAuthorize("hasAnyAuthority('contract-link:read:all', 'contract-link:read:own', 'contract-link:read:assigned')")
    public ResponseEntity<ApiResponse<List<ContractEmployeeDTO>>> getEmployeesByContractId(@PathVariable Long id) {
        List<ContractEmployeeDTO> employees = contractEmployeeService.getContractEmployeesByContractId(id);
        
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    /**
     * GET /api/v1/contracts/:id/files : Get files attached to a contract
     * 
     * @param id the ID of the contract
     * @return the ResponseEntity with status 200 (OK) and the list of contract files in body
     */
    @GetMapping("/{id}/files")
    //@PreAuthorize("hasAnyAuthority('contract-file:read:all', 'contract-file:read:own', 'contract-file:read:assigned')")
    public ResponseEntity<ApiResponse<List<ContractFileDTO>>> getFilesByContractId(@PathVariable Long id) {
        List<ContractFileDTO> files = contractFileService.getContractFilesByContractId(id);
        
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    /**
     * GET /api/v1/contracts/employee/:employeeId : Get contracts by employee ID
     * 
     * @param employeeId the ID of the employee
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the list of contracts in body
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getContractsByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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
        
        Page<ContractDTO> contracts = contractService.getContractsByEmployeeId(employeeId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
    
    /**
     * GET /api/v1/contracts/team/:teamId : Get contracts by team ID
     * 
     * @param teamId the ID of the team
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the list of contracts in body
     */
    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getContractsByTeamId(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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
        
        Page<ContractDTO> contracts = contractService.getContractsByTeamId(teamId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
    
    /**
     * GET /api/v1/contracts/opportunity/:opportunityId : Get contracts by opportunity ID
     * 
     * @param opportunityId the ID of the opportunity
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the list of contracts in body
     */
    @GetMapping("/opportunity/{opportunityId}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own', 'contract:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getContractsByOpportunityId(
            @PathVariable Long opportunityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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
        
        Page<ContractDTO> contracts = contractService.getContractsByOpportunityId(opportunityId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
    
    /**
     * GET /api/v1/contracts/sales/:salesId : Get contracts by sales ID
     * 
     * @param salesId the ID of the sales person
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the list of contracts in body
     */
    @GetMapping("/sales/{salesId}")
    @PreAuthorize("hasAnyAuthority('contract:read:all', 'contract:read:own')")
    public ResponseEntity<ApiResponse<Page<ContractDTO>>> getContractsBySalesId(
            @PathVariable Long salesId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
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
        
        Page<ContractDTO> contracts = contractService.getContractsBySalesId(salesId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }
} 