package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.TipoPasso;
import org.junit.jupiter.api.Test;

import static com.estudiobdd.model.TipoPasso.DADO;
import static com.estudiobdd.model.TipoPasso.E;
import static com.estudiobdd.model.TipoPasso.ENTAO;
import static com.estudiobdd.model.TipoPasso.MAS;
import static com.estudiobdd.model.TipoPasso.QUANDO;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorCenarioTest {

    private final ValidadorCenario validador = new ValidadorCenario();

    private Cenario cenario(TipoPasso... tipos) {
        Cenario c = new Cenario("Cenário de teste", OrigemCenario.HUMANO);
        for (TipoPasso t : tipos) {
            c.adicionarPasso(t, "texto do passo " + t.getPalavraChave());
        }
        return c;
    }

    @Test
    void cenarioCompletoNaOrdemEhAceito() {
        assertThatCode(() -> validador.validarPassos(cenario(DADO, QUANDO, ENTAO, E).getPassos()))
                .doesNotThrowAnyException();
    }

    @Test
    void cenarioSemDadoEhAceito() {
        assertThatCode(() -> validador.validarPassos(cenario(QUANDO, ENTAO).getPassos()))
                .doesNotThrowAnyException();
    }

    @Test
    void eEMasContinuamOTipoAnterior() {
        assertThatCode(() -> validador.validarPassos(cenario(DADO, E, QUANDO, ENTAO, MAS).getPassos()))
                .doesNotThrowAnyException();
    }

    @Test
    void cenarioSemPassoEntaoEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(DADO, QUANDO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Então");
    }

    @Test
    void cenarioSemPassoQuandoEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(DADO, ENTAO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Quando");
    }

    @Test
    void cenarioIniciadoPorEEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(E, QUANDO, ENTAO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("começar");
    }

    @Test
    void cenarioIniciadoPorMasEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(MAS, QUANDO, ENTAO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("começar");
    }

    @Test
    void entaoAntesDeQuandoEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(DADO, ENTAO, QUANDO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("fora de ordem");
    }

    @Test
    void dadoDepoisDeQuandoEhRejeitado() {
        assertThatThrownBy(() -> validador.validarPassos(cenario(QUANDO, DADO, ENTAO).getPassos()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("fora de ordem");
    }
}
