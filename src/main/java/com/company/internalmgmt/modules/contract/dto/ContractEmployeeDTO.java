package com.company.internalmgmt.modules.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.internalmgmt.modules.hrm.dto.EmployeeBasicDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ContractEmployee entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractEmployeeDTO {
    private Long id;
    private EmployeeBasicDTO employee;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal allocationPercentage;
    private BigDecimal billRate;
    private String role;
} 