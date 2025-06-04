package com.company.internalmgmt.modules.dashboard.dto.margin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for margin report response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarginReportDTO {
    private ReportInfoDTO reportInfo;
    private SummaryMetricsDTO summaryMetrics;
    private List<Object> content; // Can be either EmployeeMarginDTO or TeamMarginDTO
    private PageableDTO pageable;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportInfoDTO {
        private String reportName;
        private String generatedAt;
        private String period;
        private String fromDate;
        private String toDate;
        private Object filters;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryMetricsDTO {
        private Double averageMargin;
        private Integer redCount;
        private Integer yellowCount;
        private Integer greenCount;
        private MarginDistributionDTO marginDistribution;
        private List<MarginTrendDTO> marginTrend;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarginDistributionDTO {
        private List<String> labels;
        private List<Integer> values;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarginTrendDTO {
        private String period;
        private Double value;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeMarginDTO {
        private Integer employeeId;
        private String employeeCode;
        private String employeeName;
        private TeamBasicDTO team;
        private String position;
        private List<MarginDataDTO> marginData;
        private Double averageMargin;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamMarginDTO {
        private Integer teamId;
        private String teamName;
        private LeaderDTO leader;
        private Integer employeeCount;
        private List<TeamMarginDataDTO> marginData;
        private Double averageMargin;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamBasicDTO {
        private Integer id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaderDTO {
        private Integer id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarginDataDTO {
        private String period;
        private Long cost;
        private Long revenue;
        private Double margin;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamMarginDataDTO {
        private String period;
        private Long totalCost;
        private Long totalRevenue;
        private Double margin;
        private String status;
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
} 