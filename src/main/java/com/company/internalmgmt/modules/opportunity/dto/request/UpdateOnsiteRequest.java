package com.company.internalmgmt.modules.opportunity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request DTO for updating onsite priority.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOnsiteRequest {
    
    /**
     * The priority flag (true for onsite priority, false for no priority).
     */
    @NotNull(message = "Priority is required")
    private Boolean priority;
    
    /**
     * Optional note about the priority change.
     */
    @Size(max = 500, message = "Note must be less than 500 characters")
    private String note;
} 