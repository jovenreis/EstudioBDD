package com.estudiobdd.repository;

import com.estudiobdd.model.TransicaoFluxo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransicaoFluxoRepository extends JpaRepository<TransicaoFluxo, Long> {

    // ações que saem de uma etapa: é daqui que vêm os botões da SPA
    List<TransicaoFluxo> findByOrigemIdOrderByIdAsc(Long origemId);

    List<TransicaoFluxo> findByFluxoIdOrderByIdAsc(Long fluxoId);
}
