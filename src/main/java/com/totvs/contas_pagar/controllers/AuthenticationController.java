package com.totvs.contas_pagar.controllers;

import com.totvs.contas_pagar.domain.dtos.AuthenticationDTO;
import com.totvs.contas_pagar.domain.dtos.UsuarioDTO;
import com.totvs.contas_pagar.domain.dtos.responses.TokenResponse;
import com.totvs.contas_pagar.domain.dtos.validation.OnCreate;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.mappers.Mapper;
import com.totvs.contas_pagar.services.UsuarioService;
import com.totvs.contas_pagar.services.impl.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UsuarioService usuarioService;

    private final Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper;

    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, UsuarioService usuarioService, Mapper<UsuarioEntity, UsuarioDTO> usuarioMapper, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UsuarioEntity) auth.getPrincipal());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> register(@RequestBody @Validated(OnCreate.class) UsuarioDTO usuarioDTO) {
        if (this.usuarioService.findByEmail(usuarioDTO.getEmail()) != null)
            throw new UserNotFoundException();

        UsuarioEntity usuarioEntity = usuarioMapper.mapFrom(usuarioDTO);
        UsuarioEntity newUsuarioEntity = usuarioService.save(usuarioEntity);
        UsuarioDTO newUsuarioDTO = usuarioMapper.mapTo(newUsuarioEntity);
        return new ResponseEntity<>(newUsuarioDTO, HttpStatus.CREATED);
    }
}
