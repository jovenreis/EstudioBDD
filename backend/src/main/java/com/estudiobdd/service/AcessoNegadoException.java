package com.estudiobdd.service;

// Usuário sem permissão para a operação (regra 3). Vira 403.
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
