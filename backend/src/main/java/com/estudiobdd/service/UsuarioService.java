package com.estudiobdd.service;

import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado"));
    }

    // regra 1: e-mail único
    @Transactional
    public Usuario criar(Usuario novo) {
        if (usuarioRepository.existsByEmailIgnoreCase(novo.getEmail())) {
            throw new ConflitoException("Já existe um usuário com o e-mail " + novo.getEmail());
        }
        return usuarioRepository.save(novo);
    }
}
