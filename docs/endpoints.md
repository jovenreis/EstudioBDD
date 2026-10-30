# Endpoints da API

Base: `http://localhost:8080`. As operações marcadas com * exigem o cabeçalho
`X-Usuario-Id` (identidade simulada, ver README).

## Usuários

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/usuarios` | Lista os usuários (para a seleção na SPA) | 200 |
| GET | `/api/usuarios/{id}` | Busca um usuário | 200, 404 |
| POST | `/api/usuarios` | Cadastra `{nome, email}` | 201, 400, 409 (e-mail repetido) |

## Projetos

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/projetos` | Lista os projetos | 200 |
| GET | `/api/projetos/{id}` | Busca um projeto | 200, 404 |
| POST | `/api/projetos` | Cadastra `{nome, descricao, contextoDominio}` | 201, 400, 409 (nome repetido) |
| PUT | `/api/projetos/{id}` | Edita o projeto | 200, 400, 404, 409 |
| DELETE | `/api/projetos/{id}` | Exclui (só se não tiver histórias) | 204, 404, 422 |

## Histórias

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/projetos/{projetoId}/historias?prioridade=ALTA` | Lista as histórias do projeto com quantidade de cenários e de sugestões pendentes. `prioridade` é opcional | 200, 404 |
| POST * | `/api/projetos/{projetoId}/historias` | Cadastra `{titulo, papel, objetivo, beneficio, prioridade}`; o autor é o usuário do cabeçalho | 201, 400, 404, 409 (título repetido no projeto) |
| GET | `/api/historias/{id}` | Detalhe com cenários e passos | 200, 404 |
| PUT * | `/api/historias/{id}` | Edita (só o autor) | 200, 400, 403, 404, 409 |
| DELETE * | `/api/historias/{id}` | Exclui com cenários e sugestões (só o autor) | 204, 403, 404 |

## Cenários

Os passos fazem parte do cenário (composição). Eles são enviados juntos, na ordem, e
editar um cenário substitui a lista inteira de passos. Assim toda alteração revalida a
gramática do cenário completo (regra 2).

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/historias/{historiaId}/cenarios` | Lista os cenários com os passos ordenados | 200, 404 |
| POST * | `/api/historias/{historiaId}/cenarios` | Cadastra `{titulo, origem, passos: [{tipo, texto}]}` | 201, 400, 403, 404, 409, 422 |
| GET | `/api/cenarios/{id}` | Busca um cenário | 200, 404 |
| PUT * | `/api/cenarios/{id}` | Edita título, origem e passos | 200, 400, 403, 404, 409, 422 |
| DELETE * | `/api/cenarios/{id}` | Exclui o cenário e os passos | 204, 403, 404 |

Valores aceitos:
- `prioridade`: `ALTA`, `MEDIA`, `BAIXA`
- `origem`: `HUMANO` (padrão), `SUGESTAO_IA`
- `tipo` do passo: `DADO`, `QUANDO`, `ENTAO`, `E`, `MAS`

## Formato dos erros

Todos os erros seguem o ProblemDetail (RFC 9457):

```json
{
  "type": "about:blank",
  "title": "Regra de negócio violada",
  "status": 422,
  "detail": "O cenário precisa ter ao menos um passo Então",
  "instance": "/api/historias/1/cenarios"
}
```

Erros de validação dos campos (400) trazem também `"erros": { "campo": "mensagem" }`.

| Status | Quando |
|---|---|
| 400 | Campo inválido, JSON mal formatado, cabeçalho `X-Usuario-Id` ausente |
| 403 | Usuário não é o autor da história |
| 404 | Recurso não existe |
| 409 | Valor que deveria ser único já existe |
| 422 | Regra de negócio violada |
