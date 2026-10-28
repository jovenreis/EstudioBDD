package com.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sugestao")
public class Sugestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nome do assistente de IA usado (ex.: ChatGPT, Claude, Copilot)
    @Column(nullable = false, length = 80)
    private String ferramenta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DecisaoSugestao decisao = DecisaoSugestao.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_rejeicao", length = 20)
    private MotivoRejeicao motivoRejeicao;

    @Column(length = 1000)
    private String justificativa;

    @Column(name = "registrada_em", nullable = false, updatable = false)
    private LocalDateTime registradaEm;

    @Column(name = "decidida_em")
    private LocalDateTime decididaEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaUsuario historia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrada_por_id", nullable = false)
    private Usuario registradaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decidida_por_id")
    private Usuario decididaPor;

    protected Sugestao() {
    }

    public Sugestao(Usuario registradaPor, String ferramenta, String prompt, String conteudo) {
        this.registradaPor = registradaPor;
        this.ferramenta = ferramenta;
        this.prompt = prompt;
        this.conteudo = conteudo;
    }

    @PrePersist
    void aoRegistrar() {
        registradaEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getFerramenta() {
        return ferramenta;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getConteudo() {
        return conteudo;
    }

    public DecisaoSugestao getDecisao() {
        return decisao;
    }

    public void setDecisao(DecisaoSugestao decisao) {
        this.decisao = decisao;
    }

    public MotivoRejeicao getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(MotivoRejeicao motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public LocalDateTime getRegistradaEm() {
        return registradaEm;
    }

    public LocalDateTime getDecididaEm() {
        return decididaEm;
    }

    public void setDecididaEm(LocalDateTime decididaEm) {
        this.decididaEm = decididaEm;
    }

    public HistoriaUsuario getHistoria() {
        return historia;
    }

    void setHistoria(HistoriaUsuario historia) {
        this.historia = historia;
    }

    public Usuario getRegistradaPor() {
        return registradaPor;
    }

    public Usuario getDecididaPor() {
        return decididaPor;
    }

    public void setDecididaPor(Usuario decididaPor) {
        this.decididaPor = decididaPor;
    }
}
