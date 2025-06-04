package com.company.internalmgmt.modules.opportunity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for the response after initiating Hubspot synchronization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncHubspotResponse {
    
    /**
     * The unique ID of the synchronization process.
     */
    private String syncId;
    
    /**
     * A message describing the synchronization status.
     */
    private String message;
    
    /**
     * The status of the synchronization.
     * Possible values: "queued", "processing", "completed", "failed"
     */
    private String status;
    
    /**
     * Estimated time to complete in seconds
     */
    private Integer estimatedTime;
    
    /**
     * The parameters used for synchronization
     */
    private SyncParamsDTO syncParams;
    
    /**
     * URL to view the sync logs
     */
    private String logsUrl;
    
    /**
     * DTO for synchronization parameters.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncParamsDTO {
        private String syncMode;
        private String fromDate;
        private String toDate;
        private String dealStage;
        private Boolean overwriteExisting;
    }
} 