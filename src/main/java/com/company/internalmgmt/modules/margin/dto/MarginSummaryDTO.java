package com.company.internalmgmt.modules.margin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarginSummaryDTO {
    private SummaryDTO summary;
    private List<TeamMarginDTO> teams;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private String period; // "month", "quarter", "year"
        private String periodLabel; // "Tháng 5/2025", "Q2 2025", "2025"
        private Integer totalTeams;
        private Integer totalEmployees;
        private BigDecimal averageCost;
        private BigDecimal averageRevenue;
        private BigDecimal averageMargin;
        private Map<String, Integer> statusCounts; // Map<"Red"|"Yellow"|"Green", count>
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMarginDTO {
        private Long id;
        private String name;
        private Integer employeeCount;
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal margin;
        private String marginStatus; // Red, Yellow, Green
        private Map<String, Integer> statusCounts; // Map<"Red"|"Yellow"|"Green", count>
        private TrendsDTO trends;
        private List<PeriodMarginDTO> periods;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TrendsDTO {
            private List<BigDecimal> margin;
            private List<String> periods;
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodMarginDTO {
        private String period;
        private String periodLabel;
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal margin;
        private String marginStatus;
    }
} 