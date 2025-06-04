package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for OpportunityAssignment entities.
 */
@Repository
public interface OpportunityAssignmentRepository extends JpaRepository<OpportunityAssignment, Long> {

    /**
     * Find an assignment by opportunity and employee.
     * 
     * @param opportunity the opportunity
     * @param employee the employee
     * @return the optional containing assignment if found
     */
    Optional<OpportunityAssignment> findByOpportunityAndEmployee(Opportunity opportunity, Employee employee);
    
    /**
     * Find all assignments for a specific opportunity.
     * 
     * @param opportunity the opportunity
     * @return list of assignments
     */
    List<OpportunityAssignment> findByOpportunity(Opportunity opportunity);
    
    /**
     * Find all assignments for a specific employee.
     * 
     * @param employee the employee
     * @return list of assignments
     */
    List<OpportunityAssignment> findByEmployee(Employee employee);
    
    /**
     * Delete all assignments for a specific opportunity.
     * 
     * @param opportunity the opportunity
     */
    void deleteByOpportunity(Opportunity opportunity);
} 