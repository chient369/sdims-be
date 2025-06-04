package com.company.internalmgmt.modules.admin.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.dto.auth.*;
import com.company.internalmgmt.modules.admin.service.AuthService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login endpoint
     * @param loginRequest login credentials
     * @return JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse));
    }

    /**
     * Register endpoint
     * @param signUpRequest registration data
     * @return success message
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MessageResponse>> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        MessageResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Logout endpoint
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logoutUser() {
        ApiResponse<MessageResponse> response = authService.logout();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user info
     * @return user information
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<JwtResponse.UserInfoDto>> getCurrentUser() {
        ApiResponse<JwtResponse.UserInfoDto> response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh token endpoint
     * @param request refresh token
     * @return new JWT token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        ApiResponse<JwtResponse> response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
} 
