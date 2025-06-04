package com.company.internalmgmt.modules.opportunity.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import com.company.internalmgmt.modules.opportunity.dto.request.AssignLeaderRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.ListOpportunitiesRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.SyncHubspotRequest;
import com.company.internalmgmt.modules.opportunity.dto.request.UpdateOnsiteRequest;
import com.company.internalmgmt.modules.opportunity.dto.response.AssignLeaderResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.ListOpportunitiesResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncHubspotResponse;
import com.company.internalmgmt.modules.opportunity.dto.response.SyncLogResponse;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing opportunities.
 */
@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Opportunity Management", description = "APIs for managing business opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    /**
     * GET /api/v1/opportunities : Get a list of opportunities.
     *
     * @param request the filter and pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of opportunities in body
     */
    @GetMapping
    @PreAuthorize("hasAuthority('opportunity:read:all')")
    @Operation(summary = "Get a list of opportunities", description = "Returns a list of opportunities with optional filtering and pagination")
    public ResponseEntity<ApiResponse<ListOpportunitiesResponse>> listOpportunities(
            @Valid ListOpportunitiesRequest request) {
        log.debug("REST request to get opportunities with filters: {}", request);
        ListOpportunitiesResponse response = opportunityService.getOpportunities(request);
        ApiResponse<ListOpportunitiesResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * GET /api/v1/opportunities/{id} : Get an opportunity by id.
     *
     * @param id the id of the opportunity to retrieve
     * @param includeNotes whether to include notes in the response (default: true)
     * @param includeHistory whether to include history in the response (default: false)
     * @return the ResponseEntity with status 200 (OK) and with body the opportunity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('opportunity:read:all', 'opportunity:read:own', 'opportunity:read:assigned')")
    @Operation(summary = "Get an opportunity by ID", description = "Returns detailed information about a specific opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> getOpportunity(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "true") Boolean includeNotes,
            @RequestParam(required = false, defaultValue = "false") Boolean includeHistory) {
        log.debug("REST request to get Opportunity ID: {} (includeNotes: {}, includeHistory: {})", 
                id, includeNotes, includeHistory);
        // For now, we're using the basic getOpportunityById method
        // In a complete implementation, we'd handle the includeNotes and includeHistory parameters
        OpportunityDTO opportunity = opportunityService.getOpportunityById(id);
        ApiResponse<OpportunityDTO> apiResponse = ApiResponse.success(opportunity);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * POST /api/v1/opportunities/sync : Synchronize opportunities from Hubspot.
     *
     * @param request the synchronization parameters
     * @return the ResponseEntity with status 202 (Accepted) and with body the sync info
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('opportunities:sync')")
    @Operation(summary = "Synchronize opportunities from Hubspot", description = "Initiates a manual synchronization process from Hubspot CRM")
    public ResponseEntity<ApiResponse<SyncHubspotResponse>> synchronizeHubspot(
            @Valid @RequestBody SyncHubspotRequest request) {
        log.debug("REST request to synchronize opportunities from Hubspot: {}", request);
        SyncHubspotResponse response = opportunityService.synchronizeHubspot(request);
        ApiResponse<SyncHubspotResponse> apiResponse = ApiResponse.success(response, 202);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse);
    }

    /**
     * GET /api/v1/opportunities/sync/logs : Get synchronization logs.
     *
     * @param syncId optional ID of a specific sync process
     * @param status filter by status
     * @param fromDate filter from date
     * @param toDate filter to date
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return the ResponseEntity with status 200 (OK) and with body the sync logs
     */
    @GetMapping("/sync/logs")
    @PreAuthorize("hasAuthority('opportunity-log:read:all')")
    @Operation(summary = "Get synchronization logs", description = "Returns logs of Hubspot synchronization operations")
    public ResponseEntity<ApiResponse<SyncLogResponse>> getSyncLogs(
            @RequestParam(required = false) String syncId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "startedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        log.debug("REST request to get sync logs (syncId: {}, status: {}, dates: {} to {})", 
                syncId, status, fromDate, toDate);
        SyncLogResponse response = opportunityService.getSyncLogs(syncId, status, fromDate, toDate, 
                page, size, sortBy, sortDir);
        ApiResponse<SyncLogResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * POST /api/v1/opportunities/{id}/assign : Assign a leader to an opportunity.
     *
     * @param id the id of the opportunity to assign
     * @param request the assignment request with leader ID and options
     * @return the ResponseEntity with status 200 (OK) and with body the updated opportunity
     */
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('opportunity-assign:update:all')")
    @Operation(summary = "Assign a leader to an opportunity", description = "Assigns a leader to be responsible for an opportunity")
    public ResponseEntity<ApiResponse<AssignLeaderResponse>> assignLeader(
            @PathVariable Long id,
            @Valid @RequestBody AssignLeaderRequest request) {
        log.debug("REST request to assign leader to Opportunity ID: {}, Leader ID: {}", 
                id, request.getLeaderId());
        AssignLeaderResponse response = opportunityService.assignLeader(id, request);
        ApiResponse<AssignLeaderResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.ok(apiResponse);
    }   

    /**
     * PUT /api/v1/opportunities/{id}/onsite : Update onsite priority of an opportunity.
     *
     * @param id the id of the opportunity to update
     * @param request the update request with priority flag and optional note
     * @return the ResponseEntity with status 200 (OK) and with body the updated opportunity
     */
    @PutMapping("/{id}/onsite")
    @PreAuthorize("hasAnyAuthority('opportunity-onsite:update:all', 'opportunity-onsite:update:assigned')")
    @Operation(summary = "Update onsite priority", description = "Sets or removes the onsite priority flag for an opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> updateOnsitePriority(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOnsiteRequest request) {
        log.debug("REST request to update onsite priority for Opportunity ID: {}, Priority: {}", 
                id, request.getPriority());
        OpportunityDTO updated = opportunityService.updateOnsitePriority(id, request.getPriority(), request.getNote());
        ApiResponse<OpportunityDTO> apiResponse = ApiResponse.success(updated);
        return ResponseEntity.ok(apiResponse);
    }
} 