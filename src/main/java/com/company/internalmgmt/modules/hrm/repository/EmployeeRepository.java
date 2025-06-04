package com.company.internalmgmt.modules.hrm.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.Employee;

/**
 * Repository for managing Employee entities
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    /**
     * Find an employee by their unique employee code
     *
     * @param employeeCode the employee code
     * @return the employee if found
     */
    Optional<Employee> findByEmployeeCode(String employeeCode);

    /**
     * Find an employee by their company email
     *
     * @param companyEmail the company email
     * @return the employee if found
     */
    Optional<Employee> findByCompanyEmail(String companyEmail);

    /**
     * Find an employee by their associated user ID
     *
     * @param userId the user ID
     * @return the employee if found
     */
    Optional<Employee> findByUserId(Long userId);

    /**
     * Find employees by their team ID
     *
     * @param teamId the team ID
     * @param pageable pagination information
     * @return a page of employees belonging to the specified team
     */
    Page<Employee> findByTeam_Id(Long teamId, Pageable pageable);

    /**
     * Find employees by their current status
     *
     * @param status the status to search for
     * @param pageable pagination information
     * @return a page of employees with the specified status
     */
    Page<Employee> findByCurrentStatus(String status, Pageable pageable);

    /**
     * Find employees by their reporting leader
     *
     * @param reportingLeaderId the ID of the reporting leader
     * @param pageable pagination information
     * @return a page of employees reporting to the specified leader
     */
    Page<Employee> findByReportingLeaderId(Long reportingLeaderId, Pageable pageable);

    /**
     * Search employees by name (first name or last name containing the search string)
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of employees matching the search term
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Employee> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find employees who are ending projects soon
     *
     * @param endDate the end date threshold
     * @param pageable pagination information
     * @return a page of employees with projects ending before the specified date
     */
    @Query("SELECT DISTINCT e FROM Employee e JOIN e.statusLogs sl WHERE e.currentStatus = 'EndingSoon' AND sl.expectedEndDate <= :endDate")
    Page<Employee> findEmployeesEndingSoon(@Param("endDate") LocalDate endDate, Pageable pageable);

    /**
     * Find available employees (those with Available status)
     *
     * @param pageable pagination information
     * @return a page of available employees
     */
    Page<Employee> findByCurrentStatusEqualsIgnoreCase(String status, Pageable pageable);
} 
