package com.estudiobdd.dto;

import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HistoriaRequest(
        @NotBlank(message = "Informe o título") @Size(max = 150) String titulo,
        @NotBlank(message = "Informe o papel (Como...)") @Size(max = 150) String papel,
        @NotBlank(message = "Informe o objetivo (quero...)") @Size(max = 500) String objetivo,
        @NotBlank(message = "Informe o benefício (para...)") @Size(max = 500) String beneficio,
        @NotNull(message = "Informe a prioridade (ALTA, MEDIA ou BAIXA)") Prioridade prioridade) {

    public HistoriaUsuario paraDominio() {
        return new HistoriaUsuario(titulo.trim(), papel.trim(), objetivo.trim(), beneficio.trim(), prioridade);
    }
}
