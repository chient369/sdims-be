package com.company.internalmgmt.modules.opportunity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Opportunity data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityDTO {
    
    /**
     * Opportunity ID.
     */
    private Long id;
    
    /**
     * Opportunity code.
     */
    private String code;

    /**
     * Opportunity name.
     */
    private String name;
    
    /**
     * Opportunity description.
     */
    private String description;
    
    /**
     * Customer name.
     */
    private String customerName;
    
    /**
     * Customer contact person name.
     */
    private String customerContact;
    
    /**
     * Customer email.
     */
    private String customerEmail;
    
    /**
     * Customer phone number.
     */
    private String customerPhone;
    
    /**
     * Opportunity amount.
     */
    private BigDecimal amount;
    
    /**
     * Currency code (USD, VND, etc.).
     */
    private String currency;
    
    /**
     * Current status.
     */
    private String status;
    
    /**
     * Deal size category.
     */
    private String dealSize;
    
    /**
     * Source of the opportunity (hubspot, manual, website, etc.).
     */
    private String source;
    
    /**
     * External ID for reference in external systems (e.g., Hubspot)
     */
    private String externalId;
    
    /**
     * Expected closing date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate closingDate;
    
    /**
     * Probability of closing (0-100%)
     */
    private Integer closingProbability;
    
    /**
     * Person who created the opportunity
     */
    private UserSummaryDTO createdBy;
    
    /**
     * User the opportunity is assigned to.
     */
    private UserSummaryDTO assignedTo;
    
    /**
     * Employees assigned to this opportunity.
     */
    private List<EmployeeAssignmentDTO> employeeAssignments;
    
    /**
     * Date of last interaction.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastInteractionDate;
    
    /**
     * Whether this opportunity is marked as priority.
     */
    private Boolean priority;
    
    /**
     * Creation timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    
    /**
     * Last update timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;
    
    /**
     * Tags associated with this opportunity.
     */
    private List<String> tags;
    
    /**
     * Summary DTO for User information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String position;
    }
    
    /**
     * DTO for Employee assignment information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeAssignmentDTO {
        private Long id;
        private Long employeeId;
        private String employeeName;
        private String employeeCode;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime assignedAt;
    }
} 