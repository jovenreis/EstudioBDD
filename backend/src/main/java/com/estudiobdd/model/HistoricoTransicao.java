package com.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Registro de cada transição executada: qual história, qual ação, quem e quando
@Entity
@Table(name = "historico_transicao")
public class HistoricoTransicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Column(length = 1000)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaUsuario historia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transicao_id", nullable = false)
    private TransicaoFluxo transicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected HistoricoTransicao() {
    }

    HistoricoTransicao(HistoriaUsuario historia, TransicaoFluxo transicao, Usuario usuario, String comentario) {
        this.historia = historia;
        this.transicao = transicao;
        this.usuario = usuario;
        this.comentario = comentario;
        this.dataHora = LocalDateTime.now();
    }

    @PrePersist
    void aoRegistrar() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getComentario() {
        return comentario;
    }

    public HistoriaUsuario getHistoria() {
        return historia;
    }

    public TransicaoFluxo getTransicao() {
        return transicao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
