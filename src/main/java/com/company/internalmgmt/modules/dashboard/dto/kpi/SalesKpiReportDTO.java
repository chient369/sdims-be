package com.company.internalmgmt.modules.dashboard.dto.kpi;

import java.util.List;

import com.company.internalmgmt.modules.dashboard.dto.common.PageableDTO;
import com.company.internalmgmt.modules.dashboard.dto.common.ReportInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Important for error handling
@AllArgsConstructor
public class SalesKpiReportDTO {
    private ReportInfoDTO<KpiPeriodInfoDTO> reportInfo;
    private SalesKpiSummaryMetricsDTO summaryMetrics;
    private List<SalesKpiDetailDTO> content;
    private PageableDTO pageable;
} 