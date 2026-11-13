package com.estudiobdd.controller;

import com.estudiobdd.dto.LoginRequest;
import com.estudiobdd.dto.UsuarioResponse;
import com.estudiobdd.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Confere e-mail e senha e devolve o usuário. A SPA guarda o id e o envia no
 * cabeçalho X-Usuario-Id das próximas requisições. Não há token: as demais rotas
 * continuam confiando no cabeçalho (limitação documentada no README).
 */
@RestController
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/api/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req) {
        return UsuarioResponse.de(usuarioService.autenticar(req.email(), req.senha()));
    }
}
