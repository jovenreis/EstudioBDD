package com.estudiobdd.service;

import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

    // regra 1: e-mail único. A senha é guardada só como hash.
    @Transactional
    public Usuario criar(Usuario novo, String senha) {
        if (usuarioRepository.existsByEmailIgnoreCase(novo.getEmail())) {
            throw new ConflitoException("Já existe um usuário com o e-mail " + novo.getEmail());
        }
        novo.setSenhaHash(passwordEncoder.encode(senha));
        return usuarioRepository.save(novo);
    }

    // login: devolve o usuário se o e-mail existir e a senha conferir com o hash
    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        // mesma mensagem para e-mail inexistente e senha errada,
        // para não revelar quais e-mails estão cadastrados
        CredenciaisInvalidasException invalido = new CredenciaisInvalidasException("E-mail ou senha incorretos");

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> invalido);
        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw invalido;
        }
        return usuario;
    }
}
