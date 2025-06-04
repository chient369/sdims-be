package com.company.internalmgmt.modules.opportunity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * Request DTO for filtering and pagination of opportunities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListOpportunitiesRequest {
    
    /**
     * Search keyword to filter by name, code, client name.
     */
    private String keyword;
    
    /**
     * Filter by status (comma-separated list).
     */
    private String status;
    
    /**
     * Filter by deal size (small, medium, large, extra_large).
     */
    @Pattern(regexp = "^(small|medium|large|extra_large)$", message = "Deal size must be one of: small, medium, large, extra_large")
    private String dealSize;
    
    /**
     * Filter by minimum amount.
     */
    @Min(value = 0, message = "From amount must be a positive number")
    private BigDecimal fromAmount;
    
    /**
     * Filter by maximum amount.
     */
    @Min(value = 0, message = "To amount must be a positive number")
    private BigDecimal toAmount;
    
    /**
     * Filter by onsite priority.
     */
    private Boolean priority;
    
    /**
     * Filter by assigned user ID.
     */
    @Min(value = 1, message = "Assigned to ID must be a positive number")
    private Long assignedTo;
    
    /**
     * Filter by assigned employee ID.
     */
    @Min(value = 1, message = "Employee ID must be a positive number")
    private Long employeeId;
    
    /**
     * Filter by creator ID.
     */
    @Min(value = 1, message = "Created by ID must be a positive number")
    private Long createdBy;
    
    /**
     * Filter from creation date (format: YYYY-MM-DD).
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "From date must be in format YYYY-MM-DD")
    private String fromDate;
    
    /**
     * Filter to creation date (format: YYYY-MM-DD).
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "To date must be in format YYYY-MM-DD")
    private String toDate;
    
    /**
     * Filter from last interaction date (format: YYYY-MM-DD).
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "From last interaction date must be in format YYYY-MM-DD")
    private String fromLastInteractionDate;
    
    /**
     * Filter to last interaction date (format: YYYY-MM-DD).
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "To last interaction date must be in format YYYY-MM-DD")
    private String toLastInteractionDate;
    
    /**
     * Field to sort by.
     */
    @Pattern(regexp = "^(createdAt|updatedAt|name|clientName|amount|status|closingDate|lastInteractionDate)$", 
             message = "Sort by must be one of: createdAt, updatedAt, name, clientName, amount, status, closingDate, lastInteractionDate")
    private String sortBy = "createdAt";
    
    /**
     * Sort direction (asc or desc).
     */
    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be either asc or desc")
    private String sortDir = "desc";
    
    /**
     * Page number (1-based).
     */
    @Min(value = 1, message = "Page must be greater than or equal to 1")
    private Integer page = 1;
    
    /**
     * Page size.
     */
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Max(value = 100, message = "Size must be less than or equal to 100")
    private Integer size = 20;
} 