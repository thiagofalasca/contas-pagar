package com.totvs.contas_pagar.domain.dtos.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class TotalPagoResponse {
    private final BigDecimal totalPago;
}
