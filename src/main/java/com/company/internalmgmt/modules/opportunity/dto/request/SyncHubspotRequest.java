package com.company.internalmgmt.modules.opportunity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * DTO for Hubspot synchronization request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncHubspotRequest {
    
    /**
     * The mode of synchronization.
     * Valid values: "FULL", "INCREMENTAL", "SELECTIVE"
     */
    private String syncMode;
    
    /**
     * Start date for incremental sync (in ISO format).
     */
    private String fromDate;
    
    /**
     * End date for incremental sync (in ISO format).
     */
    private String toDate;
    
    /**
     * Filter by deal stage.
     */
    private String dealStage;
    
    /**
     * Whether to overwrite existing opportunities or only add new ones.
     */
    private Boolean overwriteExisting;
} 