package com.estudiobdd.controller;

/**
 * Identidade simulada: o usuário atual vem neste cabeçalho.
 * Não é autenticação, qualquer cliente pode enviar qualquer id.
 */
public final class Cabecalhos {

    public static final String USUARIO = "X-Usuario-Id";

    private Cabecalhos() {
    }
}
