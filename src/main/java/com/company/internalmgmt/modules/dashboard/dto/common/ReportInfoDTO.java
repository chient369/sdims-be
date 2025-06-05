package com.company.internalmgmt.modules.dashboard.dto.common;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportInfoDTO<T_PERIOD_DETAILS> {
    private String reportName;
    private String generatedAt;
    private Map<String, Object> filters;
    private T_PERIOD_DETAILS periodDetails; // Generic field for period-specific details
    private String error; // Field for error messages
} 