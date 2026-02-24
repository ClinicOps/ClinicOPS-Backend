package com.clinicops.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public AuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
                Claims claims = jwtService.extractClaims(token);

                if (!"access".equals(claims.get("type"))) {
                    throw new RuntimeException("Invalid token type");
                }

                ObjectId userId = new ObjectId(claims.getSubject());

                ObjectId orgId = claims.get("org") != null
                        ? new ObjectId(claims.get("org").toString())
                        : null;

                ObjectId clinicId = claims.get("clinic") != null
                        ? new ObjectId(claims.get("clinic").toString())
                        : null;

                String role = claims.get("role") != null
                        ? claims.get("role").toString()
                        : null;

                AuthenticatedUser principal =
                        new AuthenticatedUser(userId, orgId, clinicId, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}