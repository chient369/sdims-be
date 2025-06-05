package com.company.internalmgmt.modules.dashboard.dto.kpi;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Important for error handling
@AllArgsConstructor
public class SalesKpiSummaryMetricsDTO {
    private int totalSalesPersons;
    private Double averageAchievementRate;
    private KpiAchievementRangeDTO byAchievementRange;
    private List<SalesKpiDetailDTO> topPerformers;
    private List<SalesKpiDetailDTO> lowestPerformers;
} 