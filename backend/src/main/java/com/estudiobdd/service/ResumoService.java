package com.estudiobdd.service;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.repository.CenarioRepository;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.SugestaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Regra 7: quantidade de histórias, cenários por origem, sugestões por decisão
 * e motivos de rejeição do mais frequente para o menos frequente.
 */
@Service
public class ResumoService {

    private final ProjetoService projetoService;
    private final HistoriaUsuarioRepository historiaRepository;
    private final CenarioRepository cenarioRepository;
    private final SugestaoRepository sugestaoRepository;

    public ResumoService(ProjetoService projetoService,
                         HistoriaUsuarioRepository historiaRepository,
                         CenarioRepository cenarioRepository,
                         SugestaoRepository sugestaoRepository) {
        this.projetoService = projetoService;
        this.historiaRepository = historiaRepository;
        this.cenarioRepository = cenarioRepository;
        this.sugestaoRepository = sugestaoRepository;
    }

    public record Resumo(Projeto projeto,
                         long totalHistorias,
                         Map<OrigemCenario, Long> cenariosPorOrigem,
                         Map<DecisaoSugestao, Long> sugestoesPorDecisao,
                         Map<MotivoRejeicao, Long> motivosRejeicao) {
    }

    @Transactional(readOnly = true)
    public Resumo gerar(Long projetoId) {
        Projeto projeto = projetoService.buscar(projetoId);

        Map<OrigemCenario, Long> porOrigem = new EnumMap<>(OrigemCenario.class);
        for (OrigemCenario origem : OrigemCenario.values()) {
            porOrigem.put(origem, cenarioRepository.countByHistoriaProjetoIdAndOrigem(projetoId, origem));
        }

        Map<DecisaoSugestao, Long> porDecisao = new EnumMap<>(DecisaoSugestao.class);
        for (DecisaoSugestao decisao : DecisaoSugestao.values()) {
            porDecisao.put(decisao, sugestaoRepository.countByHistoriaProjetoIdAndDecisao(projetoId, decisao));
        }

        // só os motivos que aparecem, ordenados do mais usado para o menos usado
        Map<MotivoRejeicao, Long> motivos = new LinkedHashMap<>();
        for (MotivoRejeicao motivo : MotivoRejeicao.values()) {
            long qtd = sugestaoRepository.countByHistoriaProjetoIdAndMotivoRejeicao(projetoId, motivo);
            if (qtd > 0) {
                motivos.put(motivo, qtd);
            }
        }
        Map<MotivoRejeicao, Long> motivosOrdenados = new LinkedHashMap<>();
        motivos.entrySet().stream()
                .sorted(Map.Entry.<MotivoRejeicao, Long>comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> motivosOrdenados.put(e.getKey(), e.getValue()));

        return new Resumo(projeto, historiaRepository.countByProjetoId(projetoId),
                porOrigem, porDecisao, motivosOrdenados);
    }
}
