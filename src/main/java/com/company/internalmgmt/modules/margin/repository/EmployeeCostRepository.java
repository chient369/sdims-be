package com.company.internalmgmt.modules.margin.repository;

import com.company.internalmgmt.modules.margin.model.EmployeeCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeCostRepository extends JpaRepository<EmployeeCost, Long>, JpaSpecificationExecutor<EmployeeCost> {

    /**
     * Find employee cost by employee id, year and month
     */
    Optional<EmployeeCost> findByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month);

    /**
     * Find employee costs by employee id and period range
     */
    List<EmployeeCost> findByEmployeeIdAndYearAndMonthBetween(
            Long employeeId, Integer year, Integer startMonth, Integer endMonth);
    
    /**
     * Find employee costs by employee ids for a specific year and month
     */
    List<EmployeeCost> findByEmployeeIdInAndYearAndMonth(List<Long> employeeIds, Integer year, Integer month);
    
    /**
     * Find employee costs by employee ids and period range (same year)
     */
    @Query("SELECT ec FROM EmployeeCost ec WHERE ec.employeeId IN :employeeIds " +
           "AND ec.year = :year AND ec.month BETWEEN :startMonth AND :endMonth " +
           "ORDER BY ec.employeeId, ec.year, ec.month")
    List<EmployeeCost> findByEmployeeIdsAndPeriod(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("year") Integer year,
            @Param("startMonth") Integer startMonth,
            @Param("endMonth") Integer endMonth);
    
    /**
     * Find employee costs by employee ids and period range (across years)
     */
    @Query("SELECT ec FROM EmployeeCost ec WHERE ec.employeeId IN :employeeIds " +
           "AND ((ec.year = :startYear AND ec.month >= :startMonth) " +
           "OR (ec.year > :startYear AND ec.year < :endYear) " +
           "OR (ec.year = :endYear AND ec.month <= :endMonth)) " +
           "ORDER BY ec.employeeId, ec.year, ec.month")
    List<EmployeeCost> findByEmployeeIdsAndDateRange(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);
} 