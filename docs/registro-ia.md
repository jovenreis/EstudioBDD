# Registro de uso de IA

Mínimo exigido: seis interações relevantes (ao menos três da API e três da SPA).

| # | Parte | Data | Assunto |
|---|---|---|---|
| 1 | API | 28/10/2026 | Estrutura inicial e mapeamento das entidades |
| 2 | API | 28/10/2026 | Ambiente do Codespaces não era criado |
| 3 | API | 28/10/2026 | Ampliação dos cenários iniciais e dúvidas sobre o enunciado |
| 4 | API | 30/10/2026 | Núcleo da API (Semana 2) |

---

## 1. Estrutura inicial da API e mapeamento das entidades

- **Ferramenta:** Claude (Anthropic)
- **Problema:** montar a fundação do projeto (Semana 1): projeto Spring Boot com os pacotes
  das cinco camadas, mapeamento JPA das entidades da seção 3, banco PostgreSQL e dados fictícios.
- **Prompt (resumo):** enviamos o enunciado e os slides da aula de BDD e pedimos a estrutura
  inicial, com explicação de cada parte. Definimos antes: Java 21, React + Vite e PostgreSQL
  via Docker Compose.
- **Proposta da IA:** `pom.xml` com Spring Web, Data JPA, Validation e PostgreSQL;
  `docker-compose.yml`; entidades `Usuario`, `Projeto`, `HistoriaUsuario`, `Cenario`, `Passo` e
  `Sugestao` com enums; repositórios; carga de dados fictícios com `CommandLineRunner`;
  README, AGENTS.md e diagrama de classes em Mermaid.
- **Decisão do grupo:** aceitamos a estrutura. A entidade `Sugestao` foi mapeada já na
  Semana 1, antes do previsto, para não mudar o esquema do banco depois.
- **Mudanças realizadas:** nenhuma no código proposto. Os arquivos foram divididos em
  commits por integrante.
- **Verificação:** `docker compose up -d` e `mvn spring-boot:run`. O log mostrou a carga
  dos dados fictícios, e `\dt` no PostgreSQL listou as 6 tabelas. Ao reiniciar a aplicação,
  os dados não foram carregados de novo. Prints em `docs/evidencias/`.

## 2. Ambiente do Codespaces não era criado

- **Ferramenta:** Claude (Anthropic)
- **Problema:** o Codespace abria em modo de recuperação, sem Java, Maven e Docker
  (`command not found`).
- **Prompt (resumo):** enviamos o log de criação do Codespace.
- **Proposta da IA:** o log mostrava que a imagem `devcontainers/java` tinha um repositório
  do Yarn com chave GPG inválida, o que fazia o `apt-get update` falhar ao instalar o Docker.
  A IA sugeriu trocar a imagem por `devcontainers/base:bookworm`, instalar o Java 21 e o
  Maven pela feature `java` e instalar o Docker primeiro.
- **Decisão do grupo:** aceita.
- **Mudanças realizadas:** `.devcontainer/devcontainer.json`.
- **Verificação:** apagamos e recriamos o Codespace. `java -version`, `mvn -v` e
  `docker --version` responderam, e a aplicação subiu.

## 3. Ampliação dos cenários iniciais e dúvidas sobre o enunciado

- **Ferramenta:** Claude (Anthropic)
- **Problema:** atividade inicial da seção 5. O grupo escreveu, sem IA, três cenários para a
  regra 2 e pediu à IA que revisasse e ampliasse. Também pediu ajuda para levantar pontos
  ambíguos do enunciado.
- **Prompt (resumo):** enviamos os três cenários e pedimos revisão, melhorias e casos de borda
  em Gherkin; depois pedimos uma lista de pontos ambíguos das regras.
- **Proposta da IA:** onze sugestões. Três eram revisões dos nossos cenários: o exemplo 3 era de
  outra regra, usava linguagem de tela e faltava a narrativa da história. As outras oito eram
  novos cenários. A IA também listou sete dúvidas para o professor.
- **Decisão do grupo:** aceitamos sete sugestões e rejeitamos quatro (GENERICA, DUPLICADA,
  INCORRETA, FORA_DO_ESCOPO), com o motivo de cada uma em
  [cenarios-iniciais.md](cenarios-iniciais.md). O exemplo 3 ficou guardado para a regra 4.
- **Mudanças realizadas:** `docs/cenarios-iniciais.md` (versão final na seção 5) e
  `docs/duvidas-professor.md`.
- **Verificação:** conferimos os cenários aceitos contra o texto da regra 2; eles viraram
  casos de teste em `ValidadorCenarioTest`.

## 4. Núcleo da API (Semana 2)

- **Ferramenta:** Claude (Anthropic)
- **Problema:** implementar o CRUD de usuários, projetos, histórias, cenários e passos, com as
  regras 1, 2 e 3, erros padronizados e identidade simulada.
- **Prompt (resumo):** pedimos a implementação do núcleo da API seguindo as camadas do AGENTS.md,
  com explicação de cada parte.
- **Proposta da IA:** serviços com as regras de negócio; `ValidadorCenario` para a gramática
  Dado/Quando/Então; `TratadorErros` com ProblemDetail (400, 403, 404, 409, 422); DTOs em records;
  cabeçalho `X-Usuario-Id`; passos enviados e editados junto com o cenário; bloqueio da exclusão
  de projeto com histórias.
- **Decisão do grupo:** _(revisar)_ aceitamos a proposta. Ficaram registradas como dúvidas para
  o professor a edição dos passos junto com o cenário e a exclusão de projeto com histórias.
- **Mudanças realizadas:** pacotes `controller`, `dto` e `service`; consultas nos repositórios;
  métodos auxiliares nas entidades; `ValidadorCenarioTest`; `docs/endpoints.md` e
  `docs/requisicoes.http`.
- **Verificação:** `mvn test` (9 testes da regra 2) e as requisições de `requisicoes.http`:
  cenário válido aceito (201), cenários inválidos rejeitados (422) e edição por quem não é autor
  bloqueada (403). Prints em `docs/evidencias/`.
