package com.company.internalmgmt.modules.dashboard.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for employee report response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReportDTO {
    private ReportInfoDTO reportInfo;
    private List<EmployeeDetailDTO> content;
    private SummaryMetricsDTO summaryMetrics;
    private PageableDTO pageable;
    
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
    public static class EmployeeDetailDTO {
        private Integer id;
        private String employeeCode;
        private String name;
        private String email;
        private String position;
        private TeamDTO team;
        private String status;
        private ProjectDTO currentProject;
        private Integer utilization;
        private List<SkillDTO> skills;
        private String joinDate;
        private Integer totalExperience;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamDTO {
        private Integer id;
        private String name;
        private LeaderDTO leader;
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
    public static class ProjectDTO {
        private Integer id;
        private String name;
        private String customer;
        private Integer allocation;
        private String startDate;
        private String endDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDTO {
        private Integer id;
        private String name;
        private String category;
        private String level;
        private Integer years;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryMetricsDTO {
        private Integer totalEmployees;
        private Integer allocatedCount;
        private Integer availableCount;
        private Integer endingSoonCount;
        private Double utilizationRate;
        private List<TopSkillDTO> topSkills;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopSkillDTO {
        private String name;
        private Integer count;
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