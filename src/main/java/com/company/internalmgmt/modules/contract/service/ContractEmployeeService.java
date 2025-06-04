package com.company.internalmgmt.modules.contract.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.contract.dto.ContractEmployeeDTO;

/**
 * Service interface for managing contract employees
 */
public interface ContractEmployeeService {
    
    /**
     * Get contract employees by contract ID
     * 
     * @param contractId the contract ID
     * @return list of contract employee DTOs
     */
    List<ContractEmployeeDTO> getContractEmployeesByContractId(Long contractId);
    
    /**
     * Get contract employee by ID
     * 
     * @param id the contract employee ID
     * @return the contract employee DTO
     */
    ContractEmployeeDTO getContractEmployeeById(Long id);
    
    /**
     * Get contract employees by employee ID
     * 
     * @param employeeId the employee ID
     * @return list of contract employee DTOs
     */
    List<ContractEmployeeDTO> getContractEmployeesByEmployeeId(Long employeeId);
    
    /**
     * Find active contract assignments for a specific date
     * 
     * @param date the date
     * @return list of active contract employee DTOs
     */
    List<ContractEmployeeDTO> findActiveContractEmployeesForDate(LocalDate date);
    
    /**
     * Find active contract assignments for an employee on a specific date
     * 
     * @param employeeId the employee ID
     * @param date the date
     * @return list of active contract employee DTOs
     */
    List<ContractEmployeeDTO> findActiveContractEmployeesForEmployeeAndDate(Long employeeId, LocalDate date);
    
    /**
     * Find active contract assignments for a team on a specific date
     * 
     * @param teamId the team ID
     * @param date the date
     * @return list of active contract employee DTOs
     */
    List<ContractEmployeeDTO> findActiveContractEmployeesForTeamAndDate(Long teamId, LocalDate date);
    
    /**
     * Count active employees for a contract on a specific date
     * 
     * @param contractId the contract ID
     * @param date the date
     * @return count of active employees
     */
    Long countActiveEmployeesForContractAndDate(Long contractId, LocalDate date);
    
    /**
     * Assign an employee to a contract
     * 
     * @param contractId the contract ID
     * @param employeeId the employee ID
     * @param role the role
     * @param startDate the start date
     * @param endDate the end date
     * @param allocationPercentage the allocation percentage
     * @param billRate the bill rate
     * @param currentUserId the current user ID
     * @return the created contract employee DTO
     */
    ContractEmployeeDTO assignEmployeeToContract(Long contractId, Long employeeId, String role, 
            LocalDate startDate, LocalDate endDate, BigDecimal allocationPercentage, 
            BigDecimal billRate, Long currentUserId);
    
    /**
     * Update a contract employee assignment
     * 
     * @param id the contract employee ID
     * @param role the role
     * @param startDate the start date
     * @param endDate the end date
     * @param allocationPercentage the allocation percentage
     * @param billRate the bill rate
     * @param currentUserId the current user ID
     * @return the updated contract employee DTO
     */
    ContractEmployeeDTO updateContractEmployeeAssignment(Long id, String role, LocalDate startDate, 
            LocalDate endDate, BigDecimal allocationPercentage, BigDecimal billRate, Long currentUserId);
    
    /**
     * Remove an employee from a contract
     * 
     * @param id the contract employee ID
     * @param currentUserId the current user ID
     */
    void removeEmployeeFromContract(Long id, Long currentUserId);
    
    /**
     * Get contracts by employee ID for UI
     * 
     * @param employeeId the employee ID
     * @return list of contract employee DTOs
     */
    List<ContractEmployeeDTO> getContractsByEmployeeId(Long employeeId);
    
    /**
     * Get contracts by employee ID with pagination
     * 
     * @param employeeId the employee ID
     * @param pageable the pageable information
     * @return page of contract employee DTOs
     */
    Page<ContractEmployeeDTO> getContractsByEmployeeIdPaged(Long employeeId, Pageable pageable);
} 