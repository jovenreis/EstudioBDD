package com.estudiobdd.service;

import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.Fluxo;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.HistoricoTransicao;
import com.estudiobdd.model.Papel;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.TransicaoFluxo;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.EtapaFluxoRepository;
import com.estudiobdd.repository.FluxoRepository;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.TransicaoFluxoRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Atividade bônus: controle de fluxo configurável.
 * Nenhuma etapa aparece no código. O serviço lê a etapa atual da história e as
 * transições cadastradas no banco, e confere as condições marcadas em cada transição.
 */
@Service
public class FluxoService {

    private final FluxoRepository fluxoRepository;
    private final EtapaFluxoRepository etapaRepository;
    private final TransicaoFluxoRepository transicaoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final HistoriaService historiaService;
    private final UsuarioService usuarioService;
    private final ProjetoService projetoService;

    public FluxoService(FluxoRepository fluxoRepository,
                        EtapaFluxoRepository etapaRepository,
                        TransicaoFluxoRepository transicaoRepository,
                        HistoriaUsuarioRepository historiaRepository,
                        HistoriaService historiaService,
                        UsuarioService usuarioService,
                        ProjetoService projetoService) {
        this.fluxoRepository = fluxoRepository;
        this.etapaRepository = etapaRepository;
        this.transicaoRepository = transicaoRepository;
        this.historiaRepository = historiaRepository;
        this.historiaService = historiaService;
        this.usuarioService = usuarioService;
        this.projetoService = projetoService;
    }

    // ---------- uso do fluxo ----------

    // ações que o usuário atual pode tentar na etapa em que a história está
    @Transactional(readOnly = true)
    public List<TransicaoFluxo> acoesDisponiveis(Long historiaId, Long usuarioId) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        HistoriaUsuario historia = historiaService.buscar(historiaId);
        if (!historia.getProjeto().usaFluxo() || historia.getEtapaAtual() == null) {
            return List.of();
        }
        List<TransicaoFluxo> acoes = transicaoRepository.findByOrigemIdOrderByIdAsc(historia.getEtapaAtual().getId())
                .stream()
                .filter(t -> t.getPapelPermitido() == usuario.getPapel())
                .toList();
        acoes.forEach(t -> Hibernate.initialize(t.getDestino()));
        return acoes;
    }

    @Transactional
    public HistoriaUsuario executar(Long historiaId, Long transicaoId, Long usuarioId, String comentario) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        HistoriaUsuario historia = historiaService.buscar(historiaId);
        Projeto projeto = historia.getProjeto();
        if (!projeto.usaFluxo()) {
            throw new RegraNegocioException("O projeto desta história não usa fluxo de aprovação");
        }

        TransicaoFluxo transicao = transicaoRepository.findById(transicaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ação " + transicaoId + " não encontrada"));
        EtapaFluxo etapaAtual = historia.getEtapaAtual();

        // 1. a transição precisa existir a partir da etapa atual, no fluxo do projeto
        if (etapaAtual == null
                || !mesmo(transicao.getFluxo(), projeto.getFluxo())
                || !mesmo(transicao.getOrigem(), etapaAtual)) {
            throw new RegraNegocioException("A ação \"" + transicao.getNomeAcao() + "\" não está disponível na etapa \""
                    + (etapaAtual == null ? "-" : etapaAtual.getNome()) + "\"");
        }

        // 2. o usuário precisa ter o papel permitido
        if (usuario.getPapel() != transicao.getPapelPermitido()) {
            throw new AcessoNegadoException("A ação \"" + transicao.getNomeAcao() + "\" só pode ser executada por "
                    + nomePapel(transicao.getPapelPermitido()));
        }

        // 3. condições configuradas na transição
        boolean semComentario = comentario == null || comentario.isBlank();
        if (transicao.isExigeComentario() && semComentario) {
            throw new RegraNegocioException("Informe um comentário para \"" + transicao.getNomeAcao() + "\"");
        }
        if (transicao.isExigeUsuarioDiferenteDoAutor() && historia.isAutor(usuario.getId())) {
            throw new RegraNegocioException("A ação \"" + transicao.getNomeAcao()
                    + "\" precisa ser feita por um usuário diferente do autor da história");
        }
        if (transicao.isExigeCenarios() && historia.getCenarios().isEmpty()) {
            throw new RegraNegocioException("A história precisa ter ao menos um cenário para \""
                    + transicao.getNomeAcao() + "\"");
        }
        if (transicao.isExigeSemSugestaoPendente() && historia.quantidadeSugestoesPendentes() > 0) {
            throw new RegraNegocioException("Aceite ou rejeite as sugestões pendentes antes de \""
                    + transicao.getNomeAcao() + "\"");
        }

        historia.registrarTransicao(transicao, usuario, semComentario ? null : comentario.trim());
        return historiaService.buscarDetalhada(historia.getId());
    }

    @Transactional(readOnly = true)
    public List<HistoricoTransicao> historico(Long historiaId) {
        HistoriaUsuario historia = historiaService.buscar(historiaId);
        for (HistoricoTransicao h : historia.getHistorico()) {
            Hibernate.initialize(h.getUsuario());
            Hibernate.initialize(h.getTransicao().getOrigem());
            Hibernate.initialize(h.getTransicao().getDestino());
        }
        return historia.getHistorico();
    }

    public record Quadro(Projeto projeto, Fluxo fluxo, List<EtapaFluxo> etapas,
                         Map<Long, List<HistoriaUsuario>> historiasPorEtapa) {
    }

    // uma coluna por etapa cadastrada, com as histórias que estão nela
    @Transactional(readOnly = true)
    public Quadro quadro(Long projetoId) {
        Projeto projeto = projetoService.buscar(projetoId);
        if (!projeto.usaFluxo()) {
            throw new RegraNegocioException("O projeto \"" + projeto.getNome() + "\" não usa fluxo de aprovação");
        }
        Fluxo fluxo = projeto.getFluxo();
        List<EtapaFluxo> etapas = etapaRepository.findByFluxoIdOrderByPosicaoAsc(fluxo.getId());

        Map<Long, List<HistoriaUsuario>> porEtapa = new LinkedHashMap<>();
        etapas.forEach(e -> porEtapa.put(e.getId(), new ArrayList<>()));
        for (HistoriaUsuario h : historiaRepository.findByProjetoIdOrderByTituloAsc(projetoId)) {
            Hibernate.initialize(h.getAutor());
            Hibernate.initialize(h.getCenarios());
            Hibernate.initialize(h.getSugestoes());
            EtapaFluxo etapa = h.getEtapaAtual();
            if (etapa != null && porEtapa.containsKey(etapa.getId())) {
                porEtapa.get(etapa.getId()).add(h);
            }
        }
        return new Quadro(projeto, fluxo, etapas, porEtapa);
    }

    // ---------- configuração (etapas e transições são dados) ----------

    @Transactional(readOnly = true)
    public List<Fluxo> listar() {
        return fluxoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Fluxo buscarDetalhado(Long fluxoId) {
        Fluxo fluxo = buscar(fluxoId);
        Hibernate.initialize(fluxo.getEtapas());
        for (TransicaoFluxo t : fluxo.getTransicoes()) {
            Hibernate.initialize(t.getOrigem());
            Hibernate.initialize(t.getDestino());
        }
        return fluxo;
    }

    @Transactional
    public EtapaFluxo criarEtapa(Long fluxoId, String nome, int posicao, boolean inicial, boolean fim,
                                 boolean permiteEdicao) {
        Fluxo fluxo = buscar(fluxoId);
        validarEtapa(fluxo, null, nome, inicial);
        return etapaRepository.save(new EtapaFluxo(fluxo, nome.trim(), posicao, inicial, fim, permiteEdicao));
    }

    @Transactional
    public EtapaFluxo atualizarEtapa(Long fluxoId, Long etapaId, String nome, int posicao, boolean inicial,
                                     boolean fim, boolean permiteEdicao) {
        Fluxo fluxo = buscar(fluxoId);
        EtapaFluxo etapa = buscarEtapa(fluxo, etapaId);
        validarEtapa(fluxo, etapaId, nome, inicial);
        etapa.atualizar(nome.trim(), posicao, inicial, fim, permiteEdicao);
        return etapa;
    }

    @Transactional
    public TransicaoFluxo criarTransicao(Long fluxoId, String nomeAcao, Long origemId, Long destinoId, Papel papel,
                                         boolean exigeComentario, boolean exigeUsuarioDiferenteDoAutor,
                                         boolean exigeCenarios, boolean exigeSemSugestaoPendente) {
        Fluxo fluxo = buscar(fluxoId);
        EtapaFluxo origem = buscarEtapa(fluxo, origemId);
        EtapaFluxo destino = buscarEtapa(fluxo, destinoId);
        if (mesmo(origem, destino)) {
            throw new RegraNegocioException("A etapa de origem e a de destino precisam ser diferentes");
        }
        TransicaoFluxo transicao = new TransicaoFluxo(fluxo, nomeAcao.trim(), origem, destino, papel)
                .exigindo(exigeComentario, exigeUsuarioDiferenteDoAutor, exigeCenarios, exigeSemSugestaoPendente);
        return transicaoRepository.save(transicao);
    }

    // liga (ou desliga, com fluxoId nulo) o fluxo de um projeto;
    // histórias sem etapa passam a começar na etapa inicial
    @Transactional
    public Projeto definirFluxoDoProjeto(Long projetoId, Long fluxoId) {
        Projeto projeto = projetoService.buscar(projetoId);
        if (fluxoId == null) {
            projeto.setFluxo(null);
            return projeto;
        }
        Fluxo fluxo = buscar(fluxoId);
        EtapaFluxo inicial = fluxo.etapaInicial()
                .orElseThrow(() -> new RegraNegocioException("O fluxo \"" + fluxo.getNome() + "\" não tem etapa inicial"));
        projeto.setFluxo(fluxo);
        for (HistoriaUsuario h : historiaRepository.findByProjetoIdOrderByTituloAsc(projetoId)) {
            if (h.getEtapaAtual() == null || !mesmo(h.getEtapaAtual().getFluxo(), fluxo)) {
                h.setEtapaAtual(inicial);
            }
        }
        return projeto;
    }

    private Fluxo buscar(Long fluxoId) {
        return fluxoRepository.findById(fluxoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fluxo " + fluxoId + " não encontrado"));
    }

    private EtapaFluxo buscarEtapa(Fluxo fluxo, Long etapaId) {
        EtapaFluxo etapa = etapaRepository.findById(etapaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etapa " + etapaId + " não encontrada"));
        if (!mesmo(etapa.getFluxo(), fluxo)) {
            throw new RegraNegocioException("A etapa \"" + etapa.getNome() + "\" não pertence a este fluxo");
        }
        return etapa;
    }

    private void validarEtapa(Fluxo fluxo, Long etapaId, String nome, boolean inicial) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome da etapa");
        }
        boolean repetido = etapaId == null
                ? etapaRepository.existsByFluxoIdAndNomeIgnoreCase(fluxo.getId(), nome.trim())
                : etapaRepository.existsByFluxoIdAndNomeIgnoreCaseAndIdNot(fluxo.getId(), nome.trim(), etapaId);
        if (repetido) {
            throw new ConflitoException("Já existe uma etapa \"" + nome.trim() + "\" neste fluxo");
        }
        if (inicial) {
            fluxo.etapaInicial()
                    .filter(e -> !Objects.equals(e.getId(), etapaId))
                    .ifPresent(e -> {
                        throw new RegraNegocioException("O fluxo já tem uma etapa inicial (\"" + e.getNome() + "\")");
                    });
        }
    }

    // compara entidades pelo id (com o Hibernate, a mesma linha pode vir como objetos diferentes)
    private static boolean mesmo(Object a, Object b) {
        if (a == b) {
            return true;
        }
        Long idA = idDe(a);
        Long idB = idDe(b);
        return idA != null && idA.equals(idB);
    }

    private static Long idDe(Object entidade) {
        if (entidade instanceof Fluxo f) {
            return f.getId();
        }
        if (entidade instanceof EtapaFluxo e) {
            return e.getId();
        }
        return null;
    }

    private static String nomePapel(Papel papel) {
        return papel == Papel.REVISOR ? "um revisor" : "um analista";
    }
}
