package com.company.internalmgmt.modules.contract.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.util.SecurityUtils;
import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;
import com.company.internalmgmt.modules.contract.service.ContractPaymentTermService;

/**
 * REST controller for managing contract payment terms
 */
@RestController
@RequestMapping("/api/v1/contracts/payment-terms")
public class ContractPaymentTermController {

    @Autowired
    private ContractPaymentTermService paymentTermService;
    
    /**
     * GET /api/v1/contracts/payment-terms/:id : Get a specific payment term by ID
     * 
     * @param id the ID of the payment term to retrieve
     * @return the ResponseEntity with status 200 (OK) and the payment term in body
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('payment-term:read:all', 'payment-term:read:own', 'payment-term:read:assigned')")
    public ResponseEntity<ApiResponse<ContractPaymentTermDTO>> getPaymentTermById(@PathVariable Long id) {
        ContractPaymentTermDTO paymentTerm = paymentTermService.getPaymentTermById(id);
        
        return ResponseEntity.ok(ApiResponse.success(paymentTerm));
    }
    
    /**
     * PUT /api/v1/contracts/payment-terms/:id/status : Update payment term status
     * 
     * @param id the ID of the payment term to update
     * @param status the new status
     * @param paidDate the paid date
     * @param paidAmount the paid amount
     * @param notes any notes
     * @param authentication the authentication object
     * @return the ResponseEntity with status 200 (OK) and the updated payment term in body
     */
    @PutMapping("/{id}/status")
    //@PreAuthorize("hasAuthority('payment-status:update:all')")
    public ResponseEntity<ApiResponse<ContractPaymentTermDTO>> updatePaymentTermStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidDate,
            @RequestParam(required = false) Double paidAmount,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId(authentication);
        ContractPaymentTermDTO result = paymentTermService.updatePaymentTermStatus(id, status, paidDate, paidAmount, notes, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * GET /api/v1/contracts/payment-terms/overdue : Get overdue payment terms
     * 
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of overdue payment terms in body
     */
    @GetMapping("/overdue")
    //@PreAuthorize("hasAnyAuthority('payment-alert:read:all', 'payment-alert:read:own', 'payment-alert:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractPaymentTermDTO>>> getOverduePaymentTerms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractPaymentTermDTO> overdueTerms = paymentTermService.findOverduePaymentTermsPaged(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(overdueTerms));
    }
    
    /**
     * GET /api/v1/contracts/payment-terms/due-soon : Get payment terms due soon
     * 
     * @param days number of days to look ahead
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of upcoming payment terms in body
     */
    @GetMapping("/due-soon")
    //@PreAuthorize("hasAnyAuthority('payment-alert:read:all', 'payment-alert:read:own', 'payment-alert:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractPaymentTermDTO>>> getPaymentTermsDueSoon(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractPaymentTermDTO> upcomingTerms = paymentTermService.findPaymentTermsDueSoonPaged(days, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(upcomingTerms));
    }
    
    /**
     * GET /api/v1/contracts/payment-terms/status/:status : Get payment terms by status
     * 
     * @param status the payment status
     * @param page page number (1-based, will be converted to 0-based)
     * @param size page size
     * @param sortBy field to sort by
     * @param sortDirection sort direction (asc or desc)
     * @return the ResponseEntity with status 200 (OK) and the paged list of payment terms in body
     */
    @GetMapping("/status/{status}")
    //@PreAuthorize("hasAnyAuthority('payment-term:read:all', 'payment-term:read:own', 'payment-term:read:assigned')")
    public ResponseEntity<ApiResponse<Page<ContractPaymentTermDTO>>> getPaymentTermsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        // Convert from 1-based page (client) to 0-based page (Spring)
        if (page > 0) {
            page = page - 1;
        }
        
        // Create pageable with sort
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ContractPaymentTermDTO> paymentTerms = paymentTermService.findPaymentTermsByStatusPaged(status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(paymentTerms));
    }
} 