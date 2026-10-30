package com.estudiobdd.repository;

import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {

    boolean existsByProjetoIdAndTituloIgnoreCase(Long projetoId, String titulo);

    boolean existsByProjetoIdAndTituloIgnoreCaseAndIdNot(Long projetoId, String titulo, Long id);

    boolean existsByProjetoId(Long projetoId);

    List<HistoriaUsuario> findByProjetoIdOrderByTituloAsc(Long projetoId);

    List<HistoriaUsuario> findByProjetoIdAndPrioridadeOrderByTituloAsc(Long projetoId, Prioridade prioridade);
}
