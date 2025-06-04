package com.company.internalmgmt.modules.contract.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.contract.dto.ContractPaymentTermDTO;

/**
 * Service interface for managing contract payment terms
 */
public interface ContractPaymentTermService {
    
    /**
     * Get payment terms by contract ID
     * 
     * @param contractId the contract ID
     * @return list of payment term DTOs
     */
    List<ContractPaymentTermDTO> getPaymentTermsByContractId(Long contractId);
    
    /**
     * Get payment term by ID
     * 
     * @param id the payment term ID
     * @return the payment term DTO
     */
    ContractPaymentTermDTO getPaymentTermById(Long id);
    
    /**
     * Update payment term status
     * 
     * @param id the payment term ID
     * @param status the new status
     * @param paidDate the paid date (if status is paid)
     * @param paidAmount the paid amount (if status is paid)
     * @param notes any notes about the payment
     * @param currentUserId the current user ID
     * @return the updated payment term DTO
     */
    ContractPaymentTermDTO updatePaymentTermStatus(Long id, String status, LocalDate paidDate, Double paidAmount, String notes, Long currentUserId);
    
    /**
     * Find overdue payment terms
     * 
     * @return list of overdue payment term DTOs
     */
    List<ContractPaymentTermDTO> findOverduePaymentTerms();
    
    /**
     * Find overdue payment terms with pagination
     * 
     * @param pageable the pageable information
     * @return page of overdue payment term DTOs
     */
    Page<ContractPaymentTermDTO> findOverduePaymentTermsPaged(Pageable pageable);
    
    /**
     * Find payment terms due in the next N days
     * 
     * @param days number of days to look ahead
     * @return list of upcoming payment term DTOs
     */
    List<ContractPaymentTermDTO> findPaymentTermsDueSoon(int days);
    
    /**
     * Find payment terms due in the next N days with pagination
     * 
     * @param days number of days to look ahead
     * @param pageable the pageable information
     * @return page of upcoming payment term DTOs
     */
    Page<ContractPaymentTermDTO> findPaymentTermsDueSoonPaged(int days, Pageable pageable);
    
    /**
     * Find payment terms by contract IDs
     * 
     * @param contractIds the list of contract IDs
     * @return list of payment term DTOs
     */
    List<ContractPaymentTermDTO> findPaymentTermsByContractIds(List<Long> contractIds);
    
    /**
     * Find payment terms by status
     * 
     * @param status the payment status
     * @return list of payment term DTOs
     */
    List<ContractPaymentTermDTO> findPaymentTermsByStatus(String status);
    
    /**
     * Find payment terms by status with pagination
     * 
     * @param status the payment status
     * @param pageable the pageable information
     * @return page of payment term DTOs
     */
    Page<ContractPaymentTermDTO> findPaymentTermsByStatusPaged(String status, Pageable pageable);
} 