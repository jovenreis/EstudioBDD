# Roteiro de verificação

Itens da seção 13 do enunciado. Para cada um: como verificar, entrada, resultado esperado
e resultado observado. A coluna "Observado" é preenchida pelo grupo ao executar o roteiro.

## Preparação

```bash
cd /workspaces/EstudioBDD
docker compose down -v && docker compose up -d      # banco zerado, com os dados fictícios
cd backend && mvn test                               # 18 testes
mvn spring-boot:run                                  # API na 8080
# em outro terminal
cd /workspaces/EstudioBDD/frontend && npm install && npm run dev   # SPA na 5173
```

Dados fictícios: usuários 1 (Ana) e 2 (Carlos); projeto 1 "Loja" com as histórias
"Login" (autora Ana, 2 cenários, 1 sugestão pendente) e "Adicionar ao carrinho" (autor Carlos).

Executado em: ____/____/2026, por: ______________________

## Histórias (regra 1)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 1 | SPA: Loja → Nova história | Título "Recuperar senha", Como "cliente", Quero "recuperar minha senha por e-mail", Para "voltar a acessar a loja" | História criada e aberta | | |
| 2 | SPA: Loja → Nova história | Título "Login" (já existe no projeto) | Recusada: "Já existe uma história com o título "Login" neste projeto" (409) | | |
| 3 | SPA: Loja → Nova história | Campo "Para" vazio | Recusada com o campo inválido listado (400) | | |

## Cenários e passos (regra 2)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 4 | SPA: história "Recuperar senha" → Novo cenário | Dado / Quando / Então preenchidos | Cenário salvo com os passos na ordem | | |
| 5 | Idem | Passos Dado / Quando / E (sem Então) | Recusado: "O cenário precisa ter ao menos um passo Então" (422) | | |
| 6 | Idem | Passos Dado / Então (sem Quando) | Recusado: "O cenário precisa ter ao menos um passo Quando" (422) | | |
| 7 | Idem | Passos Dado / Então / Quando | Recusado: passo fora de ordem (422) | | |
| 8 | Idem | Primeiro passo do tipo E | Recusado: "O cenário não pode começar com um passo E" (422) | | |
| 9 | SPA: editar o cenário do item 4 | Adicionar um passo E e subir ele para antes do Então; salvar | Passos exibidos na nova ordem, inclusive depois de recarregar a página (F5) | | |

## Autoria (regra 3)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 10 | SPA: usuário "Carlos Lima", abrir "Login" (da Ana) → Editar história → Salvar | Qualquer alteração | Recusada: "Somente o autor da história pode alterá-la ou excluí-la" (403) | | |
| 11 | SPA: usuário "Carlos Lima", "Login" → Excluir história → Confirmar | — | Recusada com a mesma mensagem (403) | | |
| 12 | SPA: usuário "Carlos Lima", "Login" → Excluir um cenário | — | Recusada (403) | | |

## Sugestões e exportação (regras 4 e 5)

Fazer na ordem: a exportação bloqueada (item 14) precisa acontecer antes de as sugestões
serem decididas.

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 13 | SPA: "Login" → Registrar sugestão | Ferramenta, prompt e conteúdo | Sugestão aparece como Pendente; agora há 2 pendentes | | |
| 14 | SPA: "Login" → Exportar .feature | — | Recusada: "A história tem 2 sugestões pendentes…" (422) | | |
| 15 | SPA: a sugestão nova → Rejeitar → Confirmar rejeição | Sem escolher motivo | Recusada: "Informe o motivo da rejeição…" (422); continua Pendente | | |
| 16 | Idem | Motivo "Genérica" | Sugestão passa a Rejeitada (Genérica) | | |
| 17 | SPA: a sugestão da carga inicial → Aceitar → Confirmar aceite | Justificativa opcional | Sugestão passa a Aceita; os cenários da história não mudam | | |
| 18 | `requisicoes.http`: "Alterar sugestão já decidida" | Rejeitar a sugestão 1, já aceita | Recusada: "A sugestão já foi aceita e não pode ser alterada" (422) | | |
| 19 | SPA: projeto "Estúdio BDD" → "Exportar feature" (sem cenários) → Exportar .feature | — | Recusada: "A história precisa ter ao menos um cenário para ser exportada" (422) | | |
| 20 | SPA: "Login", já sem pendências → Exportar .feature → Baixar arquivo | — | Texto em Gherkin com `# language: pt`, narrativa e cenários na ordem; arquivo `login.feature` baixado | | `docs/login.feature` |

## Consulta e resumo (regras 6 e 7)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 21 | SPA: Loja, filtro "Alta" | — | Só as histórias de prioridade alta, com quantidade de cenários e de sugestões pendentes | | |
| 22 | SPA: Loja → Ver resumo | — | Números iguais aos dados cadastrados (conferir contando na tela das histórias) | | |

## Persistência

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 23 | Parar a API (Ctrl + C) e subir de novo | — | Tudo o que foi cadastrado continua lá; a carga de dados fictícios não roda de novo | | |

## Testes automatizados

| # | Como verificar | Esperado | Observado | Evidência |
|---|---|---|---|---|
| 24 | `mvn test` | 18 testes, 0 falhas: cenário sem Então rejeitado, rejeição sem motivo, exportação com sugestão pendente e ArchUnit (controllers não acessam repositórios) | | `semana3-mvn-test.png` |
