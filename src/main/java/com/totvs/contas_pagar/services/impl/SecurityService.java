package com.totvs.contas_pagar.services.impl;

import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {

    public UUID getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuario = (UsuarioEntity) auth.getPrincipal();
        return usuario.getId();
    }

    public boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuth -> grantedAuth.getAuthority().equals("ROLE_ADMIN"));
    }
}

