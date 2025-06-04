package com.company.internalmgmt.modules.margin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMarginDTO {
    private Long employeeId;
    private String employeeCode;
    private String name;
    private String position;
    private TeamDTO team;
    private String status; // Allocated, Bench, EndingSoon, etc.
    private String currentProject;
    private Integer allocation; // 0-100%
    private List<PeriodMarginDTO> periods;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDTO {
        private Long id;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodMarginDTO {
        private String period; // "YYYY-MM" format
        private String periodLabel; // "Tháng M/YYYY" format
        private BigDecimal cost;
        private BigDecimal revenue;
        private BigDecimal margin; // Percentage
        private String marginStatus; // Red, Yellow, Green
    }
} 