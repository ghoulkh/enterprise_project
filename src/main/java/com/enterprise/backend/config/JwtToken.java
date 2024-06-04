package com.enterprise.backend.config;

import com.enterprise.backend.auth.AuthoritiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtToken implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 720 * 60 * 60; // 720 hours

    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${security.jwt.token.secret-key}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(",", roles));
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public UserDetails validateToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            if (Boolean.TRUE.equals(isTokenExpired(token))) {
                return null;
            }
            return new org.springframework.security.core.userdetails.User(
                    claims.getSubject(),
                    "",
                    getRolesFromClaims(claims)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private List<SimpleGrantedAuthority> getRolesFromClaims(Claims claims) {
        String rolesString = claims.get("roles", String.class);
        if (rolesString == null || rolesString.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.ROLE_USER));
        }
        String[] roles = rolesString.split(",");
        return Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
