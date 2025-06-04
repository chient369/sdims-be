package com.company.internalmgmt.modules.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ContractPaymentTerm entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractPaymentTermDTO {
    private Long id;
    private Integer termNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal percentage;
    private String description;
    private String status;
    private LocalDate paidDate;
    private BigDecimal paidAmount;
    private String notes;
} 