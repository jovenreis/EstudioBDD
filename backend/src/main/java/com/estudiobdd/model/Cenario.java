package com.estudiobdd.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cenario",
        uniqueConstraints = @UniqueConstraint(name = "uk_cenario_titulo_historia",
                columnNames = {"historia_id", "titulo"}))
public class Cenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false)
    private int posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private OrigemCenario origem = OrigemCenario.HUMANO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaUsuario historia;

    // composição: os passos não existem sem o cenário (orphanRemoval apaga junto)
    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicao ASC")
    private List<Passo> passos = new ArrayList<>();

    protected Cenario() {
    }

    public Cenario(String titulo, OrigemCenario origem) {
        this.titulo = titulo;
        this.origem = origem;
    }

    public void adicionarPasso(TipoPasso tipo, String texto) {
        Passo passo = new Passo(tipo, texto, passos.size() + 1);
        passo.setCenario(this);
        passos.add(passo);
    }

    // usado na edição: troca todos os passos e renumera as posições
    public void substituirPassos(List<Passo> novos) {
        passos.clear();
        int pos = 1;
        for (Passo p : novos) {
            p.setPosicao(pos++);
            p.setCenario(this);
            passos.add(p);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public OrigemCenario getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemCenario origem) {
        this.origem = origem;
    }

    public HistoriaUsuario getHistoria() {
        return historia;
    }

    void setHistoria(HistoriaUsuario historia) {
        this.historia = historia;
    }

    public List<Passo> getPassos() {
        return passos;
    }
}
