package com.estudiobdd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EtapaRequest(
        @NotBlank(message = "Informe o nome da etapa") @Size(max = 80) String nome,
        int posicao,
        boolean inicial,
        boolean etapaFinal,
        boolean permiteEdicao) {
}
