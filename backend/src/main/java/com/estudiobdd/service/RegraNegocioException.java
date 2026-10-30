package com.estudiobdd.service;

// Violação de uma regra de negócio do enunciado. Vira 422.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
