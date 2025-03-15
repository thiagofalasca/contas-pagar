package com.totvs.contas_pagar.domain.entities;

import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import jakarta.persistence.*;
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
@Entity
@Table(name = "contas")
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate dataPagamento;

    private LocalDate dataVencimento;

    private BigDecimal valor;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoSituacaoConta situacao;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}
