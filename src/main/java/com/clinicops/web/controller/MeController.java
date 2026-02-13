package com.clinicops.web.controller;

import com.clinicops.domain.access.service.PermissionEvaluator;
import com.clinicops.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        AuthenticatedUser user =
                (AuthenticatedUser) request.getAttribute("AUTH_USER");
        String clinicId =
                (String) request.getAttribute("CLINIC_ID");

        return Map.of(
                "userId", user.getUserId(),
                "clinicId", clinicId
        );
    }
}
