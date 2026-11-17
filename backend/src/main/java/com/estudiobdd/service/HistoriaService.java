package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoriaService {

    private final HistoriaUsuarioRepository historiaRepository;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    public HistoriaService(HistoriaUsuarioRepository historiaRepository,
                           ProjetoService projetoService,
                           UsuarioService usuarioService) {
        this.historiaRepository = historiaRepository;
        this.projetoService = projetoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public HistoriaUsuario criar(Long projetoId, Long usuarioId, HistoriaUsuario nova) {
        Projeto projeto = projetoService.buscar(projetoId);
        Usuario autor = usuarioService.buscar(usuarioId);
        validarNarrativa(nova);

        if (historiaRepository.existsByProjetoIdAndTituloIgnoreCase(projetoId, nova.getTitulo())) {
            throw new ConflitoException(tituloRepetido(nova.getTitulo()));
        }

        nova.vincular(projeto, autor);
        // bônus: com fluxo ativo, a história nasce na etapa inicial
        if (projeto.usaFluxo()) {
            nova.setEtapaAtual(projeto.getFluxo().etapaInicial()
                    .orElseThrow(() -> new RegraNegocioException("O fluxo do projeto não tem etapa inicial")));
        }
        return carregarDetalhes(historiaRepository.save(nova));
    }

    @Transactional(readOnly = true)
    public HistoriaUsuario buscarDetalhada(Long id) {
        return carregarDetalhes(buscar(id));
    }

    // regra 6 (parte da consulta): histórias do projeto, com filtro opcional por prioridade
    @Transactional(readOnly = true)
    public List<HistoriaUsuario> listarPorProjeto(Long projetoId, Prioridade prioridade) {
        projetoService.buscar(projetoId);
        List<HistoriaUsuario> historias = prioridade == null
                ? historiaRepository.findByProjetoIdOrderByTituloAsc(projetoId)
                : historiaRepository.findByProjetoIdAndPrioridadeOrderByTituloAsc(projetoId, prioridade);

        // as contagens de cenários e sugestões são feitas no controller,
        // então as coleções precisam estar carregadas antes de sair da transação
        for (HistoriaUsuario h : historias) {
            Hibernate.initialize(h.getEtapaAtual());
            Hibernate.initialize(h.getAutor());
            Hibernate.initialize(h.getCenarios());
            Hibernate.initialize(h.getSugestoes());
        }
        return historias;
    }

    @Transactional
    public HistoriaUsuario atualizar(Long id, Long usuarioId, HistoriaUsuario dados) {
        HistoriaUsuario historia = buscarParaAlterar(id, usuarioId);
        validarNarrativa(dados);

        Long projetoId = historia.getProjeto().getId();
        if (historiaRepository.existsByProjetoIdAndTituloIgnoreCaseAndIdNot(projetoId, dados.getTitulo(), id)) {
            throw new ConflitoException(tituloRepetido(dados.getTitulo()));
        }

        historia.atualizarDados(dados);
        // flush para o @PreUpdate rodar antes de montar a resposta (atualizadaEm)
        return carregarDetalhes(historiaRepository.saveAndFlush(historia));
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        HistoriaUsuario historia = buscarParaAlterar(id, usuarioId);
        // cenários, passos e sugestões vão junto pelo cascade
        historiaRepository.delete(historia);
    }

    @Transactional(readOnly = true)
    public HistoriaUsuario buscar(Long id) {
        return historiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História " + id + " não encontrada"));
    }

    // regra 3: somente o autor edita ou exclui a história e seus cenários
    @Transactional
    public HistoriaUsuario buscarParaAlterar(Long historiaId, Long usuarioId) {
        usuarioService.buscar(usuarioId);
        HistoriaUsuario historia = buscar(historiaId);
        if (!historia.isAutor(usuarioId)) {
            throw new AcessoNegadoException("Somente o autor da história pode alterá-la ou excluí-la");
        }
        // bônus: com fluxo ativo, só edita em etapa que permite edição (e nunca na final)
        EtapaFluxo etapa = historia.getEtapaAtual();
        if (historia.getProjeto().usaFluxo() && etapa != null && (!etapa.isPermiteEdicao() || etapa.isFinal())) {
            throw new RegraNegocioException("A história está na etapa \"" + etapa.getNome()
                    + "\", que não permite edição");
        }
        return historia;
    }

    // regra 1: papel, objetivo e benefício obrigatórios
    private void validarNarrativa(HistoriaUsuario h) {
        obrigatorio(h.getTitulo(), "Informe o título da história");
        obrigatorio(h.getPapel(), "Informe o papel da história (Como...)");
        obrigatorio(h.getObjetivo(), "Informe o objetivo da história (quero...)");
        obrigatorio(h.getBeneficio(), "Informe o benefício da história (para...)");
        if (h.getPrioridade() == null) {
            throw new RegraNegocioException("Informe a prioridade da história");
        }
    }

    private void obrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(mensagem);
        }
    }

    private String tituloRepetido(String titulo) {
        return "Já existe uma história com o título \"" + titulo + "\" neste projeto";
    }

    // com open-in-view desligado, o que o controller vai ler precisa ser carregado aqui
    private HistoriaUsuario carregarDetalhes(HistoriaUsuario h) {
        Hibernate.initialize(h.getProjeto());
        Hibernate.initialize(h.getProjeto().getFluxo());
        Hibernate.initialize(h.getEtapaAtual());
        Hibernate.initialize(h.getAutor());
        for (Cenario c : h.getCenarios()) {
            Hibernate.initialize(c.getPassos());
        }
        Hibernate.initialize(h.getSugestoes());
        return h;
    }
}
