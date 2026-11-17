package com.estudiobdd.dto;

import com.estudiobdd.service.FluxoService.Quadro;

import java.util.List;

// colunas do quadro, na ordem das etapas cadastradas
public record QuadroResponse(Long projetoId, String projetoNome, Long fluxoId, String fluxoNome,
                             List<Coluna> etapas) {

    public record Coluna(Long id, String nome, int posicao, boolean inicial, boolean etapaFinal,
                         boolean permiteEdicao, List<HistoriaResumoResponse> historias) {
    }

    public static QuadroResponse de(Quadro q) {
        List<Coluna> colunas = q.etapas().stream()
                .map(e -> new Coluna(e.getId(), e.getNome(), e.getPosicao(), e.isInicial(), e.isFinal(),
                        e.isPermiteEdicao(),
                        q.historiasPorEtapa().get(e.getId()).stream().map(HistoriaResumoResponse::de).toList()))
                .toList();
        return new QuadroResponse(q.projeto().getId(), q.projeto().getNome(), q.fluxo().getId(), q.fluxo().getNome(),
                colunas);
    }
}
