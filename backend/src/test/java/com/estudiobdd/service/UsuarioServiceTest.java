package com.estudiobdd.service;

import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioRepository usuarioRepository;
    private UsuarioService service;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        usuarioRepository = mock(UsuarioRepository.class);
        service = new UsuarioService(usuarioRepository, encoder);

        ana = new Usuario("Ana Souza", "ana.souza@exemplo.com");
        ana.setSenhaHash(encoder.encode("senha123"));
        when(usuarioRepository.findByEmailIgnoreCase("ana.souza@exemplo.com")).thenReturn(Optional.of(ana));
        when(usuarioRepository.findByEmailIgnoreCase("ninguem@exemplo.com")).thenReturn(Optional.empty());
    }

    @Test
    void loginComSenhaCorretaDevolveOUsuario() {
        assertThat(service.autenticar("ana.souza@exemplo.com", "senha123")).isSameAs(ana);
    }

    @Test
    void loginComSenhaErradaEhRecusado() {
        assertThatThrownBy(() -> service.autenticar("ana.souza@exemplo.com", "errada"))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha incorretos");
    }

    @Test
    void loginComEmailInexistenteTemAMesmaMensagem() {
        assertThatThrownBy(() -> service.autenticar("ninguem@exemplo.com", "senha123"))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha incorretos");
    }

    @Test
    void senhaEhGuardadaComoHashENaoEmTexto() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario criado = service.criar(new Usuario("Beatriz Rocha", "bia@exemplo.com"), "minhasenha");

        assertThat(criado.getSenhaHash()).isNotEqualTo("minhasenha").startsWith("$2");
        assertThat(encoder.matches("minhasenha", criado.getSenhaHash())).isTrue();
    }
}
