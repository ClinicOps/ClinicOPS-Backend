package com.clinicops.modules.auth.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.security.model.AuthUser;

@RestController
@RequestMapping("/v1/auth-test")
public class AuthTestController {

    @GetMapping("/me")
    public ApiResponse<AuthUser> me() {
        AuthUser authUser =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(authUser);
    }
}

