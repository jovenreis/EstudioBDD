package com.estudiobdd.dto;

import com.estudiobdd.model.Papel;
import com.estudiobdd.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Papel papel) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel());
    }
}
