package com.estudiobdd.dto;

import com.estudiobdd.model.HistoricoTransicao;

import java.time.LocalDateTime;

public record HistoricoResponse(Long id, LocalDateTime dataHora, String acao, String origem, String destino,
                                Long usuarioId, String usuarioNome, String comentario) {

    public static HistoricoResponse de(HistoricoTransicao h) {
        return new HistoricoResponse(h.getId(), h.getDataHora(), h.getTransicao().getNomeAcao(),
                h.getTransicao().getOrigem().getNome(), h.getTransicao().getDestino().getNome(),
                h.getUsuario().getId(), h.getUsuario().getNome(), h.getComentario());
    }
}
