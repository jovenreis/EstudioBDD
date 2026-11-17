package com.estudiobdd.repository;

import com.estudiobdd.model.EtapaFluxo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtapaFluxoRepository extends JpaRepository<EtapaFluxo, Long> {

    List<EtapaFluxo> findByFluxoIdOrderByPosicaoAsc(Long fluxoId);

    boolean existsByFluxoIdAndNomeIgnoreCase(Long fluxoId, String nome);

    boolean existsByFluxoIdAndNomeIgnoreCaseAndIdNot(Long fluxoId, String nome, Long id);
}
