package com.company.internalmgmt.modules.contract.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.contract.dto.ContractDTO;
import com.company.internalmgmt.modules.contract.dto.request.ContractCreateRequest;
import com.company.internalmgmt.modules.contract.dto.request.ContractUpdateRequest;

/**
 * Service interface for managing contracts
 */
public interface ContractService {
    
    /**
     * Get contract by ID
     * 
     * @param id the contract ID
     * @param includePaymentTerms whether to include payment terms
     * @param includeEmployees whether to include employees
     * @param includeFiles whether to include files
     * @return the contract DTO
     */
    ContractDTO getContractById(Long id, Boolean includePaymentTerms, Boolean includeEmployees, Boolean includeFiles);
    
    /**
     * Get contract by contract code
     * 
     * @param contractCode the contract code
     * @return the contract DTO
     */
    ContractDTO getContractByCode(String contractCode);
    
    /**
     * Search contracts with various filters
     * 
     * @param customerName the customer name
     * @param contractCode the contract code
     * @param status the status
     * @param contractType the contract type
     * @param salesId the sales ID
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @param fromDate the from date
     * @param toDate the to date
     * @param paymentStatus the payment status
     * @param pageable the pageable information
     * @return page of contract DTOs
     */
    Page<ContractDTO> searchContracts(
            String customerName, 
            String contractCode, 
            String status, 
            String contractType, 
            Long salesId,
            Double minAmount,
            Double maxAmount,
            LocalDate fromDate,
            LocalDate toDate,
            String paymentStatus,
            Pageable pageable);
    
    /**
     * Create a new contract
     * 
     * @param request the contract create request
     * @param currentUserId the current user ID
     * @return the created contract DTO
     */
    ContractDTO createContract(ContractCreateRequest request, Long currentUserId);
    
    /**
     * Update an existing contract
     * 
     * @param id the contract ID
     * @param request the contract update request
     * @param currentUserId the current user ID
     * @return the updated contract DTO
     */
    ContractDTO updateContract(Long id, ContractUpdateRequest request, Long currentUserId);
    
    /**
     * Delete a contract (soft delete)
     * 
     * @param id the contract ID
     * @param currentUserId the current user ID
     */
    void deleteContract(Long id, Long currentUserId);
    
    /**
     * Get contracts by employee ID
     * 
     * @param employeeId the employee ID
     * @param pageable the pageable information
     * @return page of contract DTOs
     */
    Page<ContractDTO> getContractsByEmployeeId(Long employeeId, Pageable pageable);
    
    /**
     * Get contracts by team ID
     * 
     * @param teamId the team ID
     * @param pageable the pageable information
     * @return page of contract DTOs
     */
    Page<ContractDTO> getContractsByTeamId(Long teamId, Pageable pageable);
    
    /**
     * Get contracts by opportunity ID
     * 
     * @param opportunityId the opportunity ID
     * @param pageable the pageable information
     * @return page of contract DTOs
     */
    Page<ContractDTO> getContractsByOpportunityId(Long opportunityId, Pageable pageable);
    
    /**
     * Get contracts by sales ID
     * 
     * @param salesId the sales ID
     * @param pageable the pageable information
     * @return page of contract DTOs
     */
    Page<ContractDTO> getContractsBySalesId(Long salesId, Pageable pageable);
} 