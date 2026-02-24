package com.clinicops.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-expiration}")
    private long accessExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(
            ObjectId userId,
            ObjectId organizationId,
            ObjectId clinicId,
            String role) {

        return Jwts.builder()
                .setSubject(userId.toHexString())
                .claim("org", organizationId != null ? organizationId.toHexString() : null)
                .claim("clinic", clinicId != null ? clinicId.toHexString() : null)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(ObjectId userId) {

        return Jwts.builder()
                .setSubject(userId.toHexString())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String expectedType) {
        Claims claims = extractAllClaims(token);
        return expectedType.equals(claims.get("type"));
    }

	public Claims extractClaims(String token) {
		// TODO Auto-generated method stub
		return null;
	}
}
