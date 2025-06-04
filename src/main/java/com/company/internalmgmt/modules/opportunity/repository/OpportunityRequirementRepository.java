package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.opportunity.model.OpportunityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OpportunityRequirement entity.
 */
@Repository
public interface OpportunityRequirementRepository extends JpaRepository<OpportunityRequirement, Long> {
    
    /**
     * Find all requirements by opportunity ID.
     *
     * @param opportunityId the opportunity ID
     * @return list of requirements
     */
    List<OpportunityRequirement> findByOpportunityId(Long opportunityId);
} 