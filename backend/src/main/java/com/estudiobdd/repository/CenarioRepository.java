package com.estudiobdd.repository;

import com.estudiobdd.model.Cenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenarioRepository extends JpaRepository<Cenario, Long> {
}
