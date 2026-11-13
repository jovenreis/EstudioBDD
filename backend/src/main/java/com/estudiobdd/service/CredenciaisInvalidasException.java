package com.estudiobdd.service;

// E-mail ou senha incorretos no login. Vira 401.
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
