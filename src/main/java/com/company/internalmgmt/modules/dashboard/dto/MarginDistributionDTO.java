package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for margin distribution widget data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarginDistributionDTO {
    private Integer totalEmployees;
    private DistributionDTO distribution;
    private List<TrendItemDTO> trend;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DistributionDTO {
        private DistributionCategoryDTO green;
        private DistributionCategoryDTO yellow;
        private DistributionCategoryDTO red;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DistributionCategoryDTO {
        private Integer count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendItemDTO {
        private String month;
        private Double value;
    }
} 