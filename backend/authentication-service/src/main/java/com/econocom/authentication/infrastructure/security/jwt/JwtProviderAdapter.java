package com.econocom.authentication.infrastructure.security.jwt;

import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.JwtProviderPort;
import com.econocom.authentication.domain.model.JwtPayload;
import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProviderAdapter implements JwtProviderPort {

    private final SecretKey secretKey;

    private final SecurityProperties securityProperties;

    public JwtProviderAdapter(SecurityProperties securityProperties) {

        this.securityProperties = securityProperties;

        this.secretKey = Keys.hmacShaKeyFor(
                securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateAccessToken(User user) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + securityProperties.getJwt().getExpiration()
        );

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public JwtPayload parseToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return JwtPayload.builder()
                .email(claims.getSubject())
                .role(Role.valueOf(claims.get("role", String.class)))
                .issuedAt(claims.getIssuedAt().toInstant())
                .expiresAt(claims.getExpiration().toInstant())
                .build();

    }

}
