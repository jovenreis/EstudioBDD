package com.estudiobdd.dto;

import com.estudiobdd.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Informe o nome") @Size(max = 120) String nome,
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") @Size(max = 160) String email,
        @NotBlank(message = "Informe a senha")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha) {

    // a senha não entra no objeto de domínio: o serviço transforma em hash
    public Usuario paraDominio() {
        return new Usuario(nome.trim(), email.trim());
    }
}
