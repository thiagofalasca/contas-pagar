package com.totvs.contas_pagar.controllers;

import com.totvs.contas_pagar.domain.dtos.UsuarioDTO;
import com.totvs.contas_pagar.domain.dtos.validation.OnCreate;
import com.totvs.contas_pagar.domain.dtos.validation.OnUpdate;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.mappers.Mapper;
import com.totvs.contas_pagar.services.UsuarioService;
import com.totvs.contas_pagar.services.impl.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper;

    private final SecurityService securityService;

    public UsuarioController(UsuarioService usuarioService, Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper, SecurityService securityService) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.securityService = securityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioDTO>> listUsuarios(Pageable pageable) {
        Page<UsuarioEntity> usuarios = usuarioService.findAll(pageable);
        return new ResponseEntity<>(usuarios.map(usuarioMapper::mapTo), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.id.toString()")
    public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable("id") UUID id) {
        UsuarioEntity usuario = usuarioService.findOne(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado."));
        return ResponseEntity.ok(usuarioMapper.mapTo(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.id.toString()")
    public ResponseEntity<UsuarioDTO> fullUpdateUsuario(@PathVariable UUID id, @RequestBody @Validated(OnCreate.class) UsuarioDTO usuarioDTO) {
        UsuarioEntity existingUser = usuarioService.findOne(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado."));

        boolean isAdmin = securityService.isAdmin();
        if (!isAdmin) {
            usuarioDTO.setCargo(existingUser.getCargo());
        } else {
            if (usuarioDTO.getCargo() == null) {
                usuarioDTO.setCargo(existingUser.getCargo());
            }
        }
        UsuarioEntity usuarioEntityPayload = usuarioMapper.mapFrom(usuarioDTO);
        UsuarioEntity updatedUser = usuarioService.fullUpdate(id, usuarioEntityPayload);
        return ResponseEntity.ok(usuarioMapper.mapTo(updatedUser));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.id.toString()")
    public ResponseEntity<UsuarioDTO> partialUpdate(@PathVariable UUID id, @RequestBody @Validated(OnUpdate.class) UsuarioDTO usuarioDTO) {
        UsuarioEntity existingUser = usuarioService.findOne(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado."));

        boolean isAdmin = securityService.isAdmin();
        if (!isAdmin) {
            usuarioDTO.setCargo(existingUser.getCargo());
        } else {
            if (usuarioDTO.getCargo() == null) {
                usuarioDTO.setCargo(existingUser.getCargo());
            }
        }
        UsuarioEntity usuarioEntityPayload = usuarioMapper.mapFrom(usuarioDTO);
        UsuarioEntity updatedUser = usuarioService.partialUpdate(id, usuarioEntityPayload);
        return ResponseEntity.ok(usuarioMapper.mapTo(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.id.toString()")
    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        if (!usuarioService.isExists(id)) {
            throw new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado.");
        }
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
