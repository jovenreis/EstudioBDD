package com.estudiobdd.dto;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.OrigemCenario;

import java.util.List;

public record CenarioResponse(Long id, String titulo, int posicao, OrigemCenario origem,
                              List<PassoResponse> passos) {

    public static CenarioResponse de(Cenario c) {
        return new CenarioResponse(c.getId(), c.getTitulo(), c.getPosicao(), c.getOrigem(),
                c.getPassos().stream().map(PassoResponse::de).toList());
    }
}
