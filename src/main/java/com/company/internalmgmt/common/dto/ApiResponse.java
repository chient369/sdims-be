package com.company.internalmgmt.common.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * General API response
 * @param <T> type of data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private Integer code;
    private String errorCode;
    private T data;
    private List<FieldError> errors;
    private String message;
    private PageableInfo pageableInfo;
    
    public ApiResponse(String status, String message) {
        this.status = status;
        this.code = 200;
        this.message = message;
    }
    
    /**
     * Set pageable information
     * 
     * @param pageableInfo The pageable information
     */
    public void setPageableInfo(PageableInfo pageableInfo) {
        this.pageableInfo = pageableInfo;
    }
    
    // Utility methods for common responses
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(200)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, Integer code) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(code)
                .data(data)
                .build();
    }
    
    /**
     * Create a successful response with pageable information
     * 
     * @param <T> Type of data
     * @param data The data
     * @param pageableInfo The pageable information
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, PageableInfo pageableInfo) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(200)
                .data(data)
                .pageableInfo(pageableInfo)
                .build();
    }
    
    /**
     * Create an error response with default HTTP status code 400 (Bad Request)
     * 
     * @param errorCode Error code
     * @param message Error message
     * @return Error response
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(400)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message, Integer httpStatusCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(httpStatusCode)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message, List<FieldError> errors, Integer httpStatusCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(httpStatusCode)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .build();
    }
    
    // Inner class for field validation errors
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
} 
