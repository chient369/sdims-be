package com.company.internalmgmt.modules.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Common DTOs for various reports
 */
public class CommonReportDTO {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportInfoDTO {
        private String reportName;
        private String generatedAt;
        private Object filters;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageableDTO {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalPages;
        private Integer totalElements;
        private String sort;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerSummaryDTO {
        private String name;
        private Integer count;
        private Long value;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesSummaryDTO {
        private String name;
        private Integer count;
        private Long value;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerBasicDTO {
        private Integer id;
        private String name;
        private String industry;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesBasicDTO {
        private Integer id;
        private String name;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaderBasicDTO {
        private Integer id;
        private String name;
        private String assignDate;
    }
} 