package com.totvs.contas_pagar.domain.dtos;

import com.totvs.contas_pagar.domain.dtos.validation.OnCreate;
import com.totvs.contas_pagar.domain.dtos.validation.OnUpdate;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaDTO {

    private UUID id;

    private LocalDate dataPagamento;

    @NotNull(groups = OnCreate.class, message = "A data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    @NotNull(groups = OnCreate.class, message = "O valor é obrigatório")
    @DecimalMin(groups = {OnCreate.class, OnUpdate.class}, value = "0.01", inclusive = true, message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(groups = OnCreate.class, message = "A situação da conta é obrigatória")
    private TipoSituacaoConta situacao;

    private UsuarioDTO usuario;
}
