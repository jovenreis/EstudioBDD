package com.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Caminho de uma história até a aprovação. As etapas e as transições ficam
 * cadastradas no banco: mudar o fluxo é mudar dados, não código.
 */
@Entity
@Table(name = "fluxo")
public class Fluxo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "fluxo")
    @OrderBy("posicao ASC")
    private List<EtapaFluxo> etapas = new ArrayList<>();

    @OneToMany(mappedBy = "fluxo")
    private List<TransicaoFluxo> transicoes = new ArrayList<>();

    protected Fluxo() {
    }

    public Fluxo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Optional<EtapaFluxo> etapaInicial() {
        return etapas.stream().filter(EtapaFluxo::isInicial).findFirst();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<EtapaFluxo> getEtapas() {
        return etapas;
    }

    public List<TransicaoFluxo> getTransicoes() {
        return transicoes;
    }
}
