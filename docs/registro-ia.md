# Registro de uso de IA

Mínimo exigido: seis interações relevantes (ao menos três da API e três da SPA).
Registrar no momento em que acontecem.

| # | Parte | Data |
|---|---|---|
| 1 | API | 28/10/2026 |

---

## 1. Estrutura inicial da API e mapeamento das entidades

- **Parte:** API
- **Ferramenta:** Claude (Anthropic)
- **Problema:** montar a fundação do projeto (Semana 1): projeto Spring Boot com os pacotes
  das cinco camadas, mapeamento JPA das entidades da seção 3 do enunciado, banco PostgreSQL
  e dados fictícios.
- **Prompt (resumo):** enviamos o enunciado do projeto e os slides da aula de BDD e pedimos a
  estrutura inicial do projeto, explicando cada parte. Definimos antes: Java 21, React + Vite
  na SPA e PostgreSQL via Docker Compose.
- **Proposta da IA:** `pom.xml` com Spring Web, Data JPA, Validation e PostgreSQL;
  `docker-compose.yml`; entidades `Usuario`, `Projeto`, `HistoriaUsuario`, `Cenario`,
  `Passo` e `Sugestao` com enums; repositórios; carga de dados fictícios com
  `CommandLineRunner`; README, AGENTS.md e diagrama de classes em Mermaid.
- **Decisão do grupo:** _(preencher: o que aceitamos, o que mudamos e por quê)_
- **Mudanças realizadas:** _(preencher)_
- **Verificação:** _(preencher após rodar: `docker compose up -d`, `mvn spring-boot:run`,
  conferir as tabelas com `\dt` e reiniciar a aplicação para ver que os dados não duplicam)_
