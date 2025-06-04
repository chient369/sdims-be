package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main DTO for dashboard summary response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDTO {
    private DateRangeDTO dateRange;
    private WidgetsDTO widgets;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateRangeDTO {
        private String fromDate;
        private String toDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WidgetsDTO {
        private OpportunityStatusDTO opportunityStatus;
        private MarginDistributionDTO marginDistribution;
        private RevenueSummaryDTO revenueSummary;
        private EmployeeStatusDTO employeeStatus;
        private UtilizationRateDTO utilizationRate;
    }
} 