package com.company.internalmgmt.modules.margin.repository;

import com.company.internalmgmt.modules.margin.model.EmployeeRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRevenueRepository extends JpaRepository<EmployeeRevenue, Long>, JpaSpecificationExecutor<EmployeeRevenue> {

    /**
     * Find revenue by employee id, contract id, year and month
     */
    Optional<EmployeeRevenue> findByEmployeeIdAndContractIdAndYearAndMonth(
            Long employeeId, Long contractId, Integer year, Integer month);

    /**
     * Find all revenues for an employee in a specific month
     */
    List<EmployeeRevenue> findByEmployeeIdAndYearAndMonth(Long employeeId, Integer year, Integer month);
    
    /**
     * Find revenues by employee id for a period range (same year)
     */
    List<EmployeeRevenue> findByEmployeeIdAndYearAndMonthBetween(
            Long employeeId, Integer year, Integer startMonth, Integer endMonth);
    
    /**
     * Find revenues by employee ids for a specific year and month
     */
    List<EmployeeRevenue> findByEmployeeIdInAndYearAndMonth(List<Long> employeeIds, Integer year, Integer month);
    
    /**
     * Sum revenue by employee for a specific month
     */
    @Query("SELECT er.employeeId, SUM(er.calculatedRevenue) FROM EmployeeRevenue er " +
           "WHERE er.employeeId IN :employeeIds AND er.year = :year AND er.month = :month " +
           "GROUP BY er.employeeId")
    List<Object[]> sumRevenueByEmployeeIdsAndPeriod(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("year") Integer year,
            @Param("month") Integer month);
    
    /**
     * Find revenues by employee ids and period range (same year)
     */
    @Query("SELECT er FROM EmployeeRevenue er WHERE er.employeeId IN :employeeIds " +
           "AND er.year = :year AND er.month BETWEEN :startMonth AND :endMonth " +
           "ORDER BY er.employeeId, er.year, er.month")
    List<EmployeeRevenue> findByEmployeeIdsAndPeriod(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("year") Integer year,
            @Param("startMonth") Integer startMonth,
            @Param("endMonth") Integer endMonth);
    
    /**
     * Find revenues by employee ids and period range (across years)
     */
    @Query("SELECT er FROM EmployeeRevenue er WHERE er.employeeId IN :employeeIds " +
           "AND ((er.year = :startYear AND er.month >= :startMonth) " +
           "OR (er.year > :startYear AND er.year < :endYear) " +
           "OR (er.year = :endYear AND er.month <= :endMonth)) " +
           "ORDER BY er.employeeId, er.year, er.month")
    List<EmployeeRevenue> findByEmployeeIdsAndDateRange(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);
} 