package com.estudiobdd.dto;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.OrigemCenario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// os passos vêm na ordem em que devem aparecer no cenário
public record CenarioRequest(
        @NotBlank(message = "Informe o título do cenário") @Size(max = 200) String titulo,
        OrigemCenario origem,
        @NotEmpty(message = "Informe ao menos um passo") List<@Valid PassoRequest> passos) {

    public Cenario paraDominio() {
        Cenario cenario = new Cenario(titulo.trim(), origem);
        for (PassoRequest p : passos) {
            cenario.adicionarPasso(p.tipo(), p.texto().trim());
        }
        return cenario;
    }
}
