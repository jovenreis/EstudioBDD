package com.estudiobdd.service;

import com.estudiobdd.model.Projeto;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final HistoriaUsuarioRepository historiaRepository;

    public ProjetoService(ProjetoRepository projetoRepository, HistoriaUsuarioRepository historiaRepository) {
        this.projetoRepository = projetoRepository;
        this.historiaRepository = historiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Projeto> listar() {
        return projetoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Projeto buscar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto " + id + " não encontrado"));
    }

    // regra 1: nome do projeto único
    @Transactional
    public Projeto criar(Projeto novo) {
        if (projetoRepository.existsByNomeIgnoreCase(novo.getNome())) {
            throw new ConflitoException("Já existe um projeto com o nome \"" + novo.getNome() + "\"");
        }
        return projetoRepository.save(novo);
    }

    @Transactional
    public Projeto atualizar(Long id, Projeto dados) {
        Projeto projeto = buscar(id);
        if (projetoRepository.existsByNomeIgnoreCaseAndIdNot(dados.getNome(), id)) {
            throw new ConflitoException("Já existe um projeto com o nome \"" + dados.getNome() + "\"");
        }
        projeto.atualizarDados(dados);
        return projeto;
    }

    @Transactional
    public void excluir(Long id) {
        Projeto projeto = buscar(id);
        if (historiaRepository.existsByProjetoId(id)) {
            throw new RegraNegocioException("Não é possível excluir um projeto que possui histórias");
        }
        projetoRepository.delete(projeto);
    }
}
