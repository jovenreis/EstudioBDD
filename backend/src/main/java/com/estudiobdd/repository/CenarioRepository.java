package com.estudiobdd.repository;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.OrigemCenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenarioRepository extends JpaRepository<Cenario, Long> {

    // usado no resumo do projeto (regra 7)
    long countByHistoriaProjetoIdAndOrigem(Long projetoId, OrigemCenario origem);
}
