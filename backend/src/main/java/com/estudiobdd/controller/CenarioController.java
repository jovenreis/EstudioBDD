package com.estudiobdd.controller;

import com.estudiobdd.dto.CenarioRequest;
import com.estudiobdd.dto.CenarioResponse;
import com.estudiobdd.model.Cenario;
import com.estudiobdd.service.CenarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

// Os passos são editados junto com o cenário (composição): cada alteração
// revalida a gramática do cenário inteiro, como pede a regra 2.
@RestController
public class CenarioController {

    private final CenarioService cenarioService;

    public CenarioController(CenarioService cenarioService) {
        this.cenarioService = cenarioService;
    }

    @GetMapping("/api/historias/{historiaId}/cenarios")
    public List<CenarioResponse> listar(@PathVariable Long historiaId) {
        return cenarioService.listarPorHistoria(historiaId).stream().map(CenarioResponse::de).toList();
    }

    @PostMapping("/api/historias/{historiaId}/cenarios")
    public ResponseEntity<CenarioResponse> criar(@PathVariable Long historiaId,
                                                 @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                                 @Valid @RequestBody CenarioRequest req) {
        Cenario criado = cenarioService.criar(historiaId, usuarioId, req.paraDominio());
        return ResponseEntity.created(URI.create("/api/cenarios/" + criado.getId()))
                .body(CenarioResponse.de(criado));
    }

    @GetMapping("/api/cenarios/{id}")
    public CenarioResponse buscar(@PathVariable Long id) {
        return CenarioResponse.de(cenarioService.buscarDetalhado(id));
    }

    @PutMapping("/api/cenarios/{id}")
    public CenarioResponse atualizar(@PathVariable Long id,
                                     @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                     @Valid @RequestBody CenarioRequest req) {
        return CenarioResponse.de(cenarioService.atualizar(id, usuarioId, req.paraDominio()));
    }

    @DeleteMapping("/api/cenarios/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader(Cabecalhos.USUARIO) Long usuarioId) {
        cenarioService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
