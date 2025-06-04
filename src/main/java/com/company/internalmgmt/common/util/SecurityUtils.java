package com.company.internalmgmt.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.internalmgmt.security.jwt.UserDetailsImpl;

/**
 * Utility class for security related operations
 */
public class SecurityUtils {
    
    /**
     * Get the current user ID from the authentication object
     * 
     * @param authentication the authentication object
     * @return the current user ID
     */
    public static Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }
    
    /**
     * Get the current user ID from the security context
     * 
     * @return the current user ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getCurrentUserId(authentication);
    }
} 