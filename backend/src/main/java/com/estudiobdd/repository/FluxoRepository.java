package com.estudiobdd.repository;

import com.estudiobdd.model.Fluxo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FluxoRepository extends JpaRepository<Fluxo, Long> {

    List<Fluxo> findAllByOrderByNomeAsc();

    Optional<Fluxo> findByNome(String nome);
}
