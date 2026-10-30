package com.estudiobdd.controller;

import com.estudiobdd.dto.UsuarioRequest;
import com.estudiobdd.dto.UsuarioResponse;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar().stream().map(UsuarioResponse::de).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.de(usuarioService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest req) {
        Usuario criado = usuarioService.criar(req.paraDominio());
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.getId()))
                .body(UsuarioResponse.de(criado));
    }
}
