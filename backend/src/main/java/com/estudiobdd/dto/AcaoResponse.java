package com.estudiobdd.dto;

import com.estudiobdd.model.TransicaoFluxo;

// uma ação que o usuário atual pode tentar; a SPA gera um botão para cada uma
public record AcaoResponse(Long transicaoId, String nomeAcao, String destino, boolean exigeComentario) {

    public static AcaoResponse de(TransicaoFluxo t) {
        return new AcaoResponse(t.getId(), t.getNomeAcao(), t.getDestino().getNome(), t.isExigeComentario());
    }
}
