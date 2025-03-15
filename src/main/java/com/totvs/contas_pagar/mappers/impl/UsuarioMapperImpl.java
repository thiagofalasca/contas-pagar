package com.totvs.contas_pagar.mappers.impl;

import com.totvs.contas_pagar.domain.dtos.UsuarioDTO;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import com.totvs.contas_pagar.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapperImpl implements Mapper<UsuarioEntity, UsuarioDTO> {

    private final ModelMapper modelMapper;

    public UsuarioMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UsuarioDTO mapTo(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
    }

    @Override
    public UsuarioEntity mapFrom(UsuarioDTO usuarioDTO) {
        return modelMapper.map(usuarioDTO, UsuarioEntity.class);
    }
}
