package com.company.internalmgmt.modules.dashboard.dto.kpi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiPeriodInfoDTO {
    private Integer year;
    private Integer quarter;
    private Integer month;
    private String description; // e.g., "Năm 2023", "Quý 4/2023", "Tháng 12/2023"
} 