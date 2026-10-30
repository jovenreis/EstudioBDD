package com.estudiobdd.service;

import com.estudiobdd.model.Passo;
import com.estudiobdd.model.TipoPasso;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Regra 2: valida a gramática do cenário.
 * - ao menos um Quando e um Então;
 * - ordem Dado → Quando → Então (não pode voltar);
 * - E/Mas continuam o tipo do passo anterior e não podem iniciar o cenário.
 */
@Component
public class ValidadorCenario {

    public void validarPassos(List<Passo> passos) {
        if (passos == null || passos.isEmpty()) {
            throw new RegraNegocioException("O cenário precisa ter ao menos um passo");
        }

        for (int i = 0; i < passos.size(); i++) {
            if (passos.get(i).getTipo() == null) {
                throw new RegraNegocioException("Informe o tipo do passo " + (i + 1));
            }
        }

        TipoPasso primeiro = passos.get(0).getTipo();
        if (primeiro.isContinuacao()) {
            throw new RegraNegocioException(
                    "O cenário não pode começar com um passo " + primeiro.getPalavraChave());
        }

        boolean temQuando = false;
        boolean temEntao = false;
        TipoPasso anterior = null;

        for (int i = 0; i < passos.size(); i++) {
            Passo passo = passos.get(i);
            if (passo.getTexto() == null || passo.getTexto().isBlank()) {
                throw new RegraNegocioException("O passo " + (i + 1) + " está sem texto");
            }

            // E e Mas assumem o tipo do passo anterior
            TipoPasso efetivo = passo.getTipo().isContinuacao() ? anterior : passo.getTipo();

            if (anterior != null && ordem(efetivo) < ordem(anterior)) {
                throw new RegraNegocioException("O passo " + (i + 1) + " (" + passo.getTipo().getPalavraChave()
                        + ") está fora de ordem. A sequência deve ser Dado → Quando → Então");
            }

            if (efetivo == TipoPasso.QUANDO) {
                temQuando = true;
            } else if (efetivo == TipoPasso.ENTAO) {
                temEntao = true;
            }
            anterior = efetivo;
        }

        if (!temQuando) {
            throw new RegraNegocioException("O cenário precisa ter ao menos um passo Quando");
        }
        if (!temEntao) {
            throw new RegraNegocioException("O cenário precisa ter ao menos um passo Então");
        }
    }

    private int ordem(TipoPasso tipo) {
        return switch (tipo) {
            case DADO -> 1;
            case QUANDO -> 2;
            case ENTAO -> 3;
            default -> throw new IllegalStateException("Tipo sem ordem: " + tipo);
        };
    }
}
