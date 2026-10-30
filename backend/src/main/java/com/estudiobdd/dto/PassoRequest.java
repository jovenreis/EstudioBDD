package com.estudiobdd.dto;

import com.estudiobdd.model.TipoPasso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PassoRequest(
        @NotNull(message = "Informe o tipo (DADO, QUANDO, ENTAO, E ou MAS)") TipoPasso tipo,
        @NotBlank(message = "Informe o texto do passo") @Size(max = 500) String texto) {
}
