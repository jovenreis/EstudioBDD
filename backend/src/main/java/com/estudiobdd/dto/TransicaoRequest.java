package com.estudiobdd.dto;

import com.estudiobdd.model.Papel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransicaoRequest(
        @NotBlank(message = "Informe o nome da ação") @Size(max = 80) String nomeAcao,
        @NotNull(message = "Informe a etapa de origem") Long origemId,
        @NotNull(message = "Informe a etapa de destino") Long destinoId,
        @NotNull(message = "Informe o papel permitido (ANALISTA ou REVISOR)") Papel papelPermitido,
        boolean exigeComentario,
        boolean exigeUsuarioDiferenteDoAutor,
        boolean exigeCenarios,
        boolean exigeSemSugestaoPendente) {
}
