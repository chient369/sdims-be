package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for utilization rate widget data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilizationRateDTO {
    private Double overall;
    private List<TeamUtilizationDTO> byTeam;
    private List<UtilizationTrendDTO> trend;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamUtilizationDTO {
        private String team;
        private Double rate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UtilizationTrendDTO {
        private String month;
        private Double value;
    }
} 