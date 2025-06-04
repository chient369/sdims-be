package com.company.internalmgmt.modules.margin.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
public class UpdateCostRequestDTO {
    
    @NotBlank(message = "Month is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Month must be in YYYY-MM format")
    private String month;
    
    private Boolean overwrite = false;
    
    @NotEmpty(message = "At least one employee cost entry is required")
    @Size(min = 1, message = "At least one employee cost entry is required")
    @Valid
    private List<EmployeeCostEntry> employees;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeCostEntry {
        private Long employeeId;
        private String employeeCode;
        private BigDecimal basicCost;
        private BigDecimal allowance;
        private BigDecimal overtime;
        private BigDecimal otherCosts;
        private String currency;
        private String note;
    }
} 