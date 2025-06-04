package com.company.internalmgmt.modules.opportunity.dto.response;

import com.company.internalmgmt.common.dto.PageableInfo;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for the list opportunities endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListOpportunitiesResponse {
    
    /**
     * Summary statistics about the opportunities.
     */
    private OpportunitySummaryDTO summary;
    
    /**
     * List of opportunities for the current page.
     */
    private List<OpportunityDTO> content;
    
    /**
     * Pagination information.
     */
    private PageableInfo pageable;
} 