package com.company.internalmgmt.modules.contract.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.contract.model.ContractPaymentTerm;

/**
 * Repository for ContractPaymentTerm entity
 */
@Repository
public interface ContractPaymentTermRepository extends JpaRepository<ContractPaymentTerm, Long> {
    
    /**
     * Find payment terms by contract ID
     * 
     * @param contractId the contract ID
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByContractId(Long contractId);
    
    /**
     * Find payment terms by contract ID and expected payment date between from and to dates
     * 
     * @param contractId the contract ID
     * @param fromDate the from date
     * @param toDate the to date
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByContractIdAndExpectedPaymentDateBetween(Long contractId, LocalDate fromDate, LocalDate toDate);
    
    /**
     * Find payment terms by payment status
     * 
     * @param paymentStatus the payment status
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByPaymentStatus(String paymentStatus);
    
    /**
     * Find payment terms by payment status and contract ID
     * 
     * @param paymentStatus the payment status
     * @param contractId the contract ID
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByPaymentStatusAndContractId(String paymentStatus, Long contractId);
    
    /**
     * Find payment terms by contract IDs
     * 
     * @param contractIds the list of contract IDs
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByContractIdIn(List<Long> contractIds);
    
    /**
     * Delete payment terms by contract ID
     * 
     * @param contractId the contract ID
     */
    void deleteByContractId(Long contractId);
    
    /**
     * Find payment terms that are overdue (expected payment date is before current date and payment status is not paid)
     * 
     * @param currentDate the current date
     * @return list of payment terms
     */
    @Query("SELECT pt FROM ContractPaymentTerm pt WHERE pt.expectedPaymentDate < :currentDate AND pt.paymentStatus != 'paid'")
    List<ContractPaymentTerm> findOverduePaymentTerms(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find payment terms that are due soon (expected payment date is between current date and future date and payment status is not paid)
     * 
     * @param currentDate the current date
     * @param futureDate the future date
     * @return list of payment terms
     */
    @Query("SELECT pt FROM ContractPaymentTerm pt WHERE pt.expectedPaymentDate BETWEEN :currentDate AND :futureDate AND pt.paymentStatus != 'paid'")
    List<ContractPaymentTerm> findPaymentTermsDueSoon(@Param("currentDate") LocalDate currentDate, @Param("futureDate") LocalDate futureDate);
    
    /**
     * Find payment terms by contract ID, ordered by term number
     * 
     * @param contractId the contract ID
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByContractIdOrderByTermNumberAsc(Long contractId);
    
    /**
     * Find payment terms by expected payment date before and payment status not
     * 
     * @param expectedPaymentDate the expected payment date
     * @param paymentStatus the payment status
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByExpectedPaymentDateBeforeAndPaymentStatusNot(LocalDate expectedPaymentDate, String paymentStatus);
    
    /**
     * Find payment terms by expected payment date between and payment status not
     * 
     * @param fromDate the from date
     * @param toDate the to date
     * @param paymentStatus the payment status
     * @return list of payment terms
     */
    List<ContractPaymentTerm> findByExpectedPaymentDateBetweenAndPaymentStatusNot(LocalDate fromDate, LocalDate toDate, String paymentStatus);
    
    /**
     * Find payment terms by payment status with pagination
     * 
     * @param paymentStatus the payment status
     * @param pageable the pageable information
     * @return page of payment terms
     */
    Page<ContractPaymentTerm> findByPaymentStatus(String paymentStatus, Pageable pageable);
    
    /**
     * Find payment terms by expected payment date before and payment status not with pagination
     * 
     * @param expectedPaymentDate the expected payment date
     * @param paymentStatus the payment status
     * @param pageable the pageable information
     * @return page of payment terms
     */
    Page<ContractPaymentTerm> findByExpectedPaymentDateBeforeAndPaymentStatusNot(
            LocalDate expectedPaymentDate, String paymentStatus, Pageable pageable);
    
    /**
     * Find payment terms by expected payment date between and payment status not with pagination
     * 
     * @param fromDate the from date
     * @param toDate the to date
     * @param paymentStatus the payment status
     * @param pageable the pageable information
     * @return page of payment terms
     */
    Page<ContractPaymentTerm> findByExpectedPaymentDateBetweenAndPaymentStatusNot(
            LocalDate fromDate, LocalDate toDate, String paymentStatus, Pageable pageable);
} 