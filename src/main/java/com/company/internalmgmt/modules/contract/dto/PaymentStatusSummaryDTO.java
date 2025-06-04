package com.company.internalmgmt.modules.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment status summary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusSummaryDTO {
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidPercentage;
    private LocalDate nextDueDate;
    private BigDecimal nextDueAmount;
    private Integer totalTerms;
    private Integer paidTerms;
    private BigDecimal remainingAmount;
} 