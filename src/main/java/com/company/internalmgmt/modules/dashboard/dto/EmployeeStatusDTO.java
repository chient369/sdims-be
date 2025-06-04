package com.company.internalmgmt.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for employee status widget data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatusDTO {
    private Integer totalEmployees;
    private ByStatusDTO byStatus;
    private List<EmployeeBasicDTO> endingSoonList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ByStatusDTO {
        private Integer allocated;
        private Integer available;
        private Integer endingSoon;
        private Integer onLeave;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeBasicDTO {
        private Integer id;
        private String name;
        private String projectEndDate;
    }
} 