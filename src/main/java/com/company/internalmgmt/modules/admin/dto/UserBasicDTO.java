package com.company.internalmgmt.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for User entity with minimal information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicDTO {
    private Long id;
    private String name;
} 