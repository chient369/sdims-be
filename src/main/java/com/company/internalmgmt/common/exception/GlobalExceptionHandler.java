package com.company.internalmgmt.common.exception;

import com.company.internalmgmt.modules.hrm.exception.EmployeeNotFoundException;
import com.company.internalmgmt.modules.hrm.exception.EmployeeSkillNotFoundException;
import com.company.internalmgmt.modules.hrm.exception.ImportExportException;
import com.company.internalmgmt.modules.hrm.exception.InvalidEmployeeDataException;
import com.company.internalmgmt.modules.hrm.exception.InvalidStatusTransitionException;
import com.company.internalmgmt.modules.hrm.exception.ProjectHistoryNotFoundException;
import com.company.internalmgmt.modules.hrm.exception.SkillCategoryNotFoundException;
import com.company.internalmgmt.modules.hrm.exception.SkillNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3000",
                "Resource not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle EmployeeNotFoundException
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        log.error("Employee not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3001",
                "Employee not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle SkillNotFoundException
     */
    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSkillNotFoundException(SkillNotFoundException ex) {
        log.error("Skill not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3006",
                "Skill not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle SkillCategoryNotFoundException
     */
    @ExceptionHandler(SkillCategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSkillCategoryNotFoundException(SkillCategoryNotFoundException ex) {
        log.error("Skill category not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3000",
                "Skill category not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle EmployeeSkillNotFoundException
     */
    @ExceptionHandler(EmployeeSkillNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeSkillNotFoundException(EmployeeSkillNotFoundException ex) {
        log.error("Employee skill not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3000",
                "Employee skill not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle ProjectHistoryNotFoundException
     */
    @ExceptionHandler(ProjectHistoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectHistoryNotFoundException(ProjectHistoryNotFoundException ex) {
        log.error("Project history not found exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E3000",
                "Project history not found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle InvalidEmployeeDataException
     */
    @ExceptionHandler(InvalidEmployeeDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmployeeDataException(InvalidEmployeeDataException ex) {
        log.error("Invalid employee data exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E4001",
                "Invalid employee data",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle InvalidStatusTransitionException
     */
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransitionException(InvalidStatusTransitionException ex) {
        log.error("Invalid status transition exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E4006",
                "Invalid status transition",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle ImportExportException
     */
    @ExceptionHandler(ImportExportException.class)
    public ResponseEntity<ErrorResponse> handleImportExportException(ImportExportException ex) {
        log.error("Import/Export exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E7000",
                "Import/Export error",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E2000",
                "Invalid input",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E1002",
                "Access denied",
                "You don't have permission to perform this action"
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle BadCredentialsException
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException ex) {
        log.error("Authentication failed", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E1001",
                "Đăng nhập không thành công",
                "Tên đăng nhập hoặc mật khẩu không đúng"
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle JWT related exceptions
     */
    @ExceptionHandler({
            io.jsonwebtoken.ExpiredJwtException.class,
            io.jsonwebtoken.MalformedJwtException.class,
            io.jsonwebtoken.SignatureException.class,
            io.jsonwebtoken.UnsupportedJwtException.class
    })
    public ResponseEntity<ErrorResponse> handleJwtExceptions(Exception ex) {
        log.error("JWT token error", ex);
        
        String detail = "Phiên đăng nhập không hợp lệ";
        String message = "Lỗi xác thực";
        
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            detail = "Phiên đăng nhập đã hết hạn";
            message = "Phiên hết hạn";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E1003",
                message,
                detail
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle TokenRefreshException
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException ex) {
        log.error("Token refresh exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E1004",
                "Refresh token không hợp lệ",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle AuthException
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        log.error("Auth exception", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                "Lỗi xác thực",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle account status exceptions
     */
    @ExceptionHandler({
            org.springframework.security.authentication.DisabledException.class,
            org.springframework.security.authentication.LockedException.class
    })
    public ResponseEntity<ErrorResponse> handleAccountStatusExceptions(Exception ex) {
        log.error("Account status exception", ex);
        
        String detail = "Tài khoản không khả dụng";
        String message = "Lỗi trạng thái tài khoản";
        
        if (ex instanceof org.springframework.security.authentication.DisabledException) {
            detail = "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.";
        } else if (ex instanceof org.springframework.security.authentication.LockedException) {
            detail = "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E1005",
                message,
                detail
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception", ex);
        
        String traceId = UUID.randomUUID().toString();
        
        // Log with trace ID for easier tracking
        log.error("Runtime exception [traceId={}]", traceId, ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E5000",
                "Lỗi hệ thống",
                ex.getMessage(),
                traceId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        log.error("Validation exception", ex);
        
        List<Map<String, String>> errorsDetails = new ArrayList<>();
        
        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach(error -> {
                Map<String, String> errorDetail = new HashMap<>();
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorDetail.put("field", fieldName);
                errorDetail.put("message", errorMessage);
                errorsDetails.add(errorDetail);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                Map<String, String> errorDetail = new HashMap<>();
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorDetail.put("field", fieldName);
                errorDetail.put("message", errorMessage);
                errorsDetails.add(errorDetail);
            });
        }
        
        String errorDetails = errorsDetails.stream()
                .map(map -> map.get("field") + ": " + map.get("message"))
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E2000",
                "Validation failed",
                errorDetails,
                errorsDetails
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle file upload size exceeded exception
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("Max upload size exceeded", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E2004",
                "File too large",
                "The uploaded file exceeds the maximum allowed size"
        );
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception", ex);
        
        String traceId = UUID.randomUUID().toString();
        
        // Log with trace ID for easier tracking
        log.error("Unexpected exception [traceId={}]", traceId, ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "E6000",
                "Internal server error",
                "An unexpected error occurred. Trace ID: " + traceId,
                traceId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 
