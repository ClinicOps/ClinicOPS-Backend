package com.clinicops.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.clinicops.modules.auth.model.User;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;
import com.clinicops.security.rbac.RolePermissionMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties properties;
    private final RolePermissionMap rolePermissionMap;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public AuthUser parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String workspaceId = claims.get("workspaceId", String.class);
            @SuppressWarnings("unchecked")
            Set<String> roles =
            (Set<String>) claims.get("roles", List.class)
                  .stream()
                  .map(Object::toString)
                  .collect(Collectors.toSet());
            
            Set<Permission> permissions =
                    rolePermissionMap.getPermissions(roles);
            

            return new AuthUser(userId, workspaceId,roles, permissions);

        } catch (JwtException ex) {
            return null;
        }
    }
    
    public String createAccessToken(User user) {

        return Jwts.builder()
            .setSubject(user.getId())
            .claim("workspaceId", user.getWorkspaceId())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(
                Date.from(
                    Instant.now().plusSeconds(
                        properties.getAccessTokenTtlSeconds()
                    )
                )
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}

