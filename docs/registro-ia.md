# Registro de uso de IA

Mínimo exigido: seis interações relevantes (ao menos três da API e três da SPA).

| # | Parte | Data | Assunto |
|---|---|---|---|
| 1 | API | 28/10/2026 | Estrutura inicial e mapeamento das entidades |
| 2 | API | 28/10/2026 | Ambiente do Codespaces não era criado |
| 3 | API | 28/10/2026 | Ampliação dos cenários iniciais e dúvidas sobre o enunciado |
| 4 | API | 30/10/2026 | Núcleo da API (Semana 2) |
| 5 | API | 30/10/2026 | Sugestões, exportação, resumo e testes |
| 6 | SPA | 30/10/2026 | Início da SPA |
| 7 | API | 03/11/2026 | Entendimento dos enums do domínio |

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
- **Decisão do grupo:** aceitamos a proposta depois de executar e testar a API. Rodamos o
  `mvn test` nos três ambientes e testamos cada requisição de `requisicoes.http`, conferindo se
  os códigos de resposta batiam com as regras: 201 para cenário válido, 422 para cenário sem
  Então, fora de ordem ou iniciado por E, 409 para título repetido e 403 quando outro usuário
  tenta editar a história. Mantivemos duas decisões da IA como provisórias e as registramos em
  `duvidas-professor.md`: os passos são editados junto com o cenário, e um projeto com histórias
  não pode ser excluído. Também decidimos concentrar os commits desta etapa em um integrante,
  enquanto os outros dois validavam a execução nos próprios ambientes.
- **Mudanças realizadas:** pacotes `controller`, `dto` e `service`; consultas nos repositórios;
  métodos auxiliares nas entidades; `ValidadorCenarioTest`; `docs/endpoints.md` e
  `docs/requisicoes.http`.
- **Verificação:** `mvn test` (9 testes da regra 2) e as requisições de `requisicoes.http`:
  cenário válido aceito (201), cenários inválidos rejeitados (422) e edição por quem não é autor
  bloqueada (403). Prints em `docs/evidencias/`.

## 5. API completa: sugestões, exportação, resumo e testes (Semana 3)

- **Ferramenta:** Claude (Anthropic)
- **Problema:** implementar as regras 4, 5 e 7, os testes obrigatórios (rejeição sem motivo,
  exportação com sugestão pendente) e a regra de arquitetura com ArchUnit.
- **Prompt (resumo):** pedimos a implementação das sugestões, da exportação `.feature` e do
  resumo do projeto, seguindo o AGENTS.md, e os testes que rodam no `mvn test`.
- **Proposta da IA:** `SugestaoService` (aceitar e rejeitar como ações `POST`, motivo validado
  no serviço para devolver 422), `ExportacaoService` (gera o Gherkin e bloqueia sem cenários ou
  com pendência), `ResumoService` (contagens com consultas derivadas do Spring Data), CORS
  limitado à origem da SPA, `SugestaoServiceTest` e `ExportacaoServiceTest` com Mockito e
  `ArquiteturaTest` com ArchUnit.
