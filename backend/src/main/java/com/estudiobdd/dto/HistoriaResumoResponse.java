package com.estudiobdd.dto;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;

// usado na lista de histórias do projeto (regra 6)
public record HistoriaResumoResponse(Long id, String titulo, Prioridade prioridade,
                                     Long autorId, String autorNome,
                                     int quantidadeCenarios, long quantidadeSugestoesPendentes) {

    public static HistoriaResumoResponse de(HistoriaUsuario h) {
        long pendentes = h.getSugestoes().stream()
                .filter(s -> s.getDecisao() == DecisaoSugestao.PENDENTE)
                .count();
        return new HistoriaResumoResponse(h.getId(), h.getTitulo(), h.getPrioridade(),
                h.getAutor().getId(), h.getAutor().getNome(),
                h.getCenarios().size(), pendentes);
    }
}
