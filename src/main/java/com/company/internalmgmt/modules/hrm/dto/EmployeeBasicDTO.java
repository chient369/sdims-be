package com.company.internalmgmt.modules.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for Employee entity with minimal information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBasicDTO {
    private Long id;
    private String name;
    private String position;
    private TeamBasicDTO team;

    /**
     * Basic DTO for Team entity with minimal information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamBasicDTO {
        private Long id;
        private String name;
    }
} 