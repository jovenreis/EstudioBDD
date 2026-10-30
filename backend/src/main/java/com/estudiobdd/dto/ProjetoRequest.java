package com.estudiobdd.dto;

import com.estudiobdd.model.Projeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjetoRequest(
        @NotBlank(message = "Informe o nome do projeto") @Size(max = 120) String nome,
        @Size(max = 500) String descricao,
        String contextoDominio) {

    public Projeto paraDominio() {
        return new Projeto(nome.trim(), descricao, contextoDominio);
    }
}
