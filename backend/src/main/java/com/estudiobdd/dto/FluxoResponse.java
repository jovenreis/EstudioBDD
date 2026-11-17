package com.estudiobdd.dto;

import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.Fluxo;
import com.estudiobdd.model.Papel;
import com.estudiobdd.model.TransicaoFluxo;

import java.util.List;

public record FluxoResponse(Long id, String nome, String descricao, boolean ativo,
                            List<Etapa> etapas, List<Transicao> transicoes) {

    public record Etapa(Long id, String nome, int posicao, boolean inicial, boolean etapaFinal,
                        boolean permiteEdicao) {

        public static Etapa de(EtapaFluxo e) {
            return new Etapa(e.getId(), e.getNome(), e.getPosicao(), e.isInicial(), e.isFinal(), e.isPermiteEdicao());
        }
    }

    public record Transicao(Long id, String nomeAcao, Long origemId, String origem, Long destinoId, String destino,
                            Papel papelPermitido, boolean exigeComentario, boolean exigeUsuarioDiferenteDoAutor,
                            boolean exigeCenarios, boolean exigeSemSugestaoPendente) {

        public static Transicao de(TransicaoFluxo t) {
            return new Transicao(t.getId(), t.getNomeAcao(), t.getOrigem().getId(), t.getOrigem().getNome(),
                    t.getDestino().getId(), t.getDestino().getNome(), t.getPapelPermitido(), t.isExigeComentario(),
                    t.isExigeUsuarioDiferenteDoAutor(), t.isExigeCenarios(), t.isExigeSemSugestaoPendente());
        }
    }

    // versão resumida, sem etapas e transições (para a lista de fluxos)
    public static FluxoResponse resumo(Fluxo f) {
        return new FluxoResponse(f.getId(), f.getNome(), f.getDescricao(), f.isAtivo(), List.of(), List.of());
    }

    public static FluxoResponse de(Fluxo f) {
        return new FluxoResponse(f.getId(), f.getNome(), f.getDescricao(), f.isAtivo(),
                f.getEtapas().stream().map(Etapa::de).toList(),
                f.getTransicoes().stream().map(Transicao::de).toList());
    }
}
