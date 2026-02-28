package com.clinicops.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ClinicContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {

        // Skip clinic context check for auth endpoints
        if (request.getRequestURI().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Allow /me endpoint without clinic context requirement
        if ("/me".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract clinicId from JWT via SecurityContext (already set by AuthFilter)
        String clinicId = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
            if (user.getClinicId() != null) {
                clinicId = user.getClinicId().toString();
            }
        }

        // If no clinic context found, return error
        if (clinicId == null || clinicId.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "Clinic context missing. Ensure valid JWT token with clinic claim is provided");
            return;
        }

        // Store clinic context in request for downstream processing
        request.setAttribute("CLINIC_ID", clinicId);

        filterChain.doFilter(request, response);
    }
}
