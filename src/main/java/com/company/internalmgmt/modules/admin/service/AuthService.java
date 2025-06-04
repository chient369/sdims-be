package com.company.internalmgmt.modules.admin.service;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.dto.auth.*;
import com.company.internalmgmt.common.exception.AuthException;
import com.company.internalmgmt.common.exception.TokenRefreshException;
import com.company.internalmgmt.modules.admin.model.Permission;
import com.company.internalmgmt.modules.admin.model.Role;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.RoleRepository;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.security.jwt.JwtUtils;
import com.company.internalmgmt.security.jwt.UserDetailsImpl;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private RefreshTokenService refreshTokenService;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = null;
            
            // Generate refresh token if remember me is true
            if (loginRequest.getRememberMe() != null && loginRequest.getRememberMe()) {
                refreshToken = refreshTokenService.createToken(
                        userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new AuthException("User not found", "E1010"))
                );
            }

            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new AuthException("User not found", "E1010"));
            
            // Map roles and permissions
            String roleName = user.getRoles().stream()
                    .findFirst()
                    .map(Role::getName)
                    .orElse("User");
            
            List<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .distinct()
                    .collect(Collectors.toList());
            
            // Build response
            JwtResponse.UserInfoDto userInfo = JwtResponse.UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullname(user.getFullName())
                    .role(roleName)
                    .permissions(permissions)
                    .settings(getDefaultSettings())
                    .build();
            
            return JwtResponse.builder()
                    .token(jwt)
                    .token_type("Bearer")
                    .expires_in((int) jwtUtils.getExpirationTime() / 1000)
                    .refreshToken(refreshToken)
                    .user(userInfo)
                    .build();
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // Đã được xử lý bởi GlobalExceptionHandler
            throw e;
        } catch (org.springframework.security.authentication.DisabledException e) {
            throw new AuthException("Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.", "E1005");
        } catch (org.springframework.security.authentication.LockedException e) {
            throw new AuthException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.", "E1005");
        } catch (Exception e) {
            throw new AuthException("Lỗi xác thực: " + e.getMessage(), e, "E1000");
        }
    }

    public MessageResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AuthException("Tên đăng nhập đã tồn tại", "E1006");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AuthException("Email đã được sử dụng", "E1007");
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Gán role mặc định (ví dụ: ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new AuthException("Role không tồn tại", "E1008"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return new MessageResponse("Đăng ký thành công!");
    }
    
    @Transactional
    public ApiResponse<MessageResponse> logout() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        
        refreshTokenService.revokeAllUserTokens(userId);
        
        MessageResponse response = new MessageResponse("Đăng xuất thành công");
        return ApiResponse.success(response);
    }
    
    public ApiResponse<JwtResponse> refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    User user = refreshToken.getUser();
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    
                    // Create new refresh token (rotation)
                    String newRefreshToken = refreshTokenService.rotateToken(request);
                    
                    // Map roles and permissions
                    String roleName = user.getRoles().stream()
                            .findFirst()
                            .map(Role::getName)
                            .orElse("User");
                    
                    List<String> permissions = user.getRoles().stream()
                            .flatMap(role -> role.getPermissions().stream())
                            .map(Permission::getName)
                            .distinct()
                            .collect(Collectors.toList());
                    
                    // Build response
                    JwtResponse.UserInfoDto userInfo = JwtResponse.UserInfoDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .fullname(user.getFullName())
                            .role(roleName)
                            .permissions(permissions)
                            .settings(getDefaultSettings())
                            .build();
                    
                    JwtResponse response = JwtResponse.builder()
                            .token(token)
                            .token_type("Bearer")
                            .expires_in((int) jwtUtils.getExpirationTime() / 1000)
                            .refreshToken(newRefreshToken)
                            .user(userInfo)
                            .build();
                    
                    return ApiResponse.success(response);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));
    }
    
    public ApiResponse<JwtResponse.UserInfoDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Map roles and permissions
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse("User");
        
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());
        
        // Build response
        JwtResponse.UserInfoDto userInfo = JwtResponse.UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullName())
                .role(roleName)
                .permissions(permissions)
                .settings(getDefaultSettings())
                .build();
        
        return ApiResponse.success(userInfo);
    }
    
    private Map<String, Object> getDefaultSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("notification_enabled", true);
        settings.put("theme", "light");
        settings.put("language", "vi");
        return settings;
    }
} 
