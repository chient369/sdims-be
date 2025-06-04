package com.company.internalmgmt.modules.contract.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.contract.model.Contract;

/**
 * Repository for Contract entity
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    
    /**
     * Find contract by contract code
     * 
     * @param contractCode the contract code
     * @return optional of contract
     */
    Optional<Contract> findByContractCode(String contractCode);
    
    /**
     * Find contracts by customer name containing the search text
     * 
     * @param customerName the customer name to search
     * @param pageable the pageable object
     * @return page of contracts
     */
    Page<Contract> findByClientNameContainingIgnoreCase(String customerName, Pageable pageable);
    
    /**
     * Find contracts by status
     * 
     * @param status the status
     * @param pageable the pageable object
     * @return page of contracts
     */
    Page<Contract> findByStatus(String status, Pageable pageable);
    
    /**
     * Find contracts by assigned sales ID
     * 
     * @param salesId the sales ID
     * @param pageable the pageable object
     * @return page of contracts
     */
    Page<Contract> findByAssignedSalesId(Long salesId, Pageable pageable);
    
    /**
     * Find contracts by contract type
     * 
     * @param contractType the contract type
     * @param pageable the pageable object
     * @return page of contracts
     */
    Page<Contract> findByContractType(String contractType, Pageable pageable);
    
    /**
     * Find contracts by start date between from and to dates
     * 
     * @param fromDate the from date
     * @param toDate the to date
     * @param pageable the pageable object
     * @return page of contracts
     */
    Page<Contract> findByEffectiveDateBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);
    
    /**
     * Check if a contract code already exists excluding a specific contract ID
     * 
     * @param contractCode the contract code
     * @param contractId the contract ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c WHERE c.contractCode = :contractCode AND c.id != :contractId")
    boolean existsByContractCodeAndIdNot(@Param("contractCode") String contractCode, @Param("contractId") Long contractId);
    
    /**
     * Find contracts by employee ID (contracts where the employee is assigned)
     * 
     * @param employeeId the employee ID
     * @param pageable the pageable object
     * @return page of contracts
     */
    @Query("SELECT DISTINCT c FROM Contract c JOIN c.contractEmployees ce WHERE ce.employee.id = :employeeId")
    Page<Contract> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);
    
    /**
     * Find contracts by team ID (contracts where employees from the team are assigned)
     * 
     * @param teamId the team ID
     * @param pageable the pageable object
     * @return page of contracts
     */
    @Query("SELECT DISTINCT c FROM Contract c JOIN c.contractEmployees ce JOIN ce.employee e WHERE e.team.id = :teamId")
    Page<Contract> findByTeamId(@Param("teamId") Long teamId, Pageable pageable);
} 