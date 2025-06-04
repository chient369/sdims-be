package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Opportunity entities.
 */
@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long>, JpaSpecificationExecutor<Opportunity> {
    
    /**
     * Find opportunities by client name.
     *
     * @param clientName the client name
     * @return list of opportunities
     */
    List<Opportunity> findByClientNameContainingIgnoreCase(String clientName);
    
    /**
     * Find opportunities by status.
     *
     * @param status the status
     * @return list of opportunities
     */
    List<Opportunity> findByStatus(String status);
    
    /**
     * Find opportunities by assigned employee ID.
     *
     * @param employeeId the employee ID
     * @return list of opportunities
     */
    @Query("SELECT o FROM Opportunity o JOIN OpportunityAssignment a ON o.id = a.opportunity.id WHERE a.employee.id = :employeeId")
    List<Opportunity> findByAssignedEmployeeId(@Param("employeeId") Long employeeId);
    
    /**
     * Find the maximum sequence number for a given prefix.
     * This is used for generating sequential opportunity codes.
     *
     * @param prefix the prefix (e.g., "OPP-20230101-")
     * @return the maximum sequence number or null if none exists
     */
    @Query("SELECT MAX(CAST(SUBSTRING(o.code, LENGTH(:prefix) + 1) AS int)) FROM Opportunity o WHERE o.code LIKE CONCAT(:prefix, '%')")
    Long findMaxSequenceByPrefix(@Param("prefix") String prefix);
    
    /**
     * Find an opportunity by its Hubspot ID.
     * 
     * @param hubspotId the Hubspot ID
     * @return the opportunity if found, or null
     */
    Opportunity findByHubspotId(String hubspotId);
} 