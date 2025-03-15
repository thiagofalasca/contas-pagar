package com.totvs.contas_pagar.services.impl;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import com.totvs.contas_pagar.exceptions.ContaNotFoundException;
import com.totvs.contas_pagar.exceptions.CsvProcessException;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.repositories.ContaRepository;
import com.totvs.contas_pagar.services.ContaService;
import com.totvs.contas_pagar.services.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;

    private final UsuarioService usuarioService;


    public ContaServiceImpl(ContaRepository contaRepository, UsuarioService usuarioService) {
        this.contaRepository = contaRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public ContaEntity save(ContaEntity contaEntity) {
        if (contaEntity.getUsuario() != null && contaEntity.getUsuario().getId() != null) {
            UsuarioEntity usuario = obterUsuarioAssociado(contaEntity.getUsuario().getId());
            contaEntity.setUsuario(usuario);
        }
        return contaRepository.save(contaEntity);
    }

    @Override
    public Page<ContaEntity> findAll(Map<String, String> filters, Pageable pageable) {
        Specification<ContaEntity> spec = Specification.where(null);

        if (filters.containsKey("situacao")) {
            TipoSituacaoConta situacao = TipoSituacaoConta.valueOf(filters.get("situacao"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("situacao"), situacao));
        }
        if (filters.containsKey("dataPagamento")) {
            LocalDate dataPagamento = LocalDate.parse(filters.get("dataPagamento"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("dataPagamento"), dataPagamento));
        }
        if (filters.containsKey("dataVencimento")) {
            LocalDate dataVencimento = LocalDate.parse(filters.get("dataVencimento"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("dataVencimento"), dataVencimento));
        }
        if (filters.containsKey("valor")) {
            BigDecimal valor = new BigDecimal(filters.get("valor"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("valor"), valor));
        }
        if (filters.containsKey("descricao")) {
            String descricao = filters.get("descricao");
            spec = spec.and((root, query, cb) -> cb.like(root.get("descricao"), "%" + descricao + "%"));
        }
        if (filters.containsKey("usuarioId")) {
            UUID usuarioId = UUID.fromString(filters.get("usuarioId"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("id"), usuarioId));
        }
        return contaRepository.findAll(spec, pageable);
    }


    @Override
    public Optional<ContaEntity> findOne(UUID id) {
        return contaRepository.findById(id);
    }

    @Override
    public boolean isExists(UUID id) {
        return contaRepository.existsById(id);
    }

    @Override
    public ContaEntity fullUpdate(UUID id, ContaEntity contaEntity) {
        contaEntity.setId(id);
        return this.save(contaEntity);
    }

    @Override
    public ContaEntity partialUpdate(UUID id, ContaEntity contaEntity) {
        contaEntity.setId(id);
        return contaRepository.findById(id).map(existingConta -> {
            Optional.ofNullable(contaEntity.getDataPagamento()).ifPresent(existingConta::setDataPagamento);
            Optional.ofNullable(contaEntity.getDataVencimento()).ifPresent(existingConta::setDataVencimento);
            Optional.ofNullable(contaEntity.getValor()).ifPresent(existingConta::setValor);
            Optional.ofNullable(contaEntity.getDescricao()).ifPresent(existingConta::setDescricao);
            Optional.ofNullable(contaEntity.getSituacao()).ifPresent(existingConta::setSituacao);
            return this.save(existingConta);
        }).orElseThrow(() -> new ContaNotFoundException("Conta com o id " + id + " não foi encontrada."));
    }

    @Override
    public void delete(UUID id) {
        contaRepository.deleteById(id);
    }

    @Override
    public ContaEntity updateSituacao(UUID id, TipoSituacaoConta situacao) {
        return contaRepository.findById(id).map(existingConta -> {
            existingConta.setSituacao(situacao);
            return this.save(existingConta);
        }).orElseThrow(() -> new ContaNotFoundException("Conta com o id " + id + " não foi encontrada."));
    }

    @Override
    public BigDecimal getTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim, UUID usuarioId) {
        Specification<ContaEntity> spec = (root, query, cb) ->
                cb.between(root.get("dataPagamento"), dataInicio, dataFim);

        if (usuarioId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("id"), usuarioId));
        }

        List<ContaEntity> contas = contaRepository.findAll(spec);
        return contas.stream()
                .map(ContaEntity::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<ContaEntity> importCsv(MultipartFile file, UUID targetUserId) {
        UsuarioEntity targetUser = usuarioService.findOne(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("Usuário para associação não encontrado."));
        List<ContaEntity> contasImportadas = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] firstLine = reader.readNext();
            boolean hasHeader = isHeader(firstLine);
            if (!hasHeader) {
                processCsvLine(firstLine, targetUser, contasImportadas);
            }
            String[] linha;
            while ((linha = reader.readNext()) != null) {
                processCsvLine(linha, targetUser, contasImportadas);
            }
        } catch (Exception ex) {
            throw new CsvProcessException("Erro ao processar arquivo CSV: " + ex.getMessage(), ex);
        }
        return contasImportadas;
    }

    private boolean isHeader(String[] line) {
        List<String> expectedHeaders = List.of("dataPagamento", "dataVencimento", "valor", "descricao", "situacao");
        for (String columnName : line) {
            if (!expectedHeaders.contains(columnName.trim())) {
                return false;
            }
        }
        return true;
    }

    private void processCsvLine(String[] line, UsuarioEntity targetUser, List<ContaEntity> contasImportadas) {
        try {
            ContaEntity conta = new ContaEntity();
            conta.setDataPagamento(LocalDate.parse(line[0].trim()));
            conta.setDataVencimento(LocalDate.parse(line[1].trim()));
            conta.setValor(new BigDecimal(line[2].trim()));
            conta.setDescricao(line[3].trim());
            conta.setSituacao(TipoSituacaoConta.valueOf(line[4].trim().toUpperCase()));
            conta.setUsuario(targetUser);
            ContaEntity contaSalva = contaRepository.save(conta);
            contasImportadas.add(contaSalva);
        } catch (Exception e) {
            throw new CsvProcessException("Erro ao processar a linha: " + Arrays.toString(line) + " - " + e.getMessage(), e);
        }
    }

    private UsuarioEntity obterUsuarioAssociado(UUID usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioService.findOne(usuarioId)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + usuarioId + " não foi encontrado."));
    }
}
