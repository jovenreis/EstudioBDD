package com.estudiobdd.dto;

import com.estudiobdd.model.Sugestao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SugestaoRequest(
        @NotBlank(message = "Informe a ferramenta de IA usada") @Size(max = 80) String ferramenta,
        @NotBlank(message = "Informe o prompt utilizado") String prompt,
        @NotBlank(message = "Informe o conteúdo da sugestão") String conteudo) {

    // o usuário que registra é definido no serviço, a partir do cabeçalho
    public Sugestao paraDominio() {
        return new Sugestao(null, ferramenta.trim(), prompt.trim(), conteudo.trim());
    }
}
