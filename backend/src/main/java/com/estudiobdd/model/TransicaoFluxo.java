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
import jakarta.persistence.Table;

/**
 * Uma ação possível no fluxo (ex.: "Aprovar"), de uma etapa para outra.
 * As condições são colunas: o serviço lê esses valores em vez de ter um if por etapa.
 */
@Entity
@Table(name = "transicao_fluxo")
public class TransicaoFluxo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_acao", nullable = false, length = 80)
    private String nomeAcao;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel_permitido", nullable = false, length = 10)
    private Papel papelPermitido;

    @Column(name = "exige_comentario", nullable = false)
    private boolean exigeComentario;

    @Column(name = "exige_usuario_diferente_autor", nullable = false)
    private boolean exigeUsuarioDiferenteDoAutor;

    @Column(name = "exige_cenarios", nullable = false)
    private boolean exigeCenarios;

    @Column(name = "exige_sem_sugestao_pendente", nullable = false)
    private boolean exigeSemSugestaoPendente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fluxo_id", nullable = false)
    private Fluxo fluxo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origem_id", nullable = false)
    private EtapaFluxo origem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destino_id", nullable = false)
    private EtapaFluxo destino;

    protected TransicaoFluxo() {
    }

    public TransicaoFluxo(Fluxo fluxo, String nomeAcao, EtapaFluxo origem, EtapaFluxo destino, Papel papelPermitido) {
        this.fluxo = fluxo;
        this.nomeAcao = nomeAcao;
        this.origem = origem;
        this.destino = destino;
        this.papelPermitido = papelPermitido;
    }

    public TransicaoFluxo exigindo(boolean comentario, boolean usuarioDiferenteDoAutor,
                                   boolean cenarios, boolean semSugestaoPendente) {
        this.exigeComentario = comentario;
        this.exigeUsuarioDiferenteDoAutor = usuarioDiferenteDoAutor;
        this.exigeCenarios = cenarios;
        this.exigeSemSugestaoPendente = semSugestaoPendente;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getNomeAcao() {
        return nomeAcao;
    }

    public Papel getPapelPermitido() {
        return papelPermitido;
    }

    public boolean isExigeComentario() {
        return exigeComentario;
    }

    public boolean isExigeUsuarioDiferenteDoAutor() {
        return exigeUsuarioDiferenteDoAutor;
    }

    public boolean isExigeCenarios() {
        return exigeCenarios;
    }

    public boolean isExigeSemSugestaoPendente() {
        return exigeSemSugestaoPendente;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public EtapaFluxo getOrigem() {
        return origem;
    }

    public EtapaFluxo getDestino() {
        return destino;
    }
}
