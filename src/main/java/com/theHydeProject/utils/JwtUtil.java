package com.theHydeProject.utils;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.theHydeProject.components.DotenvConfig;
import com.theHydeProject.models.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    private DotenvConfig env = new DotenvConfig();

    private final String secret_key_base64 = env.get("JWT_SECRET");
    byte[] keyBytes = Decoders.BASE64.decode(secret_key_base64);
    Key secret_key = Keys.hmacShaKeyFor(keyBytes);
    private long accessTokenValidity = 60 * 60 * 1000;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public String createToken(Users user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("id", user.getId());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));

        System.out.println("reached just before token creation");
        try {
            String jwt = Jwts.builder().setClaims(claims).setExpiration(tokenValidity)
                    .signWith(secret_key).compact();
            System.out.println("json web token: " + jwt);
            return jwt;
        } catch (Exception e) {
            System.out.println("Error while creating token");
            e.printStackTrace();
        }

        throw new RuntimeException("Error while creating token");
    }

    private Claims parseJwtClaims(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secret_key).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Long getUserId(HttpServletRequest request) {
        Claims claims = resolveClaims(request);
        return claims.get("id", Long.class);
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

}