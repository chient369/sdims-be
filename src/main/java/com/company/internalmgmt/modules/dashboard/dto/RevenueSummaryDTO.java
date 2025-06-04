package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for revenue summary widget data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueSummaryDTO {
    private RevenuePeriodDTO currentMonth;
    private RevenuePeriodDTO currentQuarter;
    private RevenuePeriodDTO ytd;
    private ContractsDTO contracts;
    private PaymentDTO payment;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenuePeriodDTO {
        private Long target;
        private Long actual;
        private Double achievement;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractsDTO {
        private Integer total;
        private Integer newlyAdded;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentDTO {
        private Long totalDue;
        private Long overdue;
        private Long upcoming;
    }
} 