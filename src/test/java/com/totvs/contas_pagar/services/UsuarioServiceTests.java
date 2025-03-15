package com.totvs.contas_pagar.services;

import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.domain.enums.TipoCargoUsuario;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.repositories.UsuarioRepository;
import com.totvs.contas_pagar.services.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioEntity usuarioEntity;

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
    }

    @Test
    void testSaveUsuario() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(usuarioEntity.getSenha());

        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(invocation -> {
            UsuarioEntity savedUser = invocation.getArgument(0);
            savedUser.setSenha(hashedPassword); // Simula o comportamento
            return savedUser;
        });

        UsuarioEntity result = usuarioService.save(usuarioEntity);

        verify(usuarioRepository).save(usuarioEntity);
        assertThat(result).isNotNull();
        assertThat(passwordEncoder.matches("senha123", result.getSenha())).isTrue();
    }

    @Test
    void testFindAllUsuarios() {
        Page<UsuarioEntity> page = new PageImpl<>(List.of(usuarioEntity));
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        Page<UsuarioEntity> result = usuarioService.findAll(pageable);

        verify(usuarioRepository).findAll(pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(usuarioEntity);
    }

    @Test
    void testFindOneUsuario() {
        UUID id = usuarioEntity.getId();
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioEntity));

        Optional<UsuarioEntity> result = usuarioService.findOne(id);

        verify(usuarioRepository).findById(id);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(usuarioEntity);
    }

    @Test
    void testFindOneUsuarioNotFound() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UsuarioEntity> result = usuarioService.findOne(id);

        verify(usuarioRepository).findById(id);
        assertThat(result).isNotPresent();
    }

    @Test
    void testFullUpdateUsuario() {
        UUID id = usuarioEntity.getId();
        UsuarioEntity updatedUsuario = UsuarioEntity.builder()
                .nome("Updated User")
                .email("updated@example.com")
                .senha("novaSenha123")
                .build();

        when(usuarioRepository.existsById(id)).thenReturn(true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.findByEmail(updatedUsuario.getEmail())).thenReturn(null);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(updatedUsuario);

        UsuarioEntity result = usuarioService.fullUpdate(id, updatedUsuario);

        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository).save(updatedUsuario);
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getSenha()).isNotNull();
    }

    @Test
    void testFullUpdateUsuarioNotFound() {
        UUID id = UUID.randomUUID();
        UsuarioEntity updatedUsuario = UsuarioEntity.builder().build();

        when(usuarioRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.fullUpdate(id, updatedUsuario))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("não foi encontrado");
    }

    @Test
    void testDeleteUsuario() {
        UUID id = usuarioEntity.getId();
        when(usuarioRepository.existsById(id)).thenReturn(true);

        usuarioService.delete(id);

        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void testDeleteUsuarioNotFound() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.delete(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("não foi encontrado");
    }

    @Test
    void testFindUsuarioByEmail() {
        String email = usuarioEntity.getEmail();
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioEntity);

        UserDetails result = usuarioService.findByEmail(email);

        verify(usuarioRepository).findByEmail(email);
        assertThat(result).isEqualTo(usuarioEntity);
    }

    @Test
    void testFindUsuarioByEmailNotFound() {
        String email = "notfound@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(null);

        assertThatThrownBy(() -> usuarioService.findByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("não foi encontrado");
    }
}

