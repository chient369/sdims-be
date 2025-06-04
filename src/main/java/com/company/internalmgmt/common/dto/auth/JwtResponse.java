package com.company.internalmgmt.common.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response containing JWT authentication token and user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String token_type = "Bearer";
    private Integer expires_in;
    private String refreshToken;
    private UserInfoDto user;
    
    /**
     * User information data transfer object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String email;
        private String fullname;
        private String role;
        private List<String> permissions;
        private Map<String, Object> settings;
    }
} 
