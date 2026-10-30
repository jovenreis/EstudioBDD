package com.estudiobdd.service;

// Valor que precisa ser único e já existe. Vira 409.
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
