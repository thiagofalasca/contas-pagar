package com.totvs.contas_pagar.repositories;

import com.totvs.contas_pagar.domain.entities.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, UUID>,
        JpaSpecificationExecutor<ContaEntity> {
}
