package com.estudiobdd.dto;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;

import java.time.LocalDateTime;
import java.util.List;

public record HistoriaDetalheResponse(Long id, Long projetoId, String projetoNome,
                                      String titulo, String papel, String objetivo, String beneficio,
                                      Prioridade prioridade, Long autorId, String autorNome,
                                      LocalDateTime criadaEm, LocalDateTime atualizadaEm,
                                      List<CenarioResponse> cenarios, long quantidadeSugestoesPendentes,
                                      boolean usaFluxo, String etapaNome, boolean podeEditar) {

    public static HistoriaDetalheResponse de(HistoriaUsuario h) {
        long pendentes = h.getSugestoes().stream()
                .filter(s -> s.getDecisao() == DecisaoSugestao.PENDENTE)
                .count();
        return new HistoriaDetalheResponse(h.getId(), h.getProjeto().getId(), h.getProjeto().getNome(),
                h.getTitulo(), h.getPapel(), h.getObjetivo(), h.getBeneficio(),
                h.getPrioridade(), h.getAutor().getId(), h.getAutor().getNome(),
                h.getCriadaEm(), h.getAtualizadaEm(),
                h.getCenarios().stream().map(CenarioResponse::de).toList(), pendentes,
                h.getProjeto().usaFluxo(),
                h.getEtapaAtual() == null ? null : h.getEtapaAtual().getNome(),
                // só informativo para a tela; quem bloqueia a edição é o serviço
                !h.getProjeto().usaFluxo() || h.getEtapaAtual() == null
                        || (h.getEtapaAtual().isPermiteEdicao() && !h.getEtapaAtual().isFinal()));
    }
}
