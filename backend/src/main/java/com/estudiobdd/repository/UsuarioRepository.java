package com.estudiobdd.repository;

import com.estudiobdd.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findAllByOrderByNomeAsc();
}
