package com.company.internalmgmt.modules.opportunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for Opportunity entity with minimal information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityBasicDTO {
    private Long id;
    private String code;
    private String name;
} 