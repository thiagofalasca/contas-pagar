package com.totvs.contas_pagar.services;

import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ContaService {
    ContaEntity save(ContaEntity contaEntity);

    Page<ContaEntity> findAll(Map<String, String> filters, Pageable pageable);

    Optional<ContaEntity> findOne(UUID id);

    boolean isExists(UUID id);

    ContaEntity fullUpdate(UUID id, ContaEntity contaEntity);

    ContaEntity partialUpdate(UUID id, ContaEntity contaEntity);

    void delete(UUID id);

    ContaEntity updateSituacao(UUID id, TipoSituacaoConta situacao);

    BigDecimal getTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim, UUID usuarioId);

    List<ContaEntity> importCsv(MultipartFile file, UUID targetUserId);
}
