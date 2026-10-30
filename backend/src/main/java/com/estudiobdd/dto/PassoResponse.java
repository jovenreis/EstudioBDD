package com.estudiobdd.dto;

import com.estudiobdd.model.Passo;
import com.estudiobdd.model.TipoPasso;

public record PassoResponse(Long id, TipoPasso tipo, String texto, int posicao) {

    public static PassoResponse de(Passo p) {
        return new PassoResponse(p.getId(), p.getTipo(), p.getTexto(), p.getPosicao());
    }
}
