# Endpoints da API

Base: `http://localhost:8080`. As operações marcadas com * exigem o cabeçalho
`X-Usuario-Id` (identidade simulada, ver README).

## Login

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| POST | `/api/login` | Confere `{email, senha}` e devolve `{id, nome, email}`. A SPA passa a enviar esse `id` no `X-Usuario-Id` | 200, 400, 401 ("E-mail ou senha incorretos") |

## Usuários

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/usuarios` | Lista os usuários (para a seleção na SPA) | 200 |
| GET | `/api/usuarios/{id}` | Busca um usuário | 200, 404 |
| POST | `/api/usuarios` | Cadastra `{nome, email, senha}` (senha de 6 a 72 caracteres, guardada como hash BCrypt; a resposta nunca traz a senha) | 201, 400, 409 (e-mail repetido) |

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

## Sugestões (regra 4)

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/historias/{historiaId}/sugestoes?decisao=PENDENTE` | Lista as sugestões da história. `decisao` é opcional (`PENDENTE`, `ACEITA`, `REJEITADA`) | 200, 404 |
| POST * | `/api/historias/{historiaId}/sugestoes` | Registra `{ferramenta, prompt, conteudo}`. A sugestão nasce `PENDENTE` | 201, 400, 404 |
| GET | `/api/sugestoes/{id}` | Busca uma sugestão | 200, 404 |
| POST * | `/api/sugestoes/{id}/aceitar` | Aceita. Corpo opcional `{justificativa}`. Não altera os cenários | 200, 404, 422 (já decidida) |
| POST * | `/api/sugestoes/{id}/rejeitar` | Rejeita com `{motivo, justificativa}`. `motivo` obrigatório | 200, 404, 422 (sem motivo ou já decidida) |

`motivo`: `INCORRETA`, `GENERICA`, `DUPLICADA`, `FORA_DO_ESCOPO`, `OUTRO`.

## Exportação (regra 5)

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/historias/{id}/feature` | Devolve a história em Gherkin (`text/plain`, `# language: pt`) com `Content-Disposition` de anexo | 200, 404, 422 (sem cenários ou com sugestão pendente) |

Exemplo de saída:

```gherkin
# language: pt
Funcionalidade: Login
  Como cliente
  Quero entrar na loja com e-mail e senha
  Para acompanhar meus pedidos

  Cenário: Login com dados corretos
    Dado o cliente "joao@exemplo.com" cadastrado com a senha "abc123"
    Quando ele entra com "joao@exemplo.com" e "abc123"
    Então o acesso é liberado
```

## Resumo do projeto (regra 7)

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/projetos/{id}/resumo` | Total de histórias, cenários por origem, sugestões por decisão e motivos de rejeição (do mais para o menos frequente) | 200, 404 |

```json
{
  "projetoId": 1,
  "projetoNome": "Loja",
  "totalHistorias": 2,
  "cenariosPorOrigem": { "HUMANO": 3, "SUGESTAO_IA": 0 },
  "sugestoesPorDecisao": { "PENDENTE": 0, "ACEITA": 1, "REJEITADA": 1 },
  "motivosRejeicao": [ { "motivo": "GENERICA", "quantidade": 1 } ]
}
```

## Fluxo de aprovação (atividade bônus)

Detalhes em [bonus-fluxo.md](bonus-fluxo.md).

| Método | Caminho | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/projetos/{id}/quadro` | Colunas do quadro (uma por etapa) com as histórias de cada uma | 200, 404, 422 (projeto sem fluxo) |
| PUT | `/api/projetos/{id}/fluxo` | Define o fluxo do projeto `{fluxoId}` (nulo desliga) | 200, 404, 422 |
| GET * | `/api/historias/{id}/acoes` | Ações que o usuário atual pode tentar na etapa atual | 200, 404 |
| POST * | `/api/historias/{id}/acoes/{transicaoId}` | Executa a ação; corpo opcional `{comentario}`. Devolve a história atualizada | 200, 403 (papel), 404, 422 (fora da etapa, sem comentário, próprio autor, sem cenário, com pendência) |
| GET | `/api/historias/{id}/historico` | Transições executadas, com usuário, data e comentário | 200, 404 |
| GET | `/api/fluxos` | Lista os fluxos | 200 |
| GET | `/api/fluxos/{id}` | Fluxo com etapas e transições | 200, 404 |
| POST | `/api/fluxos/{id}/etapas` | Cadastra `{nome, posicao, inicial, etapaFinal, permiteEdicao}` | 201, 400, 404, 409, 422 |
| PUT | `/api/fluxos/{id}/etapas/{etapaId}` | Altera uma etapa | 200, 400, 404, 409, 422 |
| POST | `/api/fluxos/{id}/transicoes` | Cadastra `{nomeAcao, origemId, destinoId, papelPermitido, exigeComentario, exigeUsuarioDiferenteDoAutor, exigeCenarios, exigeSemSugestaoPendente}` | 201, 400, 404, 422 |

Com o fluxo ativo, `PUT/DELETE` de história e cenários também devolvem 422 quando a etapa
não permite edição, e a exportação devolve 422 fora da etapa final. `GET /api/historias/{id}`
passa a trazer `usaFluxo`, `etapaNome` e `podeEditar`; o login e `/api/usuarios` trazem `papel`.

## CORS

A API só aceita chamadas de navegador vindas da origem da SPA, configurada em
`app.cors.origem` (padrão `http://localhost:5173`, ou a variável de ambiente `CORS_ORIGEM`).

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
| 401 | Login com e-mail ou senha incorretos |
| 403 | Usuário não é o autor da história |
| 404 | Recurso não existe |
| 409 | Valor que deveria ser único já existe |
| 422 | Regra de negócio violada |
