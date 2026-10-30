package com.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projeto")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    // glossário e informações do negócio
    @Column(name = "contexto_dominio", columnDefinition = "TEXT")
    private String contextoDominio;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // OneToMany já é LAZY por padrão; as histórias só são carregadas quando acessadas
    @OneToMany(mappedBy = "projeto")
    private List<HistoriaUsuario> historias = new ArrayList<>();

    protected Projeto() {
    }

    public Projeto(String nome, String descricao, String contextoDominio) {
        this.nome = nome;
        this.descricao = descricao;
        this.contextoDominio = contextoDominio;
    }

    @PrePersist
    void aoCriar() {
        dataCriacao = LocalDateTime.now();
    }

    public void atualizarDados(Projeto dados) {
        this.nome = dados.getNome();
        this.descricao = dados.getDescricao();
        this.contextoDominio = dados.getContextoDominio();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getContextoDominio() {
        return contextoDominio;
    }

    public void setContextoDominio(String contextoDominio) {
        this.contextoDominio = contextoDominio;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<HistoriaUsuario> getHistorias() {
        return historias;
    }
}
