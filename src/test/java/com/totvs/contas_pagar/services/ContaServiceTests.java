package com.totvs.contas_pagar.services;


import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.domain.enums.TipoCargoUsuario;
import com.totvs.contas_pagar.domain.enums.TipoSituacaoConta;
import com.totvs.contas_pagar.repositories.ContaRepository;
import com.totvs.contas_pagar.services.impl.ContaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class ContaServiceImplTest {

    @InjectMocks
    private ContaServiceImpl contaService;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioService usuarioService;

    private UsuarioEntity usuarioEntity;
    private ContaEntity contaEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("Test User")
                .email("test@example.com")
                .senha("senha123")
                .cargo(TipoCargoUsuario.valueOf("USUARIO"))
                .build();

        contaEntity = ContaEntity.builder()
                .id(UUID.randomUUID())
                .dataPagamento(LocalDate.now())
                .dataVencimento(LocalDate.now().plusDays(10))
                .valor(BigDecimal.valueOf(200.00))
                .descricao("Conta de Teste")
                .situacao(TipoSituacaoConta.PENDENTE)
                .usuario(usuarioEntity)
                .build();
    }

    @Test
    void testSaveContaWithUsuario() {
        when(usuarioService.findOne(usuarioEntity.getId())).thenReturn(Optional.of(usuarioEntity));
        when(contaRepository.save(any(ContaEntity.class))).thenReturn(contaEntity);

        ContaEntity result = contaService.save(contaEntity);

        verify(usuarioService).findOne(usuarioEntity.getId());
        verify(contaRepository).save(contaEntity);

        assertThat(result).isNotNull();
        assertThat(result.getUsuario()).isEqualTo(usuarioEntity);
    }

    @Test
    void testSaveContaWithoutUsuario() {
        contaEntity.setUsuario(null);
        when(contaRepository.save(any(ContaEntity.class))).thenReturn(contaEntity);

        ContaEntity result = contaService.save(contaEntity);

        verify(contaRepository).save(contaEntity);
        verifyNoInteractions(usuarioService);

        assertThat(result).isNotNull();
        assertThat(result.getUsuario()).isNull();
    }

    @Test
    void testFindAllWithFilters() {
        Map<String, String> filters = new HashMap<>();
        filters.put("situacao", "PENDENTE");
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContaEntity> page = new PageImpl<>(List.of(contaEntity));

        when(contaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<ContaEntity> result = contaService.findAll(filters, pageable);

        verify(contaRepository).findAll(any(Specification.class), eq(pageable));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(contaEntity);
    }

    @Test
    void testFindOneConta() {
        UUID contaId = contaEntity.getId();
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(contaEntity));

        Optional<ContaEntity> result = contaService.findOne(contaId);

        verify(contaRepository).findById(contaId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(contaEntity);
    }

    @Test
    void testFindOneContaNotFound() {
        UUID contaId = UUID.randomUUID();
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        Optional<ContaEntity> result = contaService.findOne(contaId);

        verify(contaRepository).findById(contaId);
        assertThat(result).isNotPresent();
    }

    @Test
    void testDeleteConta() {
        UUID contaId = contaEntity.getId();

        contaService.delete(contaId);

        verify(contaRepository).deleteById(contaId);
    }

    @Test
    void testGetTotalPagoPorPeriodo() {
        LocalDate inicio = LocalDate.of(2023, 1, 1);
        LocalDate fim = LocalDate.of(2023, 12, 31);
        List<ContaEntity> contas = List.of(
                ContaEntity.builder().valor(BigDecimal.valueOf(100)).build(),
                ContaEntity.builder().valor(BigDecimal.valueOf(200)).build()
        );

        when(contaRepository.findAll(any(Specification.class))).thenReturn(contas);

        BigDecimal result = contaService.getTotalPagoPorPeriodo(inicio, fim, null);

        verify(contaRepository).findAll(any(Specification.class));
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(300));
    }
}