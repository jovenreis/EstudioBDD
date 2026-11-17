package com.estudiobdd.controller;

import com.estudiobdd.dto.AcaoResponse;
import com.estudiobdd.dto.DefinirFluxoRequest;
import com.estudiobdd.dto.EtapaRequest;
import com.estudiobdd.dto.ExecutarAcaoRequest;
import com.estudiobdd.dto.FluxoResponse;
import com.estudiobdd.dto.HistoriaDetalheResponse;
import com.estudiobdd.dto.HistoricoResponse;
import com.estudiobdd.dto.ProjetoResponse;
import com.estudiobdd.dto.QuadroResponse;
import com.estudiobdd.dto.TransicaoRequest;
import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.TransicaoFluxo;
import com.estudiobdd.service.FluxoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Atividade bônus: quadro, ações e histórico das histórias, e configuração do fluxo
@RestController
public class FluxoController {

    private final FluxoService fluxoService;

    public FluxoController(FluxoService fluxoService) {
        this.fluxoService = fluxoService;
    }

    @GetMapping("/api/projetos/{projetoId}/quadro")
    public QuadroResponse quadro(@PathVariable Long projetoId) {
        return QuadroResponse.de(fluxoService.quadro(projetoId));
    }

    @PutMapping("/api/projetos/{projetoId}/fluxo")
    public ProjetoResponse definirFluxo(@PathVariable Long projetoId, @RequestBody DefinirFluxoRequest req) {
        return ProjetoResponse.de(fluxoService.definirFluxoDoProjeto(projetoId, req.fluxoId()));
    }

    @GetMapping("/api/historias/{historiaId}/acoes")
    public List<AcaoResponse> acoes(@PathVariable Long historiaId,
                                    @RequestHeader(Cabecalhos.USUARIO) Long usuarioId) {
        return fluxoService.acoesDisponiveis(historiaId, usuarioId).stream().map(AcaoResponse::de).toList();
    }

    @PostMapping("/api/historias/{historiaId}/acoes/{transicaoId}")
    public HistoriaDetalheResponse executar(@PathVariable Long historiaId, @PathVariable Long transicaoId,
                                            @RequestHeader(Cabecalhos.USUARIO) Long usuarioId,
                                            @Valid @RequestBody(required = false) ExecutarAcaoRequest req) {
        String comentario = req == null ? null : req.comentario();
        return HistoriaDetalheResponse.de(fluxoService.executar(historiaId, transicaoId, usuarioId, comentario));
    }

    @GetMapping("/api/historias/{historiaId}/historico")
    public List<HistoricoResponse> historico(@PathVariable Long historiaId) {
        return fluxoService.historico(historiaId).stream().map(HistoricoResponse::de).toList();
    }

    @GetMapping("/api/fluxos")
    public List<FluxoResponse> listar() {
        return fluxoService.listar().stream().map(FluxoResponse::resumo).toList();
    }

    @GetMapping("/api/fluxos/{fluxoId}")
    public FluxoResponse buscar(@PathVariable Long fluxoId) {
        return FluxoResponse.de(fluxoService.buscarDetalhado(fluxoId));
    }

    @PostMapping("/api/fluxos/{fluxoId}/etapas")
    @ResponseStatus(HttpStatus.CREATED)
    public FluxoResponse.Etapa criarEtapa(@PathVariable Long fluxoId, @Valid @RequestBody EtapaRequest req) {
        EtapaFluxo etapa = fluxoService.criarEtapa(fluxoId, req.nome(), req.posicao(), req.inicial(),
                req.etapaFinal(), req.permiteEdicao());
        return FluxoResponse.Etapa.de(etapa);
    }

    @PutMapping("/api/fluxos/{fluxoId}/etapas/{etapaId}")
    public FluxoResponse.Etapa atualizarEtapa(@PathVariable Long fluxoId, @PathVariable Long etapaId,
                                              @Valid @RequestBody EtapaRequest req) {
        EtapaFluxo etapa = fluxoService.atualizarEtapa(fluxoId, etapaId, req.nome(), req.posicao(), req.inicial(),
                req.etapaFinal(), req.permiteEdicao());
        return FluxoResponse.Etapa.de(etapa);
    }

    @PostMapping("/api/fluxos/{fluxoId}/transicoes")
    @ResponseStatus(HttpStatus.CREATED)
    public FluxoResponse.Transicao criarTransicao(@PathVariable Long fluxoId,
                                                  @Valid @RequestBody TransicaoRequest req) {
        TransicaoFluxo t = fluxoService.criarTransicao(fluxoId, req.nomeAcao(), req.origemId(), req.destinoId(),
                req.papelPermitido(), req.exigeComentario(), req.exigeUsuarioDiferenteDoAutor(),
                req.exigeCenarios(), req.exigeSemSugestaoPendente());
        return FluxoResponse.Transicao.de(t);
    }
}
