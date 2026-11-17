package com.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "etapa_fluxo",
        uniqueConstraints = @UniqueConstraint(name = "uk_etapa_nome_fluxo", columnNames = {"fluxo_id", "nome"}))
public class EtapaFluxo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    // ordem das colunas no quadro
    @Column(nullable = false)
    private int posicao;

    @Column(nullable = false)
    private boolean inicial;

    @Column(name = "etapa_final", nullable = false)
    private boolean fim;

    @Column(name = "permite_edicao", nullable = false)
    private boolean permiteEdicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fluxo_id", nullable = false)
    private Fluxo fluxo;

    protected EtapaFluxo() {
    }

    public EtapaFluxo(Fluxo fluxo, String nome, int posicao, boolean inicial, boolean fim, boolean permiteEdicao) {
        this.fluxo = fluxo;
        this.nome = nome;
        this.posicao = posicao;
        this.inicial = inicial;
        this.fim = fim;
        this.permiteEdicao = permiteEdicao;
    }

    public void atualizar(String nome, int posicao, boolean inicial, boolean fim, boolean permiteEdicao) {
        this.nome = nome;
        this.posicao = posicao;
        this.inicial = inicial;
        this.fim = fim;
        this.permiteEdicao = permiteEdicao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getPosicao() {
        return posicao;
    }

    public boolean isInicial() {
        return inicial;
    }

    public boolean isFinal() {
        return fim;
    }

    public boolean isPermiteEdicao() {
        return permiteEdicao;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }
}
