package com.totvs.contas_pagar.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.exceptions.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UsuarioEntity usuarioEntity) {
        if (usuarioEntity == null || usuarioEntity.getId() == null) {
            throw new TokenException("Erro ao gerar token: Usuário inválido.");
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("contas-pagar-api")
                    .withSubject(usuarioEntity.getId().toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenException("Erro ao gerar token.", exception);
        }
    }

    public String validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new TokenException("Token fornecido está vazio ou nulo.");
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("contas-pagar-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new TokenException("Falha na validação do token.", exception);
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
