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

### 4. SPA

Em construção.

## Identidade simulada

A API não tem autenticação. O usuário atual será informado pelo cabeçalho `X-Usuario-Id`
e os serviços usam esse valor para validar a autoria. Qualquer cliente pode enviar qualquer
id, então isso serve só para demonstração.

## Tarefas previstas

- [x] Modelagem e diagrama de classes
- [x] Projeto Spring Boot, pacotes e entidades
- [x] Dados fictícios
- [ ] CRUD de projetos, histórias, cenários e passos (regras 1, 2 e 3)
- [ ] Tratamento de erros padronizado com ProblemDetail
- [ ] Sugestões: registro, aceitação e rejeição (regra 4)
- [ ] Exportação `.feature` (regra 5)
- [ ] Consulta e resumo do projeto (regras 6 e 7)
- [ ] Testes de regras e teste ArchUnit
- [ ] Documentação dos endpoints
- [ ] SPA
- [ ] Roteiro de verificação

## Documentação

- [Diagrama de classes](docs/diagrama-classes.md)
- [Registro de IA](docs/registro-ia.md)
- [História e cenários iniciais](docs/cenarios-iniciais.md)
- [Dúvidas para o professor](docs/duvidas-professor.md)
- [Caminho de "cadastrar um cenário" pelas cinco camadas](docs/caminho-cadastrar-cenario.md)
