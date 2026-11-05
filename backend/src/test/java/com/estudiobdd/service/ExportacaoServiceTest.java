package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.TipoPasso;
import com.estudiobdd.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportacaoServiceTest {

    private ExportacaoService service;
    private HistoriaUsuario historia;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        HistoriaService historiaService = mock(HistoriaService.class);
        service = new ExportacaoService(historiaService);

        ana = new Usuario("Ana Souza", "ana.souza@exemplo.com");
        Projeto loja = new Projeto("Loja", "Loja virtual", null);
        historia = new HistoriaUsuario(loja, ana, "Login", "cliente",
                "entrar na loja com e-mail e senha", "acompanhar meus pedidos", Prioridade.ALTA);

        when(historiaService.buscar(1L)).thenReturn(historia);
    }

    private void adicionarCenarioValido() {
        Cenario cenario = new Cenario("Login com dados corretos", OrigemCenario.HUMANO);
        cenario.adicionarPasso(TipoPasso.DADO, "o cliente \"joao@exemplo.com\" cadastrado");
        cenario.adicionarPasso(TipoPasso.QUANDO, "ele entra com a senha correta");
        cenario.adicionarPasso(TipoPasso.ENTAO, "o acesso é liberado");
        cenario.adicionarPasso(TipoPasso.E, "a página inicial é exibida");
        historia.adicionarCenario(cenario);
    }

    @Test
    void exportacaoBloqueadaComSugestaoPendente() {
        adicionarCenarioValido();
        historia.adicionarSugestao(new Sugestao(ana, "ChatGPT", "Sugira cenários", "Cenário: conta bloqueada"));

        assertThatThrownBy(() -> service.exportar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("pendente");
    }

    @Test
    void exportacaoBloqueadaSemCenarios() {
        assertThatThrownBy(() -> service.exportar(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ao menos um cenário");
    }

    @Test
    void featureGeradoEmGherkinPortugues() {
        adicionarCenarioValido();

        String feature = service.exportar(1L);

        assertThat(feature).isEqualTo("""
                # language: pt
                Funcionalidade: Login
                  Como cliente
                  Quero entrar na loja com e-mail e senha
                  Para acompanhar meus pedidos

                  Cenário: Login com dados corretos
                    Dado o cliente "joao@exemplo.com" cadastrado
                    Quando ele entra com a senha correta
                    Então o acesso é liberado
                    E a página inicial é exibida
                """);
    }

    @Test
    void nomeDoArquivoSemAcentosNemEspacos() {
        assertThat(ExportacaoService.nomeArquivo("Exportar Feature à vista!"))
                .isEqualTo("exportar-feature-a-vista.feature");
    }
}
