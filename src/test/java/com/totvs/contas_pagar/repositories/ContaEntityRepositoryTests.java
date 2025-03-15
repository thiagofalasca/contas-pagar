package com.totvs.contas_pagar.repositories;

import com.totvs.contas_pagar.TestDataUtil;
import com.totvs.contas_pagar.domain.entities.ContaEntity;
import com.totvs.contas_pagar.domain.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ContaEntityRepositoryTests {

    private ContaRepository underTest;

    @Autowired
    public ContaEntityRepositoryTests(ContaRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatContaCanBeCreatedAndRecalled() {
        UsuarioEntity usuarioEntity = TestDataUtil.createTestUsuarioEntityA();
        ContaEntity contaEntity = TestDataUtil.createTestContaEntityA(usuarioEntity);
        contaEntity.setValor(contaEntity.getValor().setScale(2));
        underTest.save(contaEntity);
        Optional<ContaEntity> result = underTest.findById(contaEntity.getId());
        result.ifPresent(r -> r.setValor(r.getValor().setScale(2)));
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(contaEntity);
    }

    @Test
    public void testThatContaCanBeUpdated() {
        UsuarioEntity usuarioEntity = TestDataUtil.createTestUsuarioEntityA();

        ContaEntity contaEntityA = TestDataUtil.createTestContaEntityA(usuarioEntity);
        contaEntityA.setValor(contaEntityA.getValor().setScale(2));
        underTest.save(contaEntityA);

        contaEntityA.setDescricao("UPDATED");
        underTest.save(contaEntityA);

        Optional<ContaEntity> result = underTest.findById(contaEntityA.getId());
        result.ifPresent(r -> r.setValor(r.getValor().setScale(2)));
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(contaEntityA);
    }

    @Test
    public void testThatContaCanBeDeleted() {
        UsuarioEntity usuarioEntity = TestDataUtil.createTestUsuarioEntityA();

        ContaEntity contaEntityA = TestDataUtil.createTestContaEntityA(usuarioEntity);
        underTest.save(contaEntityA);

        underTest.deleteById(contaEntityA.getId());

        Optional<ContaEntity> result = underTest.findById(contaEntityA.getId());
        assertThat(result).isEmpty();
    }
}
