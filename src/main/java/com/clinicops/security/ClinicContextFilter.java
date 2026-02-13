package com.clinicops.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class ClinicContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {

        if (request.getRequestURI().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clinicId = request.getHeader("X-Clinic-Id");
        if (clinicId == null || clinicId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Clinic context required");
            return;
        }

        request.setAttribute("CLINIC_ID", clinicId);
        filterChain.doFilter(request, response);
    }
}
