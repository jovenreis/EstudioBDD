# Estúdio BDD

Projeto de Programação de Sistemas II (2026/2). Aplicação para documentar requisitos como
histórias de usuário e cenários BDD (Dado / Quando / Então), registrar as sugestões obtidas
com ferramentas de IA e exportar as histórias como arquivos `.feature`.

## Integrantes

| Nome | RA |
|---|---|
| Bruno Martin Magalhãe | 10752758 |
| Henry Ribeiro Reis | 10752725 |
| Pedro Pizane Stach Ferreira | 10753060 |

## Tecnologias

- API: Java 21, Spring Boot 3.5.16, Spring Data JPA, Bean Validation, BCrypt (spring-security-crypto)
- Banco: PostgreSQL 16 (via Docker Compose)
- SPA: React 18 + Vite 5 + React Router (pasta `frontend/`)

## Estrutura

```
backend/    API REST (Spring Boot)
frontend/   SPA em React
docs/       diagrama de classes, registro de IA, cenários e evidências
```

Pacotes da API (`backend/src/main/java/com/estudiobdd`):

| Pacote | Camada | Responsabilidade |
|---|---|---|
| `controller` | Controller | Recebe as requisições HTTP, valida os DTOs e devolve as respostas |
| `dto` | Controller | Objetos de entrada e saída da API |
| `service` | Serviço | Regras de negócio e transações |
| `repository` | Repositório | Interfaces Spring Data JPA |
| `model` | Domínio | Entidades JPA e enums |
| `config` | — | Configurações e carga de dados fictícios |

Fluxo de uma requisição: SPA → HTTP/JSON → controller → serviço → repositório → banco.

## Como executar

### Pré-requisitos

- Java 21
- Maven 3.9+ (ou o Maven embutido do IntelliJ)
- Docker Desktop

### 1. Banco de dados

Na raiz do repositório:

```bash
docker compose up -d
```

Sobe um PostgreSQL em `localhost:5432` (banco `estudiobdd`, usuário e senha `estudio`,
apenas para desenvolvimento local).

### 2. API

```bash
cd backend
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Na primeira execução as tabelas são criadas e os
dados fictícios são carregados (classe `config/DadosIniciais`). Reiniciar a aplicação não
apaga nem duplica os dados.

Para conferir as tabelas:

```bash
docker exec -it estudio-bdd-db psql -U estudio -d estudiobdd -c "\dt"
```

### 3. Testes

```bash
cd backend
mvn test
```

Os testes não precisam do banco. Eles cobrem as regras 2, 4 e 5, o login, o fluxo de
aprovação (bônus) e a regra de arquitetura (ArchUnit: controllers não acessam repositórios).

Para testar os endpoints à mão, use o arquivo `docs/requisicoes.http` com a extensão
REST Client do VS Code.

### 4. SPA

Com a API rodando, em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

A SPA abre em `http://localhost:5173` (no Codespaces, pela aba **Portas**, porta 5173).

Telas:

| Caminho | Tela |
|---|---|
| (sem login) | Tela de login, com a seleção do usuário de demonstração |
| `/` | Início; o topo mostra o usuário logado e o botão Sair |
| `/projetos/:id` | Histórias do projeto, com filtro por prioridade |
| `/projetos/:id/historias/nova` | Nova história |
| `/projetos/:id/resumo` | Resumo do projeto |
| `/projetos/:id/quadro` | Quadro do fluxo de aprovação (bônus), uma coluna por etapa |
| `/historias/:id` | História com os cenários (edição dos passos), exportação do `.feature` e sugestões ao lado |
| `/historias/:id/editar` | Edição da história |
Em desenvolvimento ela chama `/api` no próprio Vite, que repassa para a API na porta 8080
(proxy em `vite.config.js`). Por isso funciona igual no computador e no Codespaces.

### CORS

A API libera o acesso do navegador apenas para a origem da SPA, definida em
`app.cors.origem` (padrão `http://localhost:5173`). Para outra origem, defina a variável
de ambiente `CORS_ORIGEM` antes de subir a API.

## Login e identidade

A SPA começa por uma tela de login com e-mail e senha.

- A senha é guardada no banco só como **hash BCrypt** (coluna `senha_hash`), nunca em texto.
- `POST /api/login` confere o e-mail e a senha e devolve o usuário. E-mail inexistente e senha
  errada recebem a mesma mensagem, para não revelar quais e-mails estão cadastrados.
- Depois do login, a SPA envia o id do usuário no cabeçalho `X-Usuario-Id`, e os serviços usam
  esse valor para validar a autoria (regra 3).

**Limitação:** não há token nem sessão. A senha é verificada só no login; as demais rotas
continuam confiando no cabeçalho `X-Usuario-Id`, que qualquer cliente pode enviar. É a
identidade simulada pedida no enunciado, com uma tela de login na frente. Para um sistema
real, o próximo passo seria usar Spring Security com um token (por exemplo, JWT) validado em
todas as requisições.

Contas de demonstração (dados fictícios), todas com a senha `senha123`:
`ana.souza@exemplo.com` (analista), `carlos.lima@exemplo.com` (analista) e
`beatriz.rocha@exemplo.com` (revisora). O papel só importa no fluxo de aprovação (bônus).

## Tarefas previstas

- [x] Modelagem e diagrama de classes
- [x] Projeto Spring Boot, pacotes e entidades
- [x] Dados fictícios
- [x] CRUD de usuários, projetos, histórias, cenários e passos (regras 1, 2 e 3)
- [x] Tratamento de erros padronizado com ProblemDetail
- [x] Identidade simulada pelo cabeçalho `X-Usuario-Id`
- [x] Tela de login com e-mail e senha (senha com hash BCrypt)
- [x] Sugestões: registro, aceitação e rejeição (regra 4)
- [x] Exportação `.feature` (regra 5)
- [x] Lista de histórias do projeto com filtro por prioridade (regra 6)
- [x] Resumo do projeto (regra 7)
- [x] Testes de regras (cenário sem Então, rejeição sem motivo, exportação com pendência) e teste ArchUnit
- [x] Documentação dos endpoints
- [x] CORS liberado só para a origem da SPA
- [x] SPA: seleção de usuário, projetos e histórias com filtro
- [x] SPA: edição de história e cenários, sugestões, exportação e resumo
- [x] Roteiro de verificação
- [x] Atividade bônus: fluxo de aprovação configurável ([descrição](docs/bonus-fluxo.md))

## Documentação

- [Diagrama de classes](docs/diagrama-classes.md)
- [Endpoints](docs/endpoints.md)
- [Requisições de teste](docs/requisicoes.http)
- [Caminho de "cadastrar um cenário" pelas cinco camadas](docs/caminho-cadastrar-cenario.md)
- [Registro de IA](docs/registro-ia.md)
- [História e cenários iniciais](docs/cenarios-iniciais.md)
- [Dúvidas para o professor](docs/duvidas-professor.md)
- [Roteiro de verificação](docs/roteiro-verificacao.md)
- [Atividade bônus: fluxo de aprovação](docs/bonus-fluxo.md)
- [Arquivo .feature exportado pelo sistema](docs/login.feature)
