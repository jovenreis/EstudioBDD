package com.estudiobdd.dto;

import com.estudiobdd.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "Informe o nome") @Size(max = 120) String nome,
        @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") @Size(max = 160) String email) {

    public Usuario paraDominio() {
        return new Usuario(nome.trim(), email.trim());
    }
}
