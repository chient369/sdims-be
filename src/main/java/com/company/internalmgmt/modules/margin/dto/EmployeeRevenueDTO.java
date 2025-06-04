package com.company.internalmgmt.modules.margin.dto;

import com.company.internalmgmt.modules.margin.model.EmployeeRevenue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRevenueDTO {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long contractId;
    private String contractName;
    private Integer year;
    private Integer month;
    private BigDecimal billingRate;
    private BigDecimal allocationPercentage;
    private BigDecimal calculatedRevenue;
    private String currency;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    // Factory method for converting from entity to DTO
    public static EmployeeRevenueDTO fromEntity(EmployeeRevenue entity) {
        return EmployeeRevenueDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .contractId(entity.getContractId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .billingRate(entity.getBillingRate())
                .allocationPercentage(entity.getAllocationPercentage())
                .calculatedRevenue(entity.getCalculatedRevenue())
                .currency(entity.getCurrency())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
} 