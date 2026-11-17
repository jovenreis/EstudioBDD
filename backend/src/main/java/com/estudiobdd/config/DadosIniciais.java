package com.estudiobdd.config;

import com.estudiobdd.model.Cenario;
import com.estudiobdd.model.EtapaFluxo;
import com.estudiobdd.model.Fluxo;
import com.estudiobdd.model.HistoriaUsuario;
import com.estudiobdd.model.OrigemCenario;
import com.estudiobdd.model.Papel;
import com.estudiobdd.model.Prioridade;
import com.estudiobdd.model.Projeto;
import com.estudiobdd.model.Sugestao;
import com.estudiobdd.model.TipoPasso;
import com.estudiobdd.model.TransicaoFluxo;
import com.estudiobdd.model.Usuario;
import com.estudiobdd.repository.EtapaFluxoRepository;
import com.estudiobdd.repository.FluxoRepository;
import com.estudiobdd.repository.HistoriaUsuarioRepository;
import com.estudiobdd.repository.ProjetoRepository;
import com.estudiobdd.repository.TransicaoFluxoRepository;
import com.estudiobdd.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Carrega dados fictícios para demonstração. Só roda quando o banco está vazio,
 * então reiniciar a aplicação não duplica nada.
 * Todos os usuários de demonstração usam a senha SENHA_DEMO (dado fictício).
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);
    static final String SENHA_DEMO = "senha123";

    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final PasswordEncoder passwordEncoder;
    private final FluxoRepository fluxoRepository;
    private final EtapaFluxoRepository etapaRepository;
    private final TransicaoFluxoRepository transicaoRepository;

    public DadosIniciais(UsuarioRepository usuarioRepository,
                         ProjetoRepository projetoRepository,
                         HistoriaUsuarioRepository historiaRepository,
                         PasswordEncoder passwordEncoder,
                         FluxoRepository fluxoRepository,
                         EtapaFluxoRepository etapaRepository,
                         TransicaoFluxoRepository transicaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.historiaRepository = historiaRepository;
        this.passwordEncoder = passwordEncoder;
        this.fluxoRepository = fluxoRepository;
        this.etapaRepository = etapaRepository;
        this.transicaoRepository = transicaoRepository;
    }

    private Usuario usuarioDemo(String nome, String email, Papel papel) {
        Usuario u = new Usuario(nome, email);
        u.setPapel(papel);
        u.setSenhaHash(passwordEncoder.encode(SENHA_DEMO));
        return usuarioRepository.save(u);
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            definirSenhaDeQuemNaoTem();
            return;
        }

        Usuario ana = usuarioDemo("Ana Souza", "ana.souza@exemplo.com", Papel.ANALISTA);
        Usuario carlos = usuarioDemo("Carlos Lima", "carlos.lima@exemplo.com", Papel.ANALISTA);
        Usuario beatriz = usuarioDemo("Beatriz Rocha", "beatriz.rocha@exemplo.com", Papel.REVISOR);

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

        // atividade bônus: o projeto "Estúdio BDD" usa o fluxo de aprovação; o "Loja" não usa
        Fluxo fluxo = criarFluxoPadrao();
        EtapaFluxo rascunho = fluxo.etapaInicial().orElseThrow();
        estudio.setFluxo(fluxo);

        HistoriaUsuario exportar = new HistoriaUsuario(estudio, ana, "Exportar feature",
                "analista", "exportar uma história como arquivo .feature", "usar os cenários em testes automatizados",
                Prioridade.BAIXA);
        exportar.setEtapaAtual(rascunho);
        historiaRepository.save(exportar);

        HistoriaUsuario registrar = new HistoriaUsuario(estudio, beatriz, "Registrar sugestão",
                "analista", "registrar a sugestão recebida de uma IA", "decidir depois se ela será usada",
                Prioridade.MEDIA);
        Cenario registrarOk = new Cenario("Sugestão registrada fica pendente", OrigemCenario.HUMANO);
        registrarOk.adicionarPasso(TipoPasso.DADO, "a história \"Login\" sem sugestões");
        registrarOk.adicionarPasso(TipoPasso.QUANDO, "o analista registra uma sugestão do ChatGPT");
        registrarOk.adicionarPasso(TipoPasso.ENTAO, "a sugestão aparece como pendente");
        registrar.adicionarCenario(registrarOk);
        registrar.setEtapaAtual(rascunho);
        historiaRepository.save(registrar);

        log.info("Dados de demonstração carregados: 3 usuários, 2 projetos, 4 histórias e o fluxo de aprovação.");
    }

    // fluxo inicial da seção 10.2 do enunciado: são só dados, nenhuma etapa aparece no serviço
    private Fluxo criarFluxoPadrao() {
        Fluxo fluxo = fluxoRepository.save(new Fluxo("Aprovação de histórias",
                "Rascunho, revisão por IA, revisão humana e aprovação"));

        EtapaFluxo rascunho = etapaRepository.save(new EtapaFluxo(fluxo, "Rascunho", 1, true, false, true));
        EtapaFluxo revisaoIa = etapaRepository.save(new EtapaFluxo(fluxo, "Revisão por IA", 2, false, false, true));
        EtapaFluxo revisaoHumana = etapaRepository.save(new EtapaFluxo(fluxo, "Revisão humana", 3, false, false, false));
        EtapaFluxo aprovada = etapaRepository.save(new EtapaFluxo(fluxo, "Aprovada", 4, false, true, false));
        fluxo.getEtapas().addAll(List.of(rascunho, revisaoIa, revisaoHumana, aprovada));

        //                           comentário, autor diferente, cenários, sem pendência
        transicaoRepository.save(new TransicaoFluxo(fluxo, "Solicitar revisão por IA", rascunho, revisaoIa, Papel.ANALISTA)
                .exigindo(false, false, true, false));
        transicaoRepository.save(new TransicaoFluxo(fluxo, "Concluir curadoria", revisaoIa, rascunho, Papel.ANALISTA)
                .exigindo(false, false, false, true));
        transicaoRepository.save(new TransicaoFluxo(fluxo, "Enviar para revisão", rascunho, revisaoHumana, Papel.ANALISTA)
                .exigindo(false, false, true, true));
        transicaoRepository.save(new TransicaoFluxo(fluxo, "Aprovar", revisaoHumana, aprovada, Papel.REVISOR)
                .exigindo(false, true, false, false));
        transicaoRepository.save(new TransicaoFluxo(fluxo, "Devolver", revisaoHumana, rascunho, Papel.REVISOR)
                .exigindo(true, false, false, false));
        return fluxo;
    }

    // bancos criados antes do login existir têm usuários sem senha;
    // eles recebem a senha de demonstração para conseguirem entrar
    private void definirSenhaDeQuemNaoTem() {
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.getSenhaHash() == null) {
                u.setSenhaHash(passwordEncoder.encode(SENHA_DEMO));
                log.info("Senha de demonstração definida para {}", u.getEmail());
            }
            if (u.getPapel() == null) {
                u.setPapel(Papel.ANALISTA);
            }
        }
    }
}
