package com.estudiobdd.dto;

import com.estudiobdd.model.MotivoRejeicao;
import jakarta.validation.constraints.Size;

// o motivo não tem @NotNull de propósito: a obrigatoriedade é regra de negócio
// (regra 4) e fica no SugestaoService, que devolve 422 com mensagem clara
public record RejeitarSugestaoRequest(MotivoRejeicao motivo, @Size(max = 1000) String justificativa) {
}
