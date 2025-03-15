package com.totvs.contas_pagar.controllers;

import com.totvs.contas_pagar.domain.dtos.ContaDTO;
import com.totvs.contas_pagar.domain.dtos.UsuarioDTO;
import com.totvs.contas_pagar.domain.dtos.responses.TotalPagoResponse;
import com.totvs.contas_pagar.domain.dtos.validation.OnCreate;
import com.totvs.contas_pagar.domain.dtos.validation.OnUpdate;
import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import com.totvs.contas_pagar.exceptions.ContaNotFoundException;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.mappers.Mapper;
import com.totvs.contas_pagar.services.ContaService;
import com.totvs.contas_pagar.services.UsuarioService;
import com.totvs.contas_pagar.services.impl.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService contaService;

    private final Mapper<ContaEntity, ContaDTO> contaMapper;

    private final Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper;

    private final SecurityService securityService;

    private final UsuarioService usuarioService;

    public ContaController(ContaService contaService, Mapper<ContaEntity, ContaDTO> contaMapper, Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper, SecurityService securityService, UsuarioService usuarioService) {
        this.contaService = contaService;
        this.contaMapper = contaMapper;
        this.usuarioMapper = usuarioMapper;
        this.securityService = securityService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ContaDTO> createConta(@RequestBody @Validated(OnCreate.class) ContaDTO contaDto) {
        ContaEntity contaEntity = contaMapper.mapFrom(contaDto);
        if (contaDto.getUsuario() == null || contaDto.getUsuario().getId() == null) {
            UsuarioEntity usuario = usuarioService.findOne(securityService.getAuthenticatedUserId())
                    .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado."));
            contaEntity.setUsuario(usuario);
        }
        ContaEntity createdConta = contaService.save(contaEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(contaMapper.mapTo(createdConta));
    }

    @PostMapping("/import-csv")
    public ResponseEntity<List<ContaDTO>> importCsv(@RequestParam("file") MultipartFile file,
                                                    @RequestParam(value = "usuarioId", required = false) UUID usuarioId) {
        UUID targetUserId = securityService.isAdmin()
                ? (usuarioId != null ? usuarioId : securityService.getAuthenticatedUserId())
                : securityService.getAuthenticatedUserId();

        List<ContaEntity> contasImportadas = contaService.importCsv(file, targetUserId);
        List<ContaDTO> contasImportadasDTO = contasImportadas.stream()
                .map(contaMapper::mapTo)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(contasImportadasDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ContaDTO>> listContas(@RequestParam Map<String, String> filters, Pageable pageable) {
        if (!securityService.isAdmin()) {
            filters.put("usuarioId", securityService.getAuthenticatedUserId().toString());
        }
        Page<ContaEntity> contas = contaService.findAll(filters, pageable);
        return ResponseEntity.ok(contas.map(contaMapper::mapTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> getConta(@PathVariable("id") UUID id) {
        ContaEntity conta = contaService.findOne(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta com o ID " + id + " não foi encontrada."));
        if (!securityService.isAdmin() &&
                !conta.getUsuario().getId().equals(securityService.getAuthenticatedUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
        return ResponseEntity.ok(contaMapper.mapTo(conta));
    }

    @GetMapping("/total-pago")
    public ResponseEntity<TotalPagoResponse> getTotalPagoPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(required = false) UUID usuarioId) {
        if (!securityService.isAdmin()) {
            usuarioId = securityService.getAuthenticatedUserId();
        }
        BigDecimal totalPago = contaService.getTotalPagoPorPeriodo(dataInicio, dataFim, usuarioId);
        return ResponseEntity.ok(new TotalPagoResponse(totalPago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaDTO> fullUpdateConta(@PathVariable UUID id, @RequestBody @Validated(OnCreate.class) ContaDTO contaDTO) {
        ContaEntity contaExistente = contaService.findOne(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta com o ID " + id + " não foi encontrada."));

        if (!securityService.isAdmin() &&
                !contaExistente.getUsuario().getId().equals(securityService.getAuthenticatedUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
        if (securityService.isAdmin() && (contaDTO.getUsuario() == null || contaDTO.getUsuario().getId() == null)) {
            contaDTO.setUsuario(usuarioMapper.mapTo(contaExistente.getUsuario()));
        }
        ContaEntity contaEntity = contaMapper.mapFrom(contaDTO);
        ContaEntity contaAtualizada = contaService.fullUpdate(id, contaEntity);
        return ResponseEntity.ok(contaMapper.mapTo(contaAtualizada));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ContaDTO> partialUpdate(@PathVariable UUID id, @RequestBody @Validated(OnUpdate.class) ContaDTO contaDTO) {
        ContaEntity contaExistente = contaService.findOne(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta com o ID " + id + " não foi encontrada."));

        if (!securityService.isAdmin() &&
                !contaExistente.getUsuario().getId().equals(securityService.getAuthenticatedUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
        if (securityService.isAdmin() && (contaDTO.getUsuario() == null || contaDTO.getUsuario().getId() == null)) {
            contaDTO.setUsuario(usuarioMapper.mapTo(contaExistente.getUsuario()));
        }
        ContaEntity contaEntity = contaMapper.mapFrom(contaDTO);
        ContaEntity contaAtualizada = contaService.partialUpdate(id, contaEntity);
        return ResponseEntity.ok(contaMapper.mapTo(contaAtualizada));
    }


    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContaDTO> updateSituacaoConta(@PathVariable UUID id, @RequestBody Map<String, String> requestBody) {
        ContaEntity contaExistente = contaService.findOne(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta com o ID " + id + " não foi encontrada."));
        if (!securityService.isAdmin() &&
                !contaExistente.getUsuario().getId().equals(securityService.getAuthenticatedUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
        String situacao = requestBody.get("situacao");
        ContaEntity contaAtualizada = contaService.updateSituacao(id, TipoSituacaoConta.valueOf(situacao));
        return ResponseEntity.ok(contaMapper.mapTo(contaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable UUID id) {
        ContaEntity contaExistente = contaService.findOne(id)
                .orElseThrow(() -> new ContaNotFoundException("Conta com o ID " + id + " não foi encontrada."));
        if (!securityService.isAdmin() &&
                !contaExistente.getUsuario().getId().equals(securityService.getAuthenticatedUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado.");
        }
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
