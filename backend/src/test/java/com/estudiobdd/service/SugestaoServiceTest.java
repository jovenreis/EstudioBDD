package com.estudiobdd.service;

import com.estudiobdd.model.DecisaoSugestao;
import com.estudiobdd.model.MotivoRejeicao;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.SugestaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SugestaoServiceTest {

    private SugestaoRepository sugestaoRepository;
    private SugestaoService service;
    private Sugestao sugestao;

    @BeforeEach
    void preparar() {
        sugestaoRepository = mock(SugestaoRepository.class);
        HistoriaService historiaService = mock(HistoriaService.class);
        UsuarioService usuarioService = mock(UsuarioService.class);
        service = new SugestaoService(sugestaoRepository, historiaService, usuarioService);

        Usuario ana = new Usuario("Ana Souza", "ana.souza@exemplo.com");
        sugestao = new Sugestao(ana, "ChatGPT", "Sugira cenários de borda", "Cenário: conta bloqueada");

        when(usuarioService.buscar(1L)).thenReturn(ana);
        when(sugestaoRepository.findById(10L)).thenReturn(Optional.of(sugestao));
    }

    @Test
    void rejeicaoSemMotivoEhBloqueada() {
        assertThatThrownBy(() -> service.rejeitar(10L, 1L, null, "não serve"))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("motivo");

        assertThat(sugestao.getDecisao()).isEqualTo(DecisaoSugestao.PENDENTE);
        assertThat(sugestao.getDecididaEm()).isNull();
    }

    @Test
    void rejeicaoComMotivoRegistraADecisao() {
        service.rejeitar(10L, 1L, MotivoRejeicao.GENERICA, "não descreve um caso concreto");

        assertThat(sugestao.getDecisao()).isEqualTo(DecisaoSugestao.REJEITADA);
        assertThat(sugestao.getMotivoRejeicao()).isEqualTo(MotivoRejeicao.GENERICA);
        assertThat(sugestao.getDecididaEm()).isNotNull();
    }

    @Test
    void aceitarNaoExigeMotivo() {
        service.aceitar(10L, 1L, null);

        assertThat(sugestao.getDecisao()).isEqualTo(DecisaoSugestao.ACEITA);
        assertThat(sugestao.getMotivoRejeicao()).isNull();
    }

    @Test
    void sugestaoJaDecididaNaoPodeSerAlterada() {
        service.aceitar(10L, 1L, null);

        assertThatThrownBy(() -> service.rejeitar(10L, 1L, MotivoRejeicao.INCORRETA, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pode ser alterada");
        assertThat(sugestao.getDecisao()).isEqualTo(DecisaoSugestao.ACEITA);
    }
}
