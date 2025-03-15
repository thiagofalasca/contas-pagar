package com.totvs.contas_pagar.domain.entities;

import com.totvs.contas_pagar.domain.enums.TipoCargoUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class UsuarioEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    private String email;

    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoCargoUsuario cargo;

}