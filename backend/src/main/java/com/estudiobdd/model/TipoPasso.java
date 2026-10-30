package com.estudiobdd.model;

public enum TipoPasso {
    DADO("Dado"),
    QUANDO("Quando"),
    ENTAO("Então"),
    E("E"),
    MAS("Mas");

    // palavra usada no Gherkin em português
    private final String palavraChave;

    TipoPasso(String palavraChave) {
        this.palavraChave = palavraChave;
    }

    public String getPalavraChave() {
        return palavraChave;
    }

    public boolean isContinuacao() {
        return this == E || this == MAS;
    }
}
