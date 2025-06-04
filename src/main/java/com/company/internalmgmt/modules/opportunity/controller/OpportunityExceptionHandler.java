package com.company.internalmgmt.modules.opportunity.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.opportunity.exception.OpportunityAccessDeniedException;
import com.company.internalmgmt.modules.opportunity.exception.OpportunityConflictException;
import com.company.internalmgmt.modules.opportunity.exception.OpportunityValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception handler for opportunity module controllers.
 * Handles all exceptions thrown by controllers in the opportunity module.
 */
@RestControllerAdvice(basePackages = "com.company.internalmgmt.modules.opportunity.controller")
@Slf4j
public class OpportunityExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("E3000", ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle AccessDeniedException
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler({AccessDeniedException.class, OpportunityAccessDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(Exception ex) {
        log.error("Access denied: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("E1002", "Bạn không có quyền truy cập chức năng này", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle OpportunityConflictException
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(OpportunityConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleOpportunityConflictException(OpportunityConflictException ex) {
        log.error("Conflict: {}", ex.getMessage());
        
        Map<String, Object> errorData = new HashMap<>();
        if (ex.getConflictData() != null) {
            errorData = ex.getConflictData();
        }
        
        List<ApiResponse.FieldError> errors = new ArrayList<>();
        ApiResponse.FieldError error = new ApiResponse.FieldError("sync", ex.getMessage());
        errors.add(error);
        
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status("error")
                .code(HttpStatus.CONFLICT.value())
                .errorCode("E4004")
                .message("Xung đột dữ liệu")
                .errors(errors)
                .data(errorData)
                .build();
                
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle MethodArgumentNotValidException for validation errors
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<ApiResponse.FieldError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return new ApiResponse.FieldError(fieldName, errorMessage);
                })
                .collect(Collectors.toList());
        
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status("error")
                .code(HttpStatus.BAD_REQUEST.value())
                .errorCode("E2000")
                .message("Tham số không hợp lệ")
                .errors(errors)
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle ConstraintViolationException for validation errors
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        
        List<ApiResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String fieldName = violation.getPropertyPath().toString();
                    String errorMessage = violation.getMessage();
                    return new ApiResponse.FieldError(fieldName, errorMessage);
                })
                .collect(Collectors.toList());
        
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status("error")
                .code(HttpStatus.BAD_REQUEST.value())
                .errorCode("E2000")
                .message("Tham số không hợp lệ")
                .errors(errors)
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle OpportunityValidationException
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(OpportunityValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleOpportunityValidationException(OpportunityValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<ApiResponse.FieldError> errors = new ArrayList<>();
        ex.getErrors().forEach((field, message) -> {
            errors.add(new ApiResponse.FieldError(field, message));
        });
        
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status("error")
                .code(HttpStatus.BAD_REQUEST.value())
                .errorCode("E2000")
                .message("Tham số không hợp lệ")
                .errors(errors)
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle general Exception
     * 
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiResponse<Object> response = ApiResponse.error("E5000", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 