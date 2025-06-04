package com.company.internalmgmt.modules.opportunity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for summarizing opportunity statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitySummaryDTO {
    
    /**
     * Total count of opportunities.
     */
    private Integer totalCount;
    
    /**
     * Total monetary value of all opportunities.
     */
    private BigDecimal totalAmount;
    
    /**
     * Distribution of opportunities by status.
     */
    @Builder.Default
    private Map<String, Integer> byStatus = new HashMap<>();
    
    /**
     * Distribution of opportunities by deal size.
     */
    @Builder.Default
    private Map<String, Integer> byDealSize = new HashMap<>();
    
    /**
     * Initialize default status distribution.
     */
    public void initializeDefaultStatusDistribution() {
        byStatus.put("new", 0);
        byStatus.put("contacted", 0);
        byStatus.put("qualified", 0);
        byStatus.put("proposal", 0);
        byStatus.put("negotiation", 0);
        byStatus.put("won", 0);
        byStatus.put("lost", 0);
        byStatus.put("closed", 0);
    }
    
    /**
     * Initialize default deal size distribution.
     */
    public void initializeDefaultDealSizeDistribution() {
        byDealSize.put("small", 0);
        byDealSize.put("medium", 0);
        byDealSize.put("large", 0);
        byDealSize.put("extra_large", 0);
    }
} 