package com.clinicops.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ClinicContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {
    	
    	System.out.println("ClinicContextFilter triggered");

        if (request.getRequestURI().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String clinicId = request.getHeader("X-Clinic-Id");
        if (clinicId != null && !clinicId.isBlank()) {
            request.setAttribute("CLINIC_ID", clinicId);
        }

        // Allow /me to pass without clinic context
        if ("/me".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (clinicId == null || clinicId.isBlank()) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Clinic context required");
        	return;
        }

        filterChain.doFilter(request, response);
    }
}
