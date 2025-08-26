package com.dizzme.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dizzme.entity.Client;
import com.dizzme.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;

    public String generateToken(Client client) {
        return JWT.create()
                .withSubject(client.getEmail())
                .withClaim("id", client.getId())
                .withClaim("name", client.getName())
                .withClaim("role", client.getRole().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public DecodedJWT validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(token);
        } catch (Exception e) {
            throw new BusinessException("Token inválido");
        }
    }

    public String getEmailFromToken(String token) {
        return validateToken(token).getSubject();
    }
}