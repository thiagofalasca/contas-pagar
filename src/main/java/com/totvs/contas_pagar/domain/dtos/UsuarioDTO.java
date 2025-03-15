package com.totvs.contas_pagar.domain.dtos;

import com.totvs.contas_pagar.domain.dtos.validation.OnCreate;
import com.totvs.contas_pagar.domain.dtos.validation.OnUpdate;
import com.totvs.contas_pagar.domain.enums.TipoCargoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private UUID id;

    @NotNull(groups = OnCreate.class, message = "O nome é obrigatório")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O nome não pode ser vazio")
    @Size(groups = {OnCreate.class, OnUpdate.class}, min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(groups = OnCreate.class, message = "O e-mail é obrigatório")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "O e-mail não pode ser vazio")
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "O e-mail deve ser válido")
    private String email;

    @NotNull(groups = OnCreate.class, message = "A senha é obrigatória")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "A senha não pode ser vazia")
    @Size(groups = {OnCreate.class, OnUpdate.class}, min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres")
    private String senha;

    @NotNull(groups = OnCreate.class, message = "O cargo é obrigatório")
    private TipoCargoUsuario cargo;
}
