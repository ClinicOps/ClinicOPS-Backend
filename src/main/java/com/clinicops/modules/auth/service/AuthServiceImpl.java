package com.clinicops.modules.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.clinicops.common.exception.BusinessException;
import com.clinicops.modules.auth.dto.LoginRequest;
import com.clinicops.modules.auth.dto.LoginResponse;
import com.clinicops.modules.auth.dto.RefreshRequest;
import com.clinicops.modules.auth.model.RefreshToken;
import com.clinicops.modules.auth.model.User;
import com.clinicops.modules.auth.model.UserStatus;
import com.clinicops.modules.auth.repo.RefreshTokenRepository;
import com.clinicops.modules.auth.repo.UserRepository;
import com.clinicops.modules.workspace.model.Workspace;
import com.clinicops.modules.workspace.model.WorkspaceStatus;
import com.clinicops.modules.workspace.repo.WorkspaceRepository;
import com.clinicops.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
            .findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
            .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials");
        }

        Workspace workspace = workspaceRepository
            .findById(user.getWorkspaceId())
            .orElseThrow(() -> new BusinessException("Workspace not found"));

        if (workspace.getStatus() != WorkspaceStatus.ACTIVE) {
            throw new BusinessException("Workspace suspended");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = createRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse refresh(RefreshRequest request) {

        String hash = hash(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository
            .findByTokenHashAndRevokedFalse(hash)
            .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expired");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        User user = userRepository
            .findById(token.getUserId())
            .orElseThrow(() -> new BusinessException("User not found"));

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = createRefreshToken(user.getId());

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    // helpers below
    private String createRefreshToken(String userId) {

        String rawToken = UUID.randomUUID().toString();
        String hash = hash(rawToken);

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setTokenHash(hash);
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        token.setRevoked(false);

        refreshTokenRepository.save(token);
        return rawToken;
    }

    private String hash(String value) {
        return DigestUtils.sha256Hex(value);
    }
}

