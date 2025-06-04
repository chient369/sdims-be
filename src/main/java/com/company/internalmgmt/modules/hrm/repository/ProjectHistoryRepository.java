package com.company.internalmgmt.modules.hrm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.ProjectHistory;

/**
 * Repository for managing ProjectHistory entities
 */
@Repository
public interface ProjectHistoryRepository extends JpaRepository<ProjectHistory, Long> {

    /**
     * Find project history entries for a specific employee
     *
     * @param employeeId the employee ID
     * @return list of project history entries for the specified employee
     */
    List<ProjectHistory> findByEmployeeId(Long employeeId);

    /**
     * Find project history entries for a specific employee with pagination
     *
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return page of project history entries for the specified employee
     */
    Page<ProjectHistory> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Find project history entries for a specific employee with pagination ordered by start date descending
     *
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return page of project history entries for the specified employee ordered by start date descending
     */
    Page<ProjectHistory> findByEmployeeIdOrderByStartDateDesc(Long employeeId, Pageable pageable);

    /**
     * Find project history entries for a specific employee ordered by start date descending
     *
     * @param employeeId the employee ID
     * @return list of project history entries for the specified employee ordered by start date descending
     */
    List<ProjectHistory> findByEmployeeIdOrderByStartDateDesc(Long employeeId);

    /**
     * Find project history entries by project name
     *
     * @param projectName the project name
     * @return list of project history entries for the specified project
     */
    List<ProjectHistory> findByProjectNameContainingIgnoreCase(String projectName);

    /**
     * Find project history entries by client name
     *
     * @param clientName the client name
     * @return list of project history entries for the specified client
     */
    List<ProjectHistory> findByClientNameContainingIgnoreCase(String clientName);

    /**
     * Find project history entries that overlap with a date range
     *
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of project history entries that overlap with the specified date range
     */
    @Query("SELECT ph FROM ProjectHistory ph WHERE (ph.startDate <= :endDate AND (ph.endDate IS NULL OR ph.endDate >= :startDate)) AND ph.employee.id = :employeeId")
    List<ProjectHistory> findOverlappingWithDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find project history entries with a specific role
     *
     * @param role the role
     * @return list of project history entries with the specified role
     */
    List<ProjectHistory> findByRoleContainingIgnoreCase(String role);

    /**
     * Check if there is an overlapping project
     *
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return true if there is an overlapping project, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ph) > 0 THEN true ELSE false END FROM ProjectHistory ph WHERE ph.employee.id = :employeeId AND (ph.startDate <= :endDate AND (ph.endDate IS NULL OR ph.endDate >= :startDate))")
    boolean existsOverlappingProject(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Check if there is an overlapping project excluding the current project
     *
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @param excludeId the project ID to exclude
     * @return true if there is an overlapping project excluding the current project, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ph) > 0 THEN true ELSE false END FROM ProjectHistory ph WHERE ph.employee.id = :employeeId AND (ph.startDate <= :endDate AND (ph.endDate IS NULL OR ph.endDate >= :startDate)) AND ph.id <> :excludeId")
    boolean existsOverlappingProjectExcludingCurrent(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("excludeId") Long excludeId);

    /**
     * Find project history entries with end date null
     *
     * @param employeeId the employee ID
     * @return list of project history entries with end date null
     */
    List<ProjectHistory> findByEmployeeIdAndEndDateIsNull(Long employeeId);
} 