- **Decisão do grupo:** aceitamos a proposta depois de rodar os testes e todas as requisições
  das regras 4, 5 e 7. Na primeira tentativa, a exportação da história 1 respondeu 200, quando
  esperávamos 422. Perguntamos à IA e entendemos que não era defeito: a sugestão 1 da carga
  inicial já tinha sido aceita em um teste anterior, e uma sugestão decidida não volta a ficar
  pendente (regra 4). Zeramos o banco (`docker compose down -v`) e refizemos as requisições na
  ordem. Também notamos que a "rejeição sem motivo" devolveu 422 pelo motivo errado ("já foi
  aceita"), porque o serviço primeiro verifica se a sugestão está pendente e só depois exige o
  motivo. Refizemos esse teste com uma sugestão nova (id 3) e obtivemos a mensagem correta
  ("Informe o motivo da rejeição"). Ao enviar duas vezes a rejeição da sugestão 2, a segunda
  resposta foi 422 ("já foi rejeitada"), o que confirmou o bloqueio de alteração de sugestão
  decidida. Mantivemos a ordem das verificações no serviço.
- **Mudanças realizadas:** nenhuma no código proposto. Geramos `docs/login.feature` com a
  exportação da própria API (`curl .../api/historias/1/feature`). A sugestão 3, repetida, foi
  rejeitada com o motivo DUPLICADA. Marcamos a versão como `api-completa`.
- **Verificação:** `mvn test` com 18 testes passando (9 da regra 2, 4 de sugestões, 4 de
  exportação e 1 do ArchUnit). Pelas requisições: exportação bloqueada com sugestão pendente
  (422), rejeição sem motivo bloqueada (422), alteração de sugestão decidida bloqueada (422),
  `.feature` gerado depois de decidir as sugestões (200) e resumo coerente com os dados
  (2 histórias, 3 cenários HUMANO, 1 aceita, 1 rejeitada com motivo GENERICA). Prints em
  `docs/evidencias/`.

## 6. Início da SPA (Semana 3)

- **Parte:** SPA
- **Ferramenta:** Claude (Anthropic)
- **Problema:** criar a SPA com a seleção do usuário de demonstração e a lista de projetos e
  histórias com filtro por prioridade, consumindo a API.
- **Prompt (resumo):** pedimos uma SPA em React + Vite que funcionasse no Codespaces, sem
  regra de negócio no front-end.
- **Proposta da IA:** `api.js` centralizando as chamadas e o cabeçalho `X-Usuario-Id`;
  `UsuarioContext` para o usuário escolhido; telas de projetos e histórias com o filtro enviado
  à API; proxy do Vite para `/api`, porque no Codespaces cada porta tem um endereço diferente.
- **Decisão do grupo:** aceitamos a proposta. A IA explicou que, no Codespaces, a SPA (porta
  5173) e a API (porta 8080) ficam em endereços diferentes, então a SPA chama `/api` no próprio
  Vite, que repassa para a API. O CORS da API continua limitado à origem da SPA, como pede o
  enunciado. Abrimos a SPA pela aba Portas, trocamos o usuário no topo e usamos o
  filtro de prioridade no projeto "Loja".
- **Mudanças realizadas:** nenhuma no código proposto.
- **Verificação:** `npm run dev`, troca de usuário no topo, lista de projetos e filtro por
  prioridade mostrando os dados reais do banco.

## 7. Entendimento dos enums do domínio

- **Ferramenta:** Claude (Anthropic)
- **Problema:** entender de onde vinham `TipoPasso.DADO`, `TipoPasso.QUANDO` etc. no
  `DadosIniciais`, por que o projeto tem vários enums e por que usar enum em vez de texto.
- **Prompt (resumo):** "de onde vem o TipoPasso.DADO?", "por que fez vários enums?" e
  "mas por que o enum?".
- **Proposta da IA:** explicou que os cinco enums (`Prioridade`, `OrigemCenario`, `TipoPasso`,
  `DecisaoSugestao`, `MotivoRejeicao`) vêm dos atributos com valores fixos da seção 3 do
  enunciado. Comparou enum com `String` e com `int`: o enum impede valores inválidos já na
  compilação, a API recusa valores fora da lista com 400, e o valor pode carregar dados e
  comportamento (`TipoPasso.ENTAO` guarda "Então" e o método `isContinuacao()`). Também
  explicou o `@Enumerated(EnumType.STRING)`, que guarda o nome no banco em vez da posição.
- **Decisão do grupo:** mantivemos os enums. Uma tabela no banco só faria sentido se os valores
  mudassem com o sistema rodando, o que não é o caso.
- **Mudanças realizadas:** nenhuma.
- **Verificação:** conferimos na seção 3 do enunciado que os cinco atributos têm listas fixas, e
  vimos no banco que a coluna `tipo` da tabela `passo` guarda os nomes (`DADO`, `QUANDO`...).
