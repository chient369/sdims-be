package com.company.internalmgmt.modules.opportunity.dto.response;

import com.company.internalmgmt.common.dto.PageableInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for the response containing synchronization logs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncLogResponse {
    
    /**
     * List of sync log entries.
     */
    private List<SyncLogEntryDTO> content;
    
    /**
     * Pagination information.
     */
    private PageableInfo pageable;
    
    /**
     * Detailed sync log (for single log endpoint).
     */
    private SyncLogDTO syncLog;
    
    /**
     * Details about processed opportunities (for single log endpoint).
     */
    private List<SyncDetailDTO> details;
    
    /**
     * Process logs (for single log endpoint).
     */
    private List<LogEntryDTO> logs;
    
    /**
     * DTO for a single sync log entry.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncLogEntryDTO {
        
        /**
         * The unique ID of the synchronization log.
         */
        private Long id;
        
        /**
         * The unique ID of the synchronization process.
         */
        private String syncId;
        
        /**
         * The status of the synchronization.
         */
        private String status;
        
        /**
         * The time when the synchronization was started.
         */
        private LocalDateTime startedAt;
        
        /**
         * The time when the synchronization was completed.
         */
        private LocalDateTime completedAt;
        
        /**
         * The duration of the synchronization in seconds.
         */
        private Integer duration;
        
        /**
         * The mode of synchronization.
         */
        private String syncMode;
        
        /**
         * The number of opportunities processed.
         */
        private Integer totalOpportunities;
        
        /**
         * The number of new opportunities created.
         */
        private Integer newOpportunities;
        
        /**
         * The number of opportunities updated.
         */
        private Integer updatedOpportunities;
        
        /**
         * The number of opportunities skipped.
         */
        private Integer skippedOpportunities;
        
        /**
         * The number of opportunities that failed to sync.
         */
        private Integer failedOpportunities;
    }
    
    /**
     * DTO for a sync log entry.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncLogDTO {
        private String syncId;
        private String status;
        private UserDTO initiatedBy;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private Integer duration;
        private SyncParamsDTO syncParams;
        private SummaryDTO summary;
        private ErrorDTO error;
    }
    
    /**
     * DTO for user information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String name;
    }
    
    /**
     * DTO for sync parameters.
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
    
    /**
     * DTO for sync summary.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private Integer totalOpportunities;
        private Integer newOpportunities;
        private Integer updatedOpportunities;
        private Integer skippedOpportunities;
        private Integer failedOpportunities;
    }
    
    /**
     * DTO for error information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDTO {
        private String code;
        private String message;
    }
    
    /**
     * DTO for sync detail.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncDetailDTO {
        private String hubspotDealId;
        private String action;
        private String status;
        private OpportunityInfoDTO opportunity;
        private String reason;
        private List<ChangeDTO> changes;
        private LocalDateTime timestamp;
    }
    
    /**
     * DTO for opportunity info.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpportunityInfoDTO {
        private Long id;
        private String code;
        private String name;
    }
    
    /**
     * DTO for field change.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeDTO {
        private String field;
        private String oldValue;
        private String newValue;
    }
    
    /**
     * DTO for log entry.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogEntryDTO {
        private String level;
        private String message;
        private LocalDateTime timestamp;
    }
} 