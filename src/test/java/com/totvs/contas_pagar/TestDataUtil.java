package com.totvs.contas_pagar;

import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.domain.enums.TipoCargoUsuario;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static UsuarioEntity createTestUsuarioEntityA() {
        return UsuarioEntity.builder()
                .nome("Usuario 1")
                .email("usuario1@email.com")
                .senha("senha123")
                .cargo(TipoCargoUsuario.USUARIO)
                .build();
    }

    public static ContaEntity createTestContaEntityA(final UsuarioEntity usuarioEntity) {
        return ContaEntity.builder()
                .dataPagamento(LocalDate.parse("2024-10-10"))
                .dataVencimento(LocalDate.parse("2024-10-15"))
                .valor(BigDecimal.valueOf(100))
                .descricao("Conta de luz")
                .situacao(TipoSituacaoConta.valueOf("PAGA"))
                .usuario(usuarioEntity)
                .build();
    }
}
