package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.Fluxo;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.Papel;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.TipoPasso;
import com.estudiobdd.model.TransicaoFluxo;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.EtapaFluxoRepository;
import com.estudiobdd.repository.FluxoRepository;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.TransicaoFluxoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

// Atividade bônus: as regras do fluxo vêm dos dados das transições, não de ifs por etapa
class FluxoServiceTest {

    private FluxoService service;
    private HistoriaUsuarioRepository historiaRepository;
    private HistoriaUsuario historia;
    private EtapaFluxo rascunho;
    private EtapaFluxo revisaoHumana;
    private EtapaFluxo aprovada;

    private static final long ENVIAR = 10L;
    private static final long APROVAR = 11L;
    private static final long DEVOLVER = 12L;

    @BeforeEach
    void preparar() {
        Fluxo fluxo = new Fluxo("Aprovação de histórias", null);
        setField(fluxo, "id", 1L);
        rascunho = etapa(fluxo, 1L, "Rascunho", true, false, true);
        revisaoHumana = etapa(fluxo, 3L, "Revisão humana", false, false, false);
        aprovada = etapa(fluxo, 4L, "Aprovada", false, true, false);

        TransicaoFluxo enviar = transicao(fluxo, ENVIAR, "Enviar para revisão", rascunho, revisaoHumana, Papel.ANALISTA)
                .exigindo(false, false, true, true);
        TransicaoFluxo aprovar = transicao(fluxo, APROVAR, "Aprovar", revisaoHumana, aprovada, Papel.REVISOR)
                .exigindo(false, true, false, false);
        TransicaoFluxo devolver = transicao(fluxo, DEVOLVER, "Devolver", revisaoHumana, rascunho, Papel.REVISOR)
                .exigindo(true, false, false, false);

        Usuario ana = usuario(1L, "Ana Souza", Papel.ANALISTA);
        Usuario beatriz = usuario(3L, "Beatriz Rocha", Papel.REVISOR);

        Projeto projeto = new Projeto("Estúdio BDD", null, null);
        projeto.setFluxo(fluxo);
        historia = new HistoriaUsuario(projeto, ana, "Exportar feature", "analista",
                "exportar a história", "usar nos testes", Prioridade.BAIXA);
        setField(historia, "id", 1L);
        Cenario cenario = new Cenario("Exportação com sucesso", OrigemCenario.HUMANO);
        cenario.adicionarPasso(TipoPasso.QUANDO, "o analista exporta");
        cenario.adicionarPasso(TipoPasso.ENTAO, "o arquivo é gerado");
        historia.adicionarCenario(cenario);

        HistoriaService historiaService = mock(HistoriaService.class);
        UsuarioService usuarioService = mock(UsuarioService.class);
        TransicaoFluxoRepository transicaoRepository = mock(TransicaoFluxoRepository.class);
        historiaRepository = mock(HistoriaUsuarioRepository.class);
        when(historiaService.buscar(1L)).thenReturn(historia);
        when(usuarioService.buscar(1L)).thenReturn(ana);
        when(usuarioService.buscar(3L)).thenReturn(beatriz);
        when(transicaoRepository.findById(ENVIAR)).thenReturn(Optional.of(enviar));
        when(transicaoRepository.findById(APROVAR)).thenReturn(Optional.of(aprovar));
        when(transicaoRepository.findById(DEVOLVER)).thenReturn(Optional.of(devolver));

        service = new FluxoService(mock(FluxoRepository.class), mock(EtapaFluxoRepository.class), transicaoRepository,
                historiaRepository, historiaService, usuarioService, mock(ProjetoService.class));
    }

    @Test
    void transicaoQueNaoSaiDaEtapaAtualEhRejeitada() {
        historia.setEtapaAtual(rascunho);

        assertThatThrownBy(() -> service.executar(1L, APROVAR, 3L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não está disponível na etapa \"Rascunho\"");
        assertThat(historia.getEtapaAtual()).isSameAs(rascunho);
    }

    @Test
    void aprovacaoPeloProprioAutorEhBloqueada() {
        Usuario beatriz = usuario(3L, "Beatriz Rocha", Papel.REVISOR);
        setField(historia, "autor", beatriz);
        historia.setEtapaAtual(revisaoHumana);

        assertThatThrownBy(() -> service.executar(1L, APROVAR, 3L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("diferente do autor");
        assertThat(historia.getEtapaAtual()).isSameAs(revisaoHumana);
        assertThat(historia.getHistorico()).isEmpty();
    }

    @Test
    void usuarioSemOPapelPermitidoEhBloqueado() {
        historia.setEtapaAtual(revisaoHumana);

        assertThatThrownBy(() -> service.executar(1L, APROVAR, 1L, null))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessageContaining("revisor");
    }

    @Test
    void devolverSemComentarioEhBloqueado() {
        historia.setEtapaAtual(revisaoHumana);

        assertThatThrownBy(() -> service.executar(1L, DEVOLVER, 3L, "  "))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("comentário");
    }

    @Test
    void enviarComSugestaoPendenteEhBloqueado() {
        historia.setEtapaAtual(rascunho);
        historia.adicionarSugestao(new Sugestao(historia.getAutor(), "ChatGPT", "prompt", "conteúdo"));

        assertThatThrownBy(() -> service.executar(1L, ENVIAR, 1L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("pendentes");
    }

    @Test
    void transicaoValidaMudaAEtapaERegistraHistorico() {
        historia.setEtapaAtual(revisaoHumana);

        service.executar(1L, APROVAR, 3L, "ok, aprovada");

        assertThat(historia.getEtapaAtual()).isSameAs(aprovada);
        assertThat(historia.getHistorico()).hasSize(1);
        assertThat(historia.getHistorico().get(0).getUsuario().getNome()).isEqualTo("Beatriz Rocha");
        assertThat(historia.getHistorico().get(0).getTransicao().getNomeAcao()).isEqualTo("Aprovar");
        assertThat(historia.getHistorico().get(0).getComentario()).isEqualTo("ok, aprovada");
    }

    @Test
    void historiaEmEtapaSemEdicaoNaoPodeSerAlterada() {
        HistoriaService historiaService = new HistoriaService(historiaRepository, mock(ProjetoService.class),
                mock(UsuarioService.class));
        when(historiaRepository.findById(1L)).thenReturn(Optional.of(historia));
        historia.setEtapaAtual(aprovada);

        assertThatThrownBy(() -> historiaService.buscarParaAlterar(1L, 1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não permite edição");
    }

    private EtapaFluxo etapa(Fluxo fluxo, Long id, String nome, boolean inicial, boolean fim, boolean edicao) {
        EtapaFluxo e = new EtapaFluxo(fluxo, nome, id.intValue(), inicial, fim, edicao);
        setField(e, "id", id);
        fluxo.getEtapas().add(e);
        return e;
    }

    private TransicaoFluxo transicao(Fluxo fluxo, long id, String nome, EtapaFluxo origem, EtapaFluxo destino,
                                     Papel papel) {
        TransicaoFluxo t = new TransicaoFluxo(fluxo, nome, origem, destino, papel);
        setField(t, "id", id);
        return t;
    }

    private Usuario usuario(Long id, String nome, Papel papel) {
        Usuario u = new Usuario(nome, nome.toLowerCase().replace(' ', '.') + "@exemplo.com");
        setField(u, "id", id);
        u.setPapel(papel);
        return u;
    }
}
