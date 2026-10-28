package com.estudiobdd.config;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.TipoPasso;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.ProjetoRepository;
import com.estudiobdd.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carrega dados fictícios para demonstração. Só roda quando o banco está vazio,
 * então reiniciar a aplicação não duplica nada.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final HistoriaUsuarioRepository historiaRepository;

    public DadosIniciais(UsuarioRepository usuarioRepository,
                         ProjetoRepository projetoRepository,
                         HistoriaUsuarioRepository historiaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.historiaRepository = historiaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario ana = usuarioRepository.save(new Usuario("Ana Souza", "ana.souza@exemplo.com"));
        Usuario carlos = usuarioRepository.save(new Usuario("Carlos Lima", "carlos.lima@exemplo.com"));
        usuarioRepository.save(new Usuario("Beatriz Rocha", "beatriz.rocha@exemplo.com"));

        Projeto loja = projetoRepository.save(new Projeto(
                "Loja",
                "Loja virtual de artigos esportivos",
                "Cliente: pessoa cadastrada que compra na loja. Carrinho: itens escolhidos antes do pagamento."));

        Projeto estudio = projetoRepository.save(new Projeto(
                "Estúdio BDD",
                "Ferramenta para documentar requisitos como cenários BDD",
                "História: necessidade descrita em Como/Quero/Para. Cenário: exemplo concreto em Dado/Quando/Então."));

        HistoriaUsuario login = new HistoriaUsuario(loja, ana, "Login",
                "cliente", "entrar na loja com e-mail e senha", "acompanhar meus pedidos", Prioridade.ALTA);

        Cenario loginOk = new Cenario("Login com dados corretos", OrigemCenario.HUMANO);
        loginOk.adicionarPasso(TipoPasso.DADO, "o cliente \"joao@exemplo.com\" cadastrado com a senha \"abc123\"");
        loginOk.adicionarPasso(TipoPasso.QUANDO, "ele entra com \"joao@exemplo.com\" e \"abc123\"");
        loginOk.adicionarPasso(TipoPasso.ENTAO, "o acesso é liberado");
        login.adicionarCenario(loginOk);

        Cenario loginErro = new Cenario("Login com senha errada é recusado", OrigemCenario.HUMANO);
        loginErro.adicionarPasso(TipoPasso.DADO, "o cliente \"joao@exemplo.com\" cadastrado com a senha \"abc123\"");
        loginErro.adicionarPasso(TipoPasso.QUANDO, "ele entra com \"joao@exemplo.com\" e \"errada\"");
        loginErro.adicionarPasso(TipoPasso.ENTAO, "o acesso é negado");
        loginErro.adicionarPasso(TipoPasso.E, "a mensagem informa que o e-mail ou a senha estão incorretos");
        login.adicionarCenario(loginErro);

        login.adicionarSugestao(new Sugestao(ana, "ChatGPT",
                "Sugira cenários de borda para esta história de login",
                "Cenário: conta bloqueada após 5 tentativas erradas"));
        historiaRepository.save(login);

        HistoriaUsuario carrinho = new HistoriaUsuario(loja, carlos, "Adicionar ao carrinho",
                "cliente", "adicionar um produto ao carrinho", "comprar vários itens de uma vez", Prioridade.MEDIA);
        Cenario addOk = new Cenario("Produto com estoque entra no carrinho", OrigemCenario.HUMANO);
        addOk.adicionarPasso(TipoPasso.DADO, "o produto \"Bola de futebol\" com 3 unidades em estoque");
        addOk.adicionarPasso(TipoPasso.QUANDO, "o cliente adiciona 1 unidade ao carrinho");
        addOk.adicionarPasso(TipoPasso.ENTAO, "o carrinho tem 1 item");
        carrinho.adicionarCenario(addOk);
        historiaRepository.save(carrinho);

        HistoriaUsuario exportar = new HistoriaUsuario(estudio, ana, "Exportar feature",
                "analista", "exportar uma história como arquivo .feature", "usar os cenários em testes automatizados",
                Prioridade.BAIXA);
        historiaRepository.save(exportar);

        log.info("Dados de demonstração carregados: 3 usuários, 2 projetos, 3 histórias.");
    }
}
