package com.company.internalmgmt.modules.admin.service;

import com.company.internalmgmt.common.dto.auth.RefreshTokenRequest;
import com.company.internalmgmt.common.exception.TokenRefreshException;
import com.company.internalmgmt.modules.admin.model.RefreshToken;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.RefreshTokenRepository;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.security.jwt.JwtUtils;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    
    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUserAndRevokedFalse(user);
    }
    
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // Revoke any existing refresh tokens for this user
        refreshTokenRepository.revokeAllUserTokens(userId);
        
        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public String createToken(User user) {
        RefreshToken token = createRefreshToken(user.getId());
        return token.getToken();
    }
    
    @Transactional
    public String rotateToken(RefreshTokenRequest request) {
        return findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Revoke current token
                    refreshTokenRepository.revokeAllUserTokens(user.getId());
                    
                    // Create new token
                    RefreshToken newToken = createRefreshToken(user.getId());
                    
                    return newToken.getToken();
                })
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));
    }
    
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }
    
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was revoked");
        }
        
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired");
        }
        
        return token;
    }
} 
