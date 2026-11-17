package com.estudiobdd.dto;

import jakarta.validation.constraints.Size;

// o comentário é opcional aqui; quando a transição exige, quem recusa é o FluxoService
public record ExecutarAcaoRequest(@Size(max = 1000) String comentario) {
}
