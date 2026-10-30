package com.estudiobdd.controller;

import com.estudiobdd.dto.ProjetoRequest;
import com.estudiobdd.dto.ProjetoResponse;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @GetMapping
    public List<ProjetoResponse> listar() {
        return projetoService.listar().stream().map(ProjetoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ProjetoResponse buscar(@PathVariable Long id) {
        return ProjetoResponse.de(projetoService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponse> criar(@Valid @RequestBody ProjetoRequest req) {
        Projeto criado = projetoService.criar(req.paraDominio());
        return ResponseEntity.created(URI.create("/api/projetos/" + criado.getId()))
                .body(ProjetoResponse.de(criado));
    }

    @PutMapping("/{id}")
    public ProjetoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProjetoRequest req) {
        return ProjetoResponse.de(projetoService.atualizar(id, req.paraDominio()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
