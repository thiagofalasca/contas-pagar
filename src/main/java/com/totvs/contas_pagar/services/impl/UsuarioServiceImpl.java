package com.totvs.contas_pagar.services.impl;

import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.exceptions.UserAlreadyExistsException;
import com.totvs.contas_pagar.exceptions.UserNotFoundException;
import com.totvs.contas_pagar.repositories.UsuarioRepository;
import com.totvs.contas_pagar.services.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioEntity save(UsuarioEntity usuarioEntity) {
        if (usuarioEntity.getSenha() != null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(usuarioEntity.getSenha());
            usuarioEntity.setSenha(encryptedPassword);
        }
        return usuarioRepository.save(usuarioEntity);
    }

    @Override
    public Page<UsuarioEntity> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Optional<UsuarioEntity> findOne(UUID id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public boolean isExists(UUID id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public UsuarioEntity fullUpdate(UUID id, UsuarioEntity usuarioEntity) {
        if (!usuarioRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado para atualização.");
        }
        usuarioEntity.setId(id);
        UsuarioEntity existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado."));

        if (usuarioEntity.getSenha() == null) {
            usuarioEntity.setSenha(existingUser.getSenha());
        }

        if (usuarioEntity.getEmail() != null &&
                !usuarioEntity.getEmail().equals(existingUser.getEmail())) {
            UsuarioEntity userWithEmail = (UsuarioEntity) usuarioRepository.findByEmail(usuarioEntity.getEmail());
            if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
                throw new UserAlreadyExistsException("Já existe um usuário com o e-mail fornecido: " + usuarioEntity.getEmail());
            }
        }
        return save(usuarioEntity);
    }

    @Override
    public UsuarioEntity partialUpdate(UUID id, UsuarioEntity usuarioEntity) {
        return usuarioRepository.findById(id).map(existingUser -> {
            if (usuarioEntity.getNome() != null) {
                existingUser.setNome(usuarioEntity.getNome());
            }
            if (usuarioEntity.getEmail() != null) {
                if (!usuarioEntity.getEmail().equals(existingUser.getEmail())) {
                    UsuarioEntity userWithEmail = (UsuarioEntity) usuarioRepository.findByEmail(usuarioEntity.getEmail());
                    if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
                        throw new UserAlreadyExistsException("Já existe um usuário com o e-mail fornecido: " + usuarioEntity.getEmail());
                    }
                    existingUser.setEmail(usuarioEntity.getEmail());
                }
            }
            if (usuarioEntity.getSenha() != null) {
                existingUser.setSenha(usuarioEntity.getSenha());
            }
            if (usuarioEntity.getCargo() != null) {
                existingUser.setCargo(usuarioEntity.getCargo());
            }
            return save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado."));
    }

    @Override
    public void delete(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário com o ID " + id + " não foi encontrado para exclusão.");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UserDetails findByEmail(String email) {
        UserDetails user = usuarioRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Usuário com o e-mail " + email + " não foi encontrado.");
        }
        return user;
    }
}
