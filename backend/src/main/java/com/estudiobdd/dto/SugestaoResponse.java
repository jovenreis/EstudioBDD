package com.estudiobdd.dto;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.Sugestao;

import java.time.LocalDateTime;

public record SugestaoResponse(Long id, String ferramenta, String prompt, String conteudo,
                               DecisaoSugestao decisao, MotivoRejeicao motivoRejeicao, String justificativa,
                               Long registradaPorId, String registradaPorNome, LocalDateTime registradaEm,
                               Long decididaPorId, String decididaPorNome, LocalDateTime decididaEm) {

    public static SugestaoResponse de(Sugestao s) {
        return new SugestaoResponse(s.getId(), s.getFerramenta(), s.getPrompt(), s.getConteudo(),
                s.getDecisao(), s.getMotivoRejeicao(), s.getJustificativa(),
                s.getRegistradaPor().getId(), s.getRegistradaPor().getNome(), s.getRegistradaEm(),
                s.getDecididaPor() == null ? null : s.getDecididaPor().getId(),
                s.getDecididaPor() == null ? null : s.getDecididaPor().getNome(),
                s.getDecididaEm());
    }
}
