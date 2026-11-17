package com.estudiobdd.dto;

import com.estudiobdd.model.Projeto;

import java.time.LocalDateTime;

// fluxoId e fluxoNome vêm nulos quando o projeto não usa fluxo de aprovação
public record ProjetoResponse(Long id, String nome, String descricao, String contextoDominio,
                              LocalDateTime dataCriacao, Long fluxoId, String fluxoNome) {

    public static ProjetoResponse de(Projeto p) {
        boolean comFluxo = p.usaFluxo();
        return new ProjetoResponse(p.getId(), p.getNome(), p.getDescricao(), p.getContextoDominio(),
                p.getDataCriacao(), comFluxo ? p.getFluxo().getId() : null, comFluxo ? p.getFluxo().getNome() : null);
    }
}
