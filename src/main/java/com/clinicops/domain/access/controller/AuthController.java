package com.clinicops.domain.access.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.domain.access.model.AuthResponse;
import com.clinicops.domain.access.model.LoginRequest;
import com.clinicops.domain.access.model.RegisterRequest;
import com.clinicops.domain.user.service.IdentityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IdentityService identityService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = identityService.register(request);
        return ApiResponse.ok(response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ApiResponse.ok(identityService.login(request));
    }
}
