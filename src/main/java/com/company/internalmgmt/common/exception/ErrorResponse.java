package com.company.internalmgmt.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard error response for API errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String traceId;
    private List<Map<String, String>> errors;

    /**
     * Constructor with error code, message, and details
     *
     * @param code Error code
     * @param message Error message
     * @param details Error details
     */
    public ErrorResponse(String code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with error code, message, details, and errors list
     *
     * @param code Error code
     * @param message Error message
     * @param details Error details
     * @param errors List of field errors
     */
    public ErrorResponse(String code, String message, String details, List<Map<String, String>> errors) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
    
    /**
     * Constructor with error code, message, details, and traceId
     *
     * @param code Error code
     * @param message Error message
     * @param details Error details
     * @param traceId Trace ID for tracking the error
     */
    public ErrorResponse(String code, String message, String details, String traceId) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.traceId = traceId;
    }
} 
