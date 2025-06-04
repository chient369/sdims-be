package com.company.internalmgmt.modules.contract.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.contract.model.ContractEmployee;

/**
 * Repository for ContractEmployee entity
 */
@Repository
public interface ContractEmployeeRepository extends JpaRepository<ContractEmployee, Long> {
    
    /**
     * Find contract employees by contract ID
     * 
     * @param contractId the contract ID
     * @return list of contract employees
     */
    List<ContractEmployee> findByContractId(Long contractId);
    
    /**
     * Find contract employees by employee ID
     * 
     * @param employeeId the employee ID
     * @return list of contract employees
     */
    List<ContractEmployee> findByEmployeeId(Long employeeId);
    
    /**
     * Find contract employees by employee ID with pagination
     * 
     * @param employeeId the employee ID
     * @param pageable the pageable information
     * @return page of contract employees
     */
    Page<ContractEmployee> findByEmployeeId(Long employeeId, Pageable pageable);
    
    /**
     * Find contract employee by contract ID and employee ID
     * 
     * @param contractId the contract ID
     * @param employeeId the employee ID
     * @return optional of contract employee
     */
    Optional<ContractEmployee> findByContractIdAndEmployeeId(Long contractId, Long employeeId);
    
    /**
     * Delete contract employees by contract ID
     * 
     * @param contractId the contract ID
     */
    void deleteByContractId(Long contractId);
    
    /**
     * Find active contract employees for a specific date
     * 
     * @param date the date
     * @return list of contract employees
     */
    @Query("SELECT ce FROM ContractEmployee ce WHERE :date BETWEEN ce.startDate AND COALESCE(ce.endDate, '9999-12-31')")
    List<ContractEmployee> findActiveByDate(@Param("date") LocalDate date);
    
    /**
     * Find active contract employees for an employee on a specific date
     * 
     * @param employeeId the employee ID
     * @param date the date
     * @return list of contract employees
     */
    @Query("SELECT ce FROM ContractEmployee ce WHERE ce.employee.id = :employeeId AND :date BETWEEN ce.startDate AND COALESCE(ce.endDate, '9999-12-31')")
    List<ContractEmployee> findActiveByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
    
    /**
     * Find active contract employees for a team on a specific date
     * 
     * @param teamId the team ID
     * @param date the date
     * @return list of contract employees
     */
    @Query("SELECT ce FROM ContractEmployee ce JOIN ce.employee e WHERE e.team.id = :teamId AND :date BETWEEN ce.startDate AND COALESCE(ce.endDate, '9999-12-31')")
    List<ContractEmployee> findActiveByTeamIdAndDate(@Param("teamId") Long teamId, @Param("date") LocalDate date);
    
    /**
     * Count active employees for a contract on a specific date
     * 
     * @param contractId the contract ID
     * @param date the date
     * @return count of active employees
     */
    @Query("SELECT COUNT(ce) FROM ContractEmployee ce WHERE ce.contract.id = :contractId AND :date BETWEEN ce.startDate AND COALESCE(ce.endDate, '9999-12-31')")
    Long countActiveByContractIdAndDate(@Param("contractId") Long contractId, @Param("date") LocalDate date);
} 