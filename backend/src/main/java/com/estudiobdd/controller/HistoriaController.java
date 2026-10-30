package com.estudiobdd.controller;

import com.estudiobdd.dto.HistoriaDetalheResponse;
import com.estudiobdd.dto.HistoriaRequest;
import com.estudiobdd.dto.HistoriaResumoResponse;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.service.HistoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class HistoriaController {

    private final HistoriaService historiaService;

    public HistoriaController(HistoriaService historiaService) {
        this.historiaService = historiaService;
    }

    @GetMapping("/api/projetos/{projetoId}/historias")
    public List<HistoriaResumoResponse> listarPorProjeto(@PathVariable Long projetoId,
                                                         @RequestParam(required = false) Prioridade prioridade) {
        return historiaService.listarPorProjeto(projetoId, prioridade).stream()
                .map(HistoriaResumoResponse::de)
                .toList();
    }

    @PostMapping("/api/projetos/{projetoId}/historias")
    public ResponseEntity<HistoriaDetalheResponse> criar(@PathVariable Long projetoId,
                                                         @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                                         @Valid @RequestBody HistoriaRequest req) {
        HistoriaUsuario criada = historiaService.criar(projetoId, usuarioId, req.paraDominio());
        return ResponseEntity.created(URI.create("/api/historias/" + criada.getId()))
                .body(HistoriaDetalheResponse.de(criada));
    }

    @GetMapping("/api/historias/{id}")
    public HistoriaDetalheResponse buscar(@PathVariable Long id) {
        return HistoriaDetalheResponse.de(historiaService.buscarDetalhada(id));
    }

    @PutMapping("/api/historias/{id}")
    public HistoriaDetalheResponse atualizar(@PathVariable Long id,
                                             @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                             @Valid @RequestBody HistoriaRequest req) {
        return HistoriaDetalheResponse.de(historiaService.atualizar(id, usuarioId, req.paraDominio()));
    }

    @DeleteMapping("/api/historias/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader(Cabecalhos.USUARIO) Long usuarioId) {
        historiaService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
