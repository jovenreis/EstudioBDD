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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historia_usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_historia_titulo_projeto",
                columnNames = {"projeto_id", "titulo"}))
public class HistoriaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    // Como...
    @Column(nullable = false, length = 150)
    private String papel;

    // quero...
    @Column(nullable = false, length = 500)
    private String objetivo;

    // para...
    @Column(nullable = false, length = 500)
    private String beneficio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicao ASC")
    private List<Cenario> cenarios = new ArrayList<>();

    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("registradaEm ASC")
    private List<Sugestao> sugestoes = new ArrayList<>();

    protected HistoriaUsuario() {
    }

    public HistoriaUsuario(String titulo, String papel, String objetivo, String beneficio, Prioridade prioridade) {
        this.titulo = titulo;
        this.papel = papel;
        this.objetivo = objetivo;
        this.beneficio = beneficio;
        this.prioridade = prioridade;
    }

    public HistoriaUsuario(Projeto projeto, Usuario autor, String titulo,
                           String papel, String objetivo, String beneficio, Prioridade prioridade) {
        this(titulo, papel, objetivo, beneficio, prioridade);
        this.projeto = projeto;
        this.autor = autor;
    }

    public void vincular(Projeto projeto, Usuario autor) {
        this.projeto = projeto;
        this.autor = autor;
    }

    public void atualizarDados(HistoriaUsuario dados) {
        this.titulo = dados.getTitulo();
        this.papel = dados.getPapel();
        this.objetivo = dados.getObjetivo();
        this.beneficio = dados.getBeneficio();
        this.prioridade = dados.getPrioridade();
    }

    public boolean isAutor(Long usuarioId) {
        return autor != null && autor.getId().equals(usuarioId);
    }

    @PrePersist
    void aoCriar() {
        criadaEm = LocalDateTime.now();
        atualizadaEm = criadaEm;
    }

    @PreUpdate
    void aoAtualizar() {
        atualizadaEm = LocalDateTime.now();
    }

    public void adicionarCenario(Cenario cenario) {
        cenario.setHistoria(this);
        cenario.setPosicao(cenarios.size() + 1);
        cenarios.add(cenario);
    }

    // tira o cenário da lista (orphanRemoval apaga do banco) e renumera os que sobraram
    public void removerCenario(Cenario cenario) {
        cenarios.remove(cenario);
        int pos = 1;
        for (Cenario c : cenarios) {
            c.setPosicao(pos++);
        }
    }

    public void adicionarSugestao(Sugestao sugestao) {
        sugestao.setHistoria(this);
        sugestoes.add(sugestao);
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

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public LocalDateTime getAtualizadaEm() {
        return atualizadaEm;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public List<Cenario> getCenarios() {
        return cenarios;
    }

    public List<Sugestao> getSugestoes() {
        return sugestoes;
    }
}
