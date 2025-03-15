package com.totvs.contas_pagar.services;

import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioService {
    UsuarioEntity save(UsuarioEntity usuarioEntity);

    Page<UsuarioEntity> findAll(Pageable pageable);

    Optional<UsuarioEntity> findOne(UUID id);

    boolean isExists(UUID id);

    UsuarioEntity fullUpdate(UUID id, UsuarioEntity usuarioEntity);

    UsuarioEntity partialUpdate(UUID id, UsuarioEntity usuarioEntity);

    void delete(UUID id);

    UserDetails findByEmail(String email);
}
