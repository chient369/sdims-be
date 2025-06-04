package com.company.internalmgmt.common.dto.auth;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login request containing username and password
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private Boolean remember_me = false;
    
    // Getter method for rememberMe field to maintain backward compatibility
    public Boolean getRememberMe() {
        return remember_me;
    }
} 
