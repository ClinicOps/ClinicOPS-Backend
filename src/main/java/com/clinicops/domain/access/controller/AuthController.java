package com.clinicops.domain.access.controller;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.common.exception.AuthorizationException;
import com.clinicops.domain.access.model.AuthResponse;
import com.clinicops.domain.access.model.LoginRequest;
import com.clinicops.domain.access.model.RefreshRequest;
import com.clinicops.domain.access.model.RefreshToken;
import com.clinicops.domain.access.model.RegisterRequest;
import com.clinicops.domain.access.model.UserDTO;
import com.clinicops.domain.clinic.model.Clinic;
import com.clinicops.domain.clinic.model.ClinicMember;
import com.clinicops.domain.clinic.model.ClinicStatus;
import com.clinicops.domain.clinic.model.MembershipStatus;
import com.clinicops.domain.organization.model.Organization;
import com.clinicops.domain.organization.model.OrganizationStatus;
import com.clinicops.domain.user.model.User;
import com.clinicops.domain.user.service.IdentityService;

import io.jsonwebtoken.Claims;
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
    
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) throws AuthorizationException {        

        return identityService.refreshToken(request);
        
    }
}
