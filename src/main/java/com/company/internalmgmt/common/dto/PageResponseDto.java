package com.company.internalmgmt.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for API responses with pagination
 *
 * @param <T> Type of content in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private String status;
    private Integer code;
    private PageData<T> data;
    
    /**
     * Inner class for page data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageData<T> {
        private List<T> content;
        private PageableInfo pageable;
    }
    
    /**
     * Inner class for pageable information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableInfo {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalPages;
        private Long totalElements;
        private String sort;
        private String errorCode;
        private String errorMessage;
    }
    
    /**
     * Create a successful response with paged content
     * 
     * @param <T> Type of content
     * @param content List of content items
     * @param pageNumber Current page number
     * @param pageSize Page size
     * @param totalElements Total number of elements
     * @param totalPages Total number of pages
     * @param sort Sort information
     * @return PageResponseDto instance
     */
    public static <T> PageResponseDto<T> success(
            List<T> content, 
            int pageNumber, 
            int pageSize, 
            long totalElements,
            int totalPages,
            String sort) {
        
        PageableInfo pageable = PageableInfo.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .sort(sort)
                .build();
        
        PageData<T> data = PageData.<T>builder()
                .content(content)
                .pageable(pageable)
                .build();
        
        return PageResponseDto.<T>builder()
                .status("success")
                .code(200)
                .data(data)
                .build();
    }
    
    /**
     * Create a successful response with paged content
     * 
     * @param <T> Type of content
     * @param content List of content items
     * @param pageNumber Current page number
     * @param pageSize Page size
     * @param totalElements Total number of elements
     * @param sort Sort information
     * @return PageResponseDto instance
     */
    public static <T> PageResponseDto<T> success(
            List<T> content, 
            int pageNumber, 
            int pageSize, 
            long totalElements,
            String sort) {
        
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / (double) pageSize) : 0;
        
        return success(content, pageNumber, pageSize, totalElements, totalPages, sort);
    }
    
    /**
     * Create an error response
     * 
     * @param errorCode Error code
     * @param message Error message
     * @return Error response
     */
    public static PageResponseDto<Object> error(String errorCode, String message) {
        return PageResponseDto.builder()
                .status("error")
                .code(400)
                .data(PageData.builder()
                        .content(null)
                        .pageable(PageableInfo.builder()
                                .errorCode(errorCode)
                                .errorMessage(message)
                                .build())
                        .build())
                .build();
    }
} 
