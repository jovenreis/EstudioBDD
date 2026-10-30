package com.estudiobdd.service;

// Recurso (usuário, projeto, história, cenário) que não existe. Vira 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
