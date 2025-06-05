package com.company.internalmgmt.modules.dashboard.dto.kpi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Important for error handling if an empty object is built
@AllArgsConstructor
public class KpiAchievementRangeDTO {
    private int belowTargetCount;    // e.g., < 70%
    private int nearTargetCount;     // e.g., 70-89.99%
    private int onTargetCount;       // e.g., 90-109.99%
    private int exceedsTargetCount;  // e.g., >= 110%
} 