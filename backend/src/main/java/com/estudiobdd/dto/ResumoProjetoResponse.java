package com.estudiobdd.dto;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.service.ResumoService.Resumo;

import java.util.List;
import java.util.Map;

public record ResumoProjetoResponse(Long projetoId, String projetoNome,
                                    long totalHistorias,
                                    Map<OrigemCenario, Long> cenariosPorOrigem,
                                    Map<DecisaoSugestao, Long> sugestoesPorDecisao,
                                    List<MotivoQuantidade> motivosRejeicao) {

    public record MotivoQuantidade(MotivoRejeicao motivo, long quantidade) {
    }

    public static ResumoProjetoResponse de(Resumo r) {
        List<MotivoQuantidade> motivos = r.motivosRejeicao().entrySet().stream()
                .map(e -> new MotivoQuantidade(e.getKey(), e.getValue()))
                .toList();
        return new ResumoProjetoResponse(r.projeto().getId(), r.projeto().getNome(), r.totalHistorias(),
                r.cenariosPorOrigem(), r.sugestoesPorDecisao(), motivos);
    }
}
