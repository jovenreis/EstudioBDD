package com.estudiobdd.service;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.SugestaoRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Regra 4: a sugestão nasce PENDENTE, a rejeição exige motivo, aceitar não mexe
 * nos cenários e uma sugestão já decidida não pode ser alterada.
 */
@Service
public class SugestaoService {

    private final SugestaoRepository sugestaoRepository;
    private final HistoriaService historiaService;
    private final UsuarioService usuarioService;

    public SugestaoService(SugestaoRepository sugestaoRepository,
                           HistoriaService historiaService,
                           UsuarioService usuarioService) {
        this.sugestaoRepository = sugestaoRepository;
        this.historiaService = historiaService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Sugestao registrar(Long historiaId, Long usuarioId, Sugestao nova) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        HistoriaUsuario historia = historiaService.buscar(historiaId);

        Sugestao sugestao = new Sugestao(usuario, nova.getFerramenta(), nova.getPrompt(), nova.getConteudo());
        historia.adicionarSugestao(sugestao);
        return carregarDetalhes(sugestaoRepository.save(sugestao));
    }

    // lista por história e, se informada, por decisão
    @Transactional(readOnly = true)
    public List<Sugestao> listarPorHistoria(Long historiaId, DecisaoSugestao decisao) {
        historiaService.buscar(historiaId);
        List<Sugestao> sugestoes = decisao == null
                ? sugestaoRepository.findByHistoriaIdOrderByRegistradaEmAsc(historiaId)
                : sugestaoRepository.findByHistoriaIdAndDecisaoOrderByRegistradaEmAsc(historiaId, decisao);
        sugestoes.forEach(this::carregarDetalhes);
        return sugestoes;
    }

    @Transactional(readOnly = true)
    public Sugestao buscarDetalhada(Long id) {
        return carregarDetalhes(buscar(id));
    }

    @Transactional
    public Sugestao aceitar(Long id, Long usuarioId, String justificativa) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        Sugestao sugestao = buscarPendente(id);

        // aceitar não altera os cenários: o analista faz o ajuste depois
        decidir(sugestao, usuario, DecisaoSugestao.ACEITA, null, justificativa);
        return carregarDetalhes(sugestao);
    }

    @Transactional
    public Sugestao rejeitar(Long id, Long usuarioId, MotivoRejeicao motivo, String justificativa) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        Sugestao sugestao = buscarPendente(id);

        if (motivo == null) {
            throw new RegraNegocioException(
                    "Informe o motivo da rejeição (INCORRETA, GENERICA, DUPLICADA, FORA_DO_ESCOPO ou OUTRO)");
        }

        decidir(sugestao, usuario, DecisaoSugestao.REJEITADA, motivo, justificativa);
        return carregarDetalhes(sugestao);
    }

    private Sugestao buscar(Long id) {
        return sugestaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sugestão " + id + " não encontrada"));
    }

    private Sugestao buscarPendente(Long id) {
        Sugestao sugestao = buscar(id);
        if (!sugestao.isPendente()) {
            throw new RegraNegocioException("A sugestão já foi "
                    + (sugestao.getDecisao() == DecisaoSugestao.ACEITA ? "aceita" : "rejeitada")
                    + " e não pode ser alterada");
        }
        return sugestao;
    }

    private void decidir(Sugestao sugestao, Usuario usuario, DecisaoSugestao decisao,
                         MotivoRejeicao motivo, String justificativa) {
        sugestao.setDecisao(decisao);
        sugestao.setMotivoRejeicao(motivo);
        sugestao.setJustificativa(justificativa == null || justificativa.isBlank() ? null : justificativa.trim());
        sugestao.setDecididaPor(usuario);
        sugestao.setDecididaEm(LocalDateTime.now());
    }

    private Sugestao carregarDetalhes(Sugestao s) {
        Hibernate.initialize(s.getRegistradaPor());
        Hibernate.initialize(s.getDecididaPor());
        return s;
    }
}
