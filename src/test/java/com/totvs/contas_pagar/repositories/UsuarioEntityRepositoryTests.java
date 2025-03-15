package com.totvs.contas_pagar.repositories;

import com.totvs.contas_pagar.TestDataUtil;
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
public class UsuarioEntityRepositoryTests {

    private UsuarioRepository underTest;

    @Autowired
    public UsuarioEntityRepositoryTests(UsuarioRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalled() {
        UsuarioEntity usuarioEntity = TestDataUtil.createTestUsuarioEntityA();
        underTest.save(usuarioEntity);
        Optional<UsuarioEntity> result = underTest.findById(usuarioEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(usuarioEntity);
    }

    @Test
    public void testThatUserCanBeUpdated() {
        UsuarioEntity userEntityA = TestDataUtil.createTestUsuarioEntityA();
        underTest.save(userEntityA);
        userEntityA.setNome("UPDATED");
        underTest.save(userEntityA);
        Optional<UsuarioEntity> result = underTest.findById(userEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntityA);
    }

    @Test
    public void testThatUserCanBeDeleted() {
        UsuarioEntity userEntityA = TestDataUtil.createTestUsuarioEntityA();
        underTest.save(userEntityA);
        underTest.deleteById(userEntityA.getId());
        Optional<UsuarioEntity> result = underTest.findById(userEntityA.getId());
        assertThat(result).isEmpty();
    }
}
