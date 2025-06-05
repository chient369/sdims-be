package com.company.internalmgmt.modules.dashboard.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableDTO {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements; // Changed to long to match Spring's Page
    private String sort;
} 