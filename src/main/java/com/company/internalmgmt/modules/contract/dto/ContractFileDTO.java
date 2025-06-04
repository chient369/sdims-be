package com.company.internalmgmt.modules.contract.dto;

import java.time.LocalDateTime;

import com.company.internalmgmt.modules.admin.dto.UserBasicDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ContractFile entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractFileDTO {
    private Long id;
    private String name;
    private String type;
    private Long size;
    private LocalDateTime uploadedAt;
    private UserBasicDTO uploadedBy;
    private String url;
    private String description;
} 