package com.company.internalmgmt.modules.hrm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;

/**
 * Repository for managing EmployeeStatusLog entities
 */
@Repository
public interface EmployeeStatusLogRepository extends JpaRepository<EmployeeStatusLog, Long> {

    /**
     * Find status logs for a specific employee
     *
     * @param employeeId the employee ID
     * @return list of status logs for the specified employee
     */
    List<EmployeeStatusLog> findByEmployeeId(Long employeeId);

    /**
     * Find status logs for a specific employee with pagination
     *
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return page of status logs for the specified employee
     */
    Page<EmployeeStatusLog> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Find the most recent status log for an employee
     *
     * @param employeeId the employee ID
     * @return the most recent status log for the specified employee
     */
    @Query("SELECT esl FROM EmployeeStatusLog esl WHERE esl.employee.id = :employeeId ORDER BY esl.logTimestamp DESC")
    List<EmployeeStatusLog> findMostRecentByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    /**
     * Find status logs for employees with a specific status
     *
     * @param status the status
     * @return list of status logs with the specified status
     */
    List<EmployeeStatusLog> findByStatus(String status);

    /**
     * Find status logs created within a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of status logs created within the specified date range
     */
    @Query("SELECT esl FROM EmployeeStatusLog esl WHERE esl.logTimestamp BETWEEN :startDate AND :endDate")
    List<EmployeeStatusLog> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find status logs for projects ending soon
     *
     * @param endDate the end date threshold
     * @return list of status logs for projects ending before the specified date
     */
    @Query("SELECT esl FROM EmployeeStatusLog esl WHERE esl.status = 'EndingSoon' AND esl.expectedEndDate <= :endDate")
    List<EmployeeStatusLog> findProjectsEndingSoon(@Param("endDate") LocalDate endDate);

    /**
     * Lấy log mới nhất của employee (phân trang, sắp xếp giảm dần theo logTimestamp)
     */
    @Query("SELECT esl FROM EmployeeStatusLog esl WHERE esl.employee.id = :employeeId ORDER BY esl.logTimestamp DESC")
    List<EmployeeStatusLog> findLatestByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    /**
     * Tìm các log có expectedEndDate trước ngày chỉ định (EndingSoon)
     */
    @Query("SELECT esl FROM EmployeeStatusLog esl WHERE esl.status = 'EndingSoon' AND esl.expectedEndDate <= :date")
    List<EmployeeStatusLog> findEndingSoonBeforeDate(@Param("date") LocalDate date);

    /**
     * Tìm log theo tên project (không phân biệt hoa thường)
     */
    List<EmployeeStatusLog> findByProjectNameContainingIgnoreCase(String projectName);

    /**
     * Tìm log theo khoảng thời gian logTimestamp
     */
    List<EmployeeStatusLog> findByLogTimestampBetween(LocalDateTime start, LocalDateTime end);
} 
