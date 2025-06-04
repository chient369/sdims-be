package com.company.internalmgmt.modules.opportunity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for assigning a leader to an opportunity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignLeaderRequest {
    
    /**
     * The ID of the leader to assign.
     */
    @NotNull(message = "Leader ID is required")
    private Long leaderId;
    
    /**
     * Optional note about the assignment.
     */
    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
    
    /**
     * Whether to notify the leader about the assignment.
     */
    private Boolean notifyLeader;
    
    /**
     * Whether to notify the sales about the assignment.
     */
    private Boolean notifySales;
} 