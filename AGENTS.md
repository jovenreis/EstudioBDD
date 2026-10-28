# AGENTS.md

Instruções para assistentes de IA que trabalharem neste repositório. Atualizar sempre que
a IA repetir um erro.

## Projeto

API REST em Spring Boot (`backend/`) e SPA em React (`frontend/`) para documentar
histórias de usuário e cenários BDD. O enunciado completo está com o grupo; as regras de
negócio estão resumidas abaixo.

## Comandos

```bash
docker compose up -d          # banco (raiz do repositório)
cd backend && mvn spring-boot:run
cd backend && mvn test        # testes de regras + ArchUnit
cd frontend && npm install && npm run dev
```

## Arquitetura (obrigatória)

- Cinco camadas: SPA → controller → service → repository → model.
- Controllers **não** acessam repositórios. Falam apenas com services. (Verificado por ArchUnit.)
- Regras de negócio ficam **só** no service. Nem no controller, nem na SPA.
- Repositórios são interfaces Spring Data JPA, sem regra de negócio.
- Controllers recebem e devolvem DTOs (pacote `dto`), nunca entidades.
- Dependências por construtor. Não usar `@Autowired` em atributo.
- Associações `@ManyToOne` com `FetchType.LAZY`. `spring.jpa.open-in-view=false`, então
  qualquer acesso a coleção LAZY deve acontecer dentro de um método `@Transactional` do service.
- Erros com `@RestControllerAdvice` devolvendo `ProblemDetail`.

## Convenções

- Código, nomes e mensagens em português, sem acentos em identificadores
  (`HistoriaUsuario`, `Cenario`, `ENTAO`).
- Sem Lombok: getters e setters escritos à mão.
- Mensagens de erro claras para o usuário final (ex.: "O cenário precisa ter ao menos um passo Então").
- Usuário atual vem do cabeçalho `X-Usuario-Id`.

## Regras de negócio

1. E-mail do usuário único; nome do projeto único; título da história único no projeto;
   papel, objetivo e benefício obrigatórios.
2. Cenário: título único na história, ao menos um QUANDO e um ENTAO, ordem DADO → QUANDO → ENTAO.
   E/MAS continuam o tipo anterior e não iniciam o cenário.
3. Só o autor edita ou exclui a história e seus cenários.
4. Sugestão começa PENDENTE. Rejeição exige motivo. Aceitar não altera cenários.
   Sugestão decidida não pode ser alterada.
5. Exporta `.feature` (com `# language: pt`) só se houver ao menos um cenário e nenhuma sugestão pendente.
6. Consulta do projeto: histórias com quantidade de cenários e de sugestões pendentes, filtro por prioridade.
7. Resumo do projeto: total de histórias, cenários por origem, sugestões por decisão, motivos de rejeição mais frequentes.

## Não fazer

- Não incluir credenciais reais nem dados pessoais reais.
- Não criar endpoints que retornem entidades JPA diretamente.
- Não colocar validação de regra de negócio só no front-end.
