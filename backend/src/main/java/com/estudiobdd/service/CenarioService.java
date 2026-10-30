package com.estudiobdd.service;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.repository.CenarioRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CenarioService {

    private final CenarioRepository cenarioRepository;
    private final HistoriaService historiaService;
    private final ValidadorCenario validador;

    public CenarioService(CenarioRepository cenarioRepository,
                          HistoriaService historiaService,
                          ValidadorCenario validador) {
        this.cenarioRepository = cenarioRepository;
        this.historiaService = historiaService;
        this.validador = validador;
    }

    @Transactional
    public Cenario criar(Long historiaId, Long usuarioId, Cenario novo) {
        HistoriaUsuario historia = historiaService.buscarParaAlterar(historiaId, usuarioId);
        validar(historia, novo, null);

        historia.adicionarCenario(novo);
        // o cascade salva os passos junto
        return cenarioRepository.save(novo);
    }

    @Transactional(readOnly = true)
    public List<Cenario> listarPorHistoria(Long historiaId) {
        HistoriaUsuario historia = historiaService.buscar(historiaId);
        for (Cenario c : historia.getCenarios()) {
            Hibernate.initialize(c.getPassos());
        }
        return historia.getCenarios();
    }

    @Transactional(readOnly = true)
    public Cenario buscarDetalhado(Long id) {
        Cenario cenario = buscar(id);
        Hibernate.initialize(cenario.getPassos());
        return cenario;
    }

    @Transactional
    public Cenario atualizar(Long id, Long usuarioId, Cenario dados) {
        Cenario cenario = buscar(id);
        HistoriaUsuario historia = historiaService.buscarParaAlterar(cenario.getHistoria().getId(), usuarioId);
        validar(historia, dados, id);

        // troca todos os passos de uma vez e renumera as posições
        cenario.atualizarDados(dados);
        return cenarioRepository.saveAndFlush(cenario);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Cenario cenario = buscar(id);
        HistoriaUsuario historia = historiaService.buscarParaAlterar(cenario.getHistoria().getId(), usuarioId);
        historia.removerCenario(cenario);
    }

    private Cenario buscar(Long id) {
        return cenarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cenário " + id + " não encontrado"));
    }

    // regra 2: título único na história + gramática dos passos
    private void validar(HistoriaUsuario historia, Cenario dados, Long idIgnorado) {
        if (dados.getTitulo() == null || dados.getTitulo().isBlank()) {
            throw new RegraNegocioException("Informe o título do cenário");
        }

        boolean tituloRepetido = historia.getCenarios().stream()
                .filter(c -> !Objects.equals(c.getId(), idIgnorado))
                .anyMatch(c -> c.getTitulo().equalsIgnoreCase(dados.getTitulo()));
        if (tituloRepetido) {
            throw new ConflitoException("Já existe um cenário com o título \"" + dados.getTitulo() + "\" nesta história");
        }

        validador.validarPassos(dados.getPassos());
    }
}
