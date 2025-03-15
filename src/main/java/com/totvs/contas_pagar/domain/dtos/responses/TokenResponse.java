package com.totvs.contas_pagar.domain.dtos.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {
    private final String token;
}
