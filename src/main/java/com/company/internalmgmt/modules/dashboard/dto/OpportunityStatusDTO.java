package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for opportunity status widget data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityStatusDTO {
    private Integer totalOpportunities;
    private ByStatusDTO byStatus;
    private List<DealStageDTO> byDealStage;
    private List<TopOpportunityDTO> topOpportunities;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ByStatusDTO {
        private Integer green;
        private Integer yellow;
        private Integer red;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DealStageDTO {
        private String stage;
        private Integer count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopOpportunityDTO {
        private Integer id;
        private String name;
        private String customer;
        private Long value;
        private String stage;
        private String lastInteraction;
    }
} 