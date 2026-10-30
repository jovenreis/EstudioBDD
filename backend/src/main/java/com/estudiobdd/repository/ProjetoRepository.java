package com.estudiobdd.repository;

import com.estudiobdd.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Projeto> findAllByOrderByNomeAsc();
}
