package com.company.internalmgmt.modules.opportunity.service;

import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import com.company.internalmgmt.modules.opportunity.dto.request.AssignLeaderRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.SyncHubspotRequest;
import com.company.internalmgmt.modules.opportunity.dto.response.AssignLeaderResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncHubspotResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncLogResponse;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;

/**
 * Service interface for managing opportunities.
 */
public interface OpportunityService {

    /**
     * Get a list of opportunities with filtering, sorting and pagination.
     *
     * @param request filter and pagination parameters
     * @return the response containing opportunities and summary
     */
    ListOpportunitiesResponse getOpportunities(ListOpportunitiesRequest request);

    /**
     * Get an opportunity by ID.
     *
     * @param id the opportunity ID
     * @return the opportunity DTO
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    OpportunityDTO getOpportunityById(Long id);

    /**
     * Get an opportunity entity by ID.
     *
     * @param id the opportunity ID
     * @return the opportunity entity
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    Opportunity getOpportunityEntityById(Long id);

    /**
     * Update opportunity onsite priority.
     *
     * @param id the opportunity ID
     * @param priority the new priority value
     * @param note optional note about the change
     * @return the updated opportunity DTO
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    OpportunityDTO updateOnsitePriority(Long id, Boolean priority, String note);
    
    /**
     * Synchronize opportunities from Hubspot.
     *
     * @param request the synchronization parameters
     * @return the response containing synchronization details
     */
    SyncHubspotResponse synchronizeHubspot(SyncHubspotRequest request);
    
    /**
     * Get synchronization logs.
     *
     * @param syncId optional ID of a specific sync process
     * @param status filter by status
     * @param fromDate filter from date
     * @param toDate filter to date
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return the response containing sync logs
     */
    SyncLogResponse getSyncLogs(String syncId, String status, String fromDate, String toDate, 
                         Integer page, Integer size, String sortBy, String sortDir);
    
    /**
     * Assign a leader to an opportunity.
     *
     * @param opportunityId the opportunity ID
     * @param request the assignment request
     * @return the response containing assignment details
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    AssignLeaderResponse assignLeader(Long opportunityId, AssignLeaderRequest request);
    
    /**
     * Create a new opportunity.
     *
     * @param opportunityDTO the opportunity data
     * @return the created opportunity DTO
     */
    OpportunityDTO createOpportunity(OpportunityDTO opportunityDTO);
    
    /**
     * Update an existing opportunity.
     *
     * @param id the opportunity ID
     * @param opportunityDTO the updated opportunity data
     * @return the updated opportunity DTO
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    OpportunityDTO updateOpportunity(Long id, OpportunityDTO opportunityDTO);
    
    /**
     * Delete an opportunity.
     *
     * @param id the opportunity ID
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    void deleteOpportunity(Long id);
    
    /**
     * Update the status of an opportunity.
     *
     * @param id the opportunity ID
     * @param status the new status
     * @param note optional note about the status change
     * @return the updated opportunity DTO
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    OpportunityDTO updateStatus(Long id, String status, String note);
    
    /**
     * Update closing information of an opportunity.
     *
     * @param id the opportunity ID
     * @param closingDate the estimated closing date (format: YYYY-MM-DD)
     * @param closingProbability the probability of closing (0-100)
     * @return the updated opportunity DTO
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if opportunity not found
     */
    OpportunityDTO updateClosingInfo(Long id, String closingDate, Integer closingProbability);

    /**
     * Update the last interaction date of an opportunity.
     *
     * @param id             the opportunity ID
     * @param interactionDate the interaction date in YYYY-MM-DD format
     * @param note           optional note about the update
     * @return the updated opportunity DTO
     */
    OpportunityDTO updateLastInteractionDate(Long id, String interactionDate, String note);
} 