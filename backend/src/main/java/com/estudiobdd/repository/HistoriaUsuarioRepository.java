package com.estudiobdd.repository;

import com.estudiobdd.model.HistoriaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {
}
