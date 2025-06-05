package com.company.internalmgmt.modules.dashboard.dto.kpi;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesKpiDetailDTO {
    private Long salesPersonId;
    private String salesPersonName;
    private String teamName;
    private String period; // Formatted period string, e.g., "2023-Q4", "2023-12"
    private BigDecimal kpiTargetAmount;
    private BigDecimal actualRevenue;
    private Double achievementRate; // percentage
    private String status; // e.g., "Vượt mục tiêu", "Đạt mục tiêu", "Dưới mục tiêu"
} 