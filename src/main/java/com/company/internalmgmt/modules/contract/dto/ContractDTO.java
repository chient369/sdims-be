package com.company.internalmgmt.modules.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.company.internalmgmt.modules.admin.dto.UserBasicDTO;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityBasicDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Contract entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDTO {
    private Long id;
    private String contractCode;
    private String name;
    private String customerName;
    private String contractType;
    private BigDecimal amount;
    private String currency;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private UserBasicDTO salesPerson;
    private OpportunityBasicDTO relatedOpportunity;
    private String description;
    private List<String> tags;
    private PaymentStatusSummaryDTO paymentStatus;
    private Integer employeeCount;
    private UserBasicDTO createdBy;
    private LocalDateTime createdAt;
    private UserBasicDTO updatedBy;
    private LocalDateTime updatedAt;
} 