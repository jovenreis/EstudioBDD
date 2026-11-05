package com.estudiobdd.controller;

import com.estudiobdd.dto.AceitarSugestaoRequest;
import com.estudiobdd.dto.RejeitarSugestaoRequest;
import com.estudiobdd.dto.SugestaoRequest;
import com.estudiobdd.dto.SugestaoResponse;
import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.service.SugestaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    @GetMapping("/api/historias/{historiaId}/sugestoes")
    public List<SugestaoResponse> listar(@PathVariable Long historiaId,
                                         @RequestParam(required = false) DecisaoSugestao decisao) {
        return sugestaoService.listarPorHistoria(historiaId, decisao).stream().map(SugestaoResponse::de).toList();
    }

    @PostMapping("/api/historias/{historiaId}/sugestoes")
    public ResponseEntity<SugestaoResponse> registrar(@PathVariable Long historiaId,
                                                      @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                                      @Valid @RequestBody SugestaoRequest req) {
        Sugestao criada = sugestaoService.registrar(historiaId, usuarioId, req.paraDominio());
        return ResponseEntity.created(URI.create("/api/sugestoes/" + criada.getId()))
                .body(SugestaoResponse.de(criada));
    }

    @GetMapping("/api/sugestoes/{id}")
    public SugestaoResponse buscar(@PathVariable Long id) {
        return SugestaoResponse.de(sugestaoService.buscarDetalhada(id));
    }

    // o corpo é opcional: aceitar não exige justificativa
    @PostMapping("/api/sugestoes/{id}/aceitar")
    public SugestaoResponse aceitar(@PathVariable Long id,
                                    @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                    @Valid @RequestBody(required = false) AceitarSugestaoRequest req) {
        String justificativa = req == null ? null : req.justificativa();
        return SugestaoResponse.de(sugestaoService.aceitar(id, usuarioId, justificativa));
    }

    @PostMapping("/api/sugestoes/{id}/rejeitar")
    public SugestaoResponse rejeitar(@PathVariable Long id,
                                     @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                     @Valid @RequestBody RejeitarSugestaoRequest req) {
        return SugestaoResponse.de(sugestaoService.rejeitar(id, usuarioId, req.motivo(), req.justificativa()));
    }
}
