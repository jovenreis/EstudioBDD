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

- API: Java 21, Spring Boot 3.5, Spring Data JPA, Bean Validation
- Banco: PostgreSQL 16 (via Docker Compose)
- SPA: React + Vite (pasta `frontend/`, em construção)

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

Os testes atuais não precisam do banco.

Para testar os endpoints à mão, use o arquivo `docs/requisicoes.http` com a extensão
REST Client do VS Code.

### 4. SPA

Em construção.

## Identidade simulada

A API não tem autenticação. O usuário atual é informado pelo cabeçalho `X-Usuario-Id`
nas operações que alteram histórias e cenários, e os serviços usam esse valor para validar a autoria (regra 3). Qualquer cliente pode enviar qualquer
id, então isso serve só para demonstração.

## Tarefas previstas

- [x] Modelagem e diagrama de classes
- [x] Projeto Spring Boot, pacotes e entidades
- [x] Dados fictícios
- [x] CRUD de usuários, projetos, histórias, cenários e passos (regras 1, 2 e 3)
- [x] Tratamento de erros padronizado com ProblemDetail
- [x] Identidade simulada pelo cabeçalho `X-Usuario-Id`
- [ ] Sugestões: registro, aceitação e rejeição (regra 4)
- [ ] Exportação `.feature` (regra 5)
- [x] Lista de histórias do projeto com filtro por prioridade (regra 6)
- [ ] Resumo do projeto (regra 7)
- [x] Teste da gramática do cenário (regra 2)
- [ ] Demais testes de regras e teste ArchUnit
- [x] Documentação dos endpoints (parcial, atualizada a cada etapa)
- [ ] SPA
- [ ] Roteiro de verificação

## Documentação

- [Diagrama de classes](docs/diagrama-classes.md)
- [Endpoints](docs/endpoints.md)
- [Requisições de teste](docs/requisicoes.http)
- [Caminho de "cadastrar um cenário" pelas cinco camadas](docs/caminho-cadastrar-cenario.md)
- [Registro de IA](docs/registro-ia.md)
- [História e cenários iniciais](docs/cenarios-iniciais.md)
- [Dúvidas para o professor](docs/duvidas-professor.md)
