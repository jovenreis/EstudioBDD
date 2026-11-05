package com.estudiobdd.repository;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.Sugestao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    List<Sugestao> findByHistoriaIdOrderByRegistradaEmAsc(Long historiaId);

    List<Sugestao> findByHistoriaIdAndDecisaoOrderByRegistradaEmAsc(Long historiaId, DecisaoSugestao decisao);

    // usados no resumo do projeto (regra 7)
    long countByHistoriaProjetoIdAndDecisao(Long projetoId, DecisaoSugestao decisao);

    long countByHistoriaProjetoIdAndMotivoRejeicao(Long projetoId, MotivoRejeicao motivo);
}
