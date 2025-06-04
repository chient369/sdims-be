package com.company.internalmgmt.common.exception;

/**
 * Exception for authentication and authorization errors
 */
public class AuthException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Constructor with message
     * 
     * @param message error message
     */
    public AuthException(String message) {
        super(message);
        this.errorCode = "E1000";
    }
    
    /**
     * Constructor with message and error code
     * 
     * @param message error message
     * @param errorCode error code
     */
    public AuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with message and cause
     * 
     * @param message error message
     * @param cause error cause
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "E1000";
    }
    
    /**
     * Constructor with message, cause, and error code
     * 
     * @param message error message
     * @param cause error cause
     * @param errorCode error code
     */
    public AuthException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Get error code
     * 
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }
} 