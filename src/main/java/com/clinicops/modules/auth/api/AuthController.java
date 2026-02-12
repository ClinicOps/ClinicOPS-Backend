package com.clinicops.modules.auth.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.auth.dto.LoginRequest;
import com.clinicops.modules.auth.dto.LoginResponse;
import com.clinicops.modules.auth.dto.RefreshRequest;
import com.clinicops.modules.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest request) {

        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @RequestBody RefreshRequest request) {

        return ApiResponse.ok(authService.refresh(request));
    }
}

