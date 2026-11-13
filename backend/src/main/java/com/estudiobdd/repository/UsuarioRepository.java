package com.estudiobdd.repository;

import com.estudiobdd.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findAllByOrderByNomeAsc();
}
