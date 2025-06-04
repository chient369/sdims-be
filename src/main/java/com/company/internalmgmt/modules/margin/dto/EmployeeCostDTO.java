package com.company.internalmgmt.modules.margin.dto;

import com.company.internalmgmt.modules.margin.model.EmployeeCost;
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
public class EmployeeCostDTO {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Integer year;
    private Integer month;
    private BigDecimal basicSalary;
    private BigDecimal allowance;
    private BigDecimal overtime;
    private BigDecimal otherCosts;
    private BigDecimal totalCost; // Same as costAmount in entity
    private String currency;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    // Factory method for converting from entity to DTO
    public static EmployeeCostDTO fromEntity(EmployeeCost entity) {
        return EmployeeCostDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .basicSalary(entity.getBasicSalary())
                .allowance(entity.getAllowance())
                .overtime(entity.getOvertime())
                .otherCosts(entity.getOtherCosts())
                .totalCost(entity.getCostAmount())
                .currency(entity.getCurrency())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
} 