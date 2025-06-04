package com.company.internalmgmt.modules.opportunity.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
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
 * REST controller for creating and managing opportunities.
 */
@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Opportunity Management", description = "APIs for creating and managing business opportunities")
public class OpportunityManagementController {

    private final OpportunityService opportunityService;

    /**
     * POST /api/v1/opportunities : Create a new opportunity.
     *
     * @param opportunityDTO the opportunity to create
     * @return the ResponseEntity with status 201 (Created) and the new opportunity in the body
     */
    @PostMapping
    @PreAuthorize("hasAuthority('opportunity:create')")
    @Operation(summary = "Create a new opportunity", description = "Creates a new business opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> createOpportunity(
            @Valid @RequestBody OpportunityDTO opportunityDTO) {
        log.debug("REST request to create opportunity: {}", opportunityDTO.getName());
        OpportunityDTO createdOpportunity = opportunityService.createOpportunity(opportunityDTO);
        ApiResponse<OpportunityDTO> response = ApiResponse.success(createdOpportunity, 201);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/opportunities/{id} : Update an existing opportunity.
     *
     * @param id the ID of the opportunity to update
     * @param opportunityDTO the updated opportunity data
     * @return the ResponseEntity with status 200 (OK) and the updated opportunity in the body
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('opportunity:update:all', 'opportunity:update:own', 'opportunity:update:assigned')")
    @Operation(summary = "Update an opportunity", description = "Updates an existing business opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> updateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityDTO opportunityDTO) {
        log.debug("REST request to update opportunity ID: {}", id);
        OpportunityDTO updatedOpportunity = opportunityService.updateOpportunity(id, opportunityDTO);
        ApiResponse<OpportunityDTO> response = ApiResponse.success(updatedOpportunity);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/opportunities/{id} : Delete an opportunity.
     *
     * @param id the ID of the opportunity to delete
     * @return the ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('opportunity:delete')")
    @Operation(summary = "Delete an opportunity", description = "Soft-deletes an existing business opportunity")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        log.debug("REST request to delete opportunity ID: {}", id);
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * PATCH /api/v1/opportunities/{id}/status : Update the status of an opportunity.
     *
     * @param id the ID of the opportunity
     * @param status the new status value
     * @param note optional note about the status change
     * @return the ResponseEntity with status 200 (OK) and the updated opportunity in the body
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('opportunity:update:all', 'opportunity:update:own', 'opportunity:update:assigned')")
    @Operation(summary = "Update opportunity status", description = "Updates the status of an existing business opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String note) {
        log.debug("REST request to update status of opportunity ID: {} to {}", id, status);
        OpportunityDTO updatedOpportunity = opportunityService.updateStatus(id, status, note);
        ApiResponse<OpportunityDTO> response = ApiResponse.success(updatedOpportunity);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/v1/opportunities/{id}/closing-info : Update closing information of an opportunity.
     *
     * @param id the ID of the opportunity
     * @param closingDate the estimated closing date (YYYY-MM-DD)
     * @param closingProbability the probability of closing (0-100)
     * @return the ResponseEntity with status 200 (OK) and the updated opportunity in the body
     */
    @PatchMapping("/{id}/closing-info")
    @PreAuthorize("hasAnyAuthority('opportunity:update:all', 'opportunity:update:own', 'opportunity:update:assigned')")
    @Operation(summary = "Update closing information", description = "Updates the closing date and probability of an existing business opportunity")
    public ResponseEntity<ApiResponse<OpportunityDTO>> updateClosingInfo(
            @PathVariable Long id,
            @RequestParam(required = false) String closingDate,
            @RequestParam(required = false) Integer closingProbability) {
        log.debug("REST request to update closing info of opportunity ID: {}", id);
        OpportunityDTO updatedOpportunity = opportunityService.updateClosingInfo(id, closingDate, closingProbability);
        ApiResponse<OpportunityDTO> response = ApiResponse.success(updatedOpportunity);
        return ResponseEntity.ok(response);
    }
} 