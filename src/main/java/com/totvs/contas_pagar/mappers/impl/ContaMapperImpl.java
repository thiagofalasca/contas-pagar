package com.totvs.contas_pagar.mappers.impl;

import com.totvs.contas_pagar.domain.dtos.ContaDTO;
import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ContaMapperImpl implements Mapper<ContaEntity, ContaDTO> {

    private final ModelMapper modelMapper;

    public ContaMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ContaDTO mapTo(ContaEntity contaEntity) {
        return modelMapper.map(contaEntity, ContaDTO.class);
    }

    @Override
    public ContaEntity mapFrom(ContaDTO contaDTO) {
        return modelMapper.map(contaDTO, ContaEntity.class);
    }
}
