# Roteiro de verificação

Itens da seção 13 do enunciado. Para cada um: como verificar, entrada, resultado esperado
e resultado observado. A coluna "Observado" é preenchida pelo grupo ao executar o roteiro.

## Preparação

```bash
cd /workspaces/EstudioBDD
docker compose down -v && docker compose up -d      # banco zerado, com os dados fictícios
cd backend && mvn test                               # 30 testes (22 antes do bônus)
mvn spring-boot:run                                  # API na 8080
# em outro terminal
cd /workspaces/EstudioBDD/frontend && npm install && npm run dev   # SPA na 5173
```

Entrar na SPA com `ana.souza@exemplo.com` e senha `senha123`. Para agir como outro usuário,
clicar em **Sair** e entrar com `carlos.lima@exemplo.com` (mesma senha).

Dados fictícios: usuários 1 (Ana) e 2 (Carlos); projeto 1 "Loja" com as histórias
"Login" (autora Ana, 2 cenários, 1 sugestão pendente) e "Adicionar ao carrinho" (autor Carlos).

Executado em: 13/11/2026, por: Bruno Martin Magalhãe, Henry Ribeiro Reis e Pedro Pizane Stach Ferreira

## Histórias (regra 1)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 1 | SPA: Loja → Nova história | Título "Recuperar senha", Como "cliente", Quero "recuperar minha senha por e-mail", Para "voltar a acessar a loja" | História criada e aberta | História criada; a página da história "Recuperar senha" abriu com a narrativa e "Autor: Ana Souza" |  |
| 2 | SPA: Loja → Nova história | Título "Login" (já existe no projeto) | Recusada: "Já existe uma história com o título "Login" neste projeto" (409) | Recusada com a mensagem "Já existe uma história com o título "Login" neste projeto"; nada foi criado |  |
| 3 | SPA: Loja → Nova história | Campo "Para" vazio | Recusada com o campo inválido listado (400) | Recusada com "Um ou mais campos estão inválidos" e o campo "Informe o benefício (para...)" listado |  |

## Cenários e passos (regra 2)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 4 | SPA: história "Recuperar senha" → Novo cenário | Dado / Quando / Então preenchidos | Cenário salvo com os passos na ordem | Cenário salvo e exibido com Dado, Quando e Então na ordem |  |
| 5 | Idem | Passos Dado / Quando / E (sem Então) | Recusado: "O cenário precisa ter ao menos um passo Então" (422) | Recusado com "O cenário precisa ter ao menos um passo Então"; o cenário não foi salvo |  |
| 6 | Idem | Passos Dado / Então (sem Quando) | Recusado: "O cenário precisa ter ao menos um passo Quando" (422) | Recusado com "O cenário precisa ter ao menos um passo Quando" |  |
| 7 | Idem | Passos Dado / Então / Quando | Recusado: passo fora de ordem (422) | Recusado com a mensagem de passo fora de ordem (Dado → Quando → Então) |  |
| 8 | Idem | Primeiro passo do tipo E | Recusado: "O cenário não pode começar com um passo E" (422) | Recusado com "O cenário não pode começar com um passo E" |  |
| 9 | SPA: editar o cenário do item 4 | Adicionar um passo E e subir ele para antes do Então; salvar | Passos exibidos na nova ordem, inclusive depois de recarregar a página (F5) | Passos salvos como Dado, Quando, E, Então; a ordem se manteve depois do F5 |  |

## Autoria (regra 3)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 10 | SPA: usuário "Carlos Lima", abrir "Login" (da Ana) → Editar história → Salvar | Qualquer alteração | Recusada: "Somente o autor da história pode alterá-la ou excluí-la" (403) | Recusada com "Somente o autor da história pode alterá-la ou excluí-la"; a história não mudou |  |
| 11 | SPA: usuário "Carlos Lima", "Login" → Excluir história → Confirmar | — | Recusada com a mesma mensagem (403) | Recusada com a mesma mensagem; a história continuou cadastrada |  |
| 12 | SPA: usuário "Carlos Lima", "Login" → Excluir um cenário | — | Recusada (403) | Recusada com a mensagem de autoria; o cenário continuou cadastrado |  |

## Sugestões e exportação (regras 4 e 5)

Fazer na ordem: a exportação bloqueada (item 14) precisa acontecer antes de as sugestões
serem decididas.

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 13 | SPA: "Login" → Registrar sugestão | Ferramenta, prompt e conteúdo | Sugestão aparece como Pendente; agora há 2 pendentes | Sugestão registrada como Pendente; a história passou a mostrar 2 sugestões pendentes |  |
| 14 | SPA: "Login" → Exportar .feature | — | Recusada: "A história tem 2 sugestões pendentes…" (422) | Recusada com "A história tem 2 sugestões pendentes. Aceite ou rejeite antes de exportar" |  |
| 15 | SPA: a sugestão nova → Rejeitar → Confirmar rejeição | Sem escolher motivo | Recusada: "Informe o motivo da rejeição…" (422); continua Pendente | Recusada com "Informe o motivo da rejeição (...)"; a sugestão continuou Pendente |  |
| 16 | Idem | Motivo "Genérica" | Sugestão passa a Rejeitada (Genérica) | Sugestão passou a Rejeitada (Genérica) por Ana Souza |  |
| 17 | SPA: a sugestão da carga inicial → Aceitar → Confirmar aceite | Justificativa opcional | Sugestão passa a Aceita; os cenários da história não mudam | Sugestão passou a Aceita por Ana Souza; os cenários do Login continuaram os mesmos |  |
| 18 | `requisicoes.http`: "Alterar sugestão já decidida" | Rejeitar a sugestão 1, já aceita | Recusada: "A sugestão já foi aceita e não pode ser alterada" (422) | Resposta 422 com "A sugestão já foi aceita e não pode ser alterada" |  |
| 19 | SPA: projeto "Estúdio BDD" → "Exportar feature" (sem cenários) → Exportar .feature | — | Recusada: "A história precisa ter ao menos um cenário para ser exportada" (422) | Recusada com "A história precisa ter ao menos um cenário para ser exportada" |  |
| 20 | SPA: "Login", já sem pendências → Exportar .feature → Baixar arquivo | — | Texto em Gherkin com `# language: pt`, narrativa e cenários na ordem; arquivo `login.feature` baixado | Texto Gherkin exibido com "# language: pt", narrativa e cenários na ordem; arquivo login.feature baixado | docs/login.feature |

## Consulta e resumo (regras 6 e 7)

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 21 | SPA: Loja, filtro "Alta" | — | Só as histórias de prioridade alta, com quantidade de cenários e de sugestões pendentes | Só a história "Login" apareceu, com 2 cenários e nenhuma sugestão pendente |  |
| 22 | SPA: Loja → Ver resumo | — | Números iguais aos dados cadastrados (conferir contando na tela das histórias) | 3 histórias; cenários: 4 escritos pelo analista e 0 da IA; sugestões: 0 pendentes, 1 aceita, 1 rejeitada; motivo: Genérica (1). Números conferem com os dados cadastrados |  |

## Persistência

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 23 | Parar a API (Ctrl + C) e subir de novo | — | Tudo o que foi cadastrado continua lá; a carga de dados fictícios não roda de novo | A carga de dados fictícios não rodou de novo; a história "Recuperar senha" e as decisões das sugestões continuaram lá após reiniciar |  |

## Testes automatizados

Na primeira execução (antes do login) foram 18 testes passando; com o login são 22.

| # | Como verificar | Esperado | Observado | Evidência |
|---|---|---|---|---|
| 24 | `mvn test` | 22 testes, 0 falhas: cenário sem Então rejeitado, rejeição sem motivo, exportação com sugestão pendente e ArchUnit (controllers não acessam repositórios), além dos testes de login | Tests run: 22, Failures: 0, Errors: 0; BUILD SUCCESS |  |

## Login

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 25 | SPA: abrir sem estar logado | — | Aparece a tela de login; nenhuma outra tela abre | Abriu a tela de login; mesmo acessando /projetos/1 direto, nenhuma outra tela apareceu |  |
| 26 | SPA: tela de login | `ana.souza@exemplo.com` e senha `errada` | Recusado: "E-mail ou senha incorretos" (401) | Recusado com "E-mail ou senha incorretos"; continuou na tela de login |  |
| 27 | SPA: tela de login | `nao.existe@exemplo.com` e senha `senha123` | A mesma mensagem do item 26 (não revela se o e-mail existe) | Recusado com a mesma mensagem do item 26 ("E-mail ou senha incorretos") |  |
| 28 | SPA: tela de login | `ana.souza@exemplo.com` e senha `senha123` | Entra; o topo mostra "Ana Souza" e o botão Sair | Entrou; o topo mostrou "Ana Souza" e o botão Sair; continuou logado depois do F5 |  |
| 29 | SPA: clicar em Sair | — | Volta para a tela de login | Voltou para a tela de login |  |
| 30 | Banco: `docker exec -it estudio-bdd-db psql -U estudio -d estudiobdd -c "select email, senha_hash from usuario"` | — | A coluna `senha_hash` mostra hashes começando com `$2a$`, nunca a senha em texto | A coluna senha_hash mostrou hashes começando com "$2a$10$", diferentes entre si; a senha em texto não aparece |  |

## Atividade bônus: fluxo de aprovação

Fazer com o banco zerado (`docker compose down -v && docker compose up -d`), no projeto
**Estúdio BDD**, que usa o fluxo. Histórias: "Exportar feature" (id 3, autora Ana, sem cenários)
e "Registrar sugestão" (id 4, autora Beatriz, 1 cenário). Ana é analista; Beatriz é revisora.

| # | Como verificar | Entrada | Esperado | Observado | Evidência |
|---|---|---|---|---|---|
| 31 | SPA (Ana): Estúdio BDD → Ver quadro | — | Quatro colunas vindas da API (Rascunho, Revisão por IA, Revisão humana, Aprovada); as duas histórias em Rascunho | Não executado antes da entrega (falta de tempo) | |
| 32 | `requisicoes.http`: "Transição inválida" | Beatriz aprova a história 3, que está em Rascunho | Recusada: "A ação "Aprovar" não está disponível na etapa "Rascunho"" (422) | Não executado antes da entrega (falta de tempo) | |
| 33 | SPA (Ana): "Exportar feature" → Enviar para revisão → Confirmar | História sem cenários | Recusada: precisa de ao menos um cenário (422) | Não executado antes da entrega (falta de tempo) | |
| 34 | SPA (Ana): criar um cenário válido → Solicitar revisão por IA → registrar uma sugestão → Concluir curadoria | Sugestão ainda pendente | Recusada: "Aceite ou rejeite as sugestões pendentes antes de "Concluir curadoria"" (422) | Não executado antes da entrega (falta de tempo) | |
| 35 | SPA (Ana): decidir a sugestão → Concluir curadoria → Enviar para revisão | — | História passa por Rascunho e chega a Revisão humana; aparece "Edição bloqueada nesta etapa" e Ana não tem ações | Não executado antes da entrega (falta de tempo) | |
| 36 | SPA (Ana): na história em Revisão humana → Editar história → Salvar | Qualquer alteração | Recusada: "A história está na etapa "Revisão humana", que não permite edição" (422) | Não executado antes da entrega (falta de tempo) | |
| 37 | SPA (Beatriz): "Exportar feature" → Devolver → Confirmar | Sem comentário | Recusada: "Informe um comentário para "Devolver"" (422) | Não executado antes da entrega (falta de tempo) | |
| 38 | SPA (Beatriz): "Exportar feature" → Aprovar → Confirmar | — | História vai para Aprovada; o histórico lista as transições com usuário e data | Não executado antes da entrega (falta de tempo) | |
| 39 | SPA (Ana): "Registrar sugestão" (da Beatriz) → Enviar para revisão; depois SPA (Beatriz) → Aprovar | — | Recusada: "A ação "Aprovar" precisa ser feita por um usuário diferente do autor da história" (422) | Não executado antes da entrega (falta de tempo) | |
| 40 | SPA: Exportar .feature em "Registrar sugestão" (Revisão humana) e em "Exportar feature" (Aprovada) | — | Primeira recusada: só exporta na etapa final (422); segunda gera o .feature (200) | Não executado antes da entrega (falta de tempo) | |
| 41 | `requisicoes.http`: seção "Bônus: nova etapa só com dados" (3 requisições), depois SPA → Ver quadro | — | Sem reiniciar nem mudar código: o quadro passa a ter 5 colunas, e Beatriz vê "Liberar para desenvolvimento" na história aprovada | Não executado antes da entrega (falta de tempo) | |
| 42 | `mvn test` | — | 30 testes, 0 falhas (inclui `FluxoServiceTest`) | Tests run: 30, Failures: 0; BUILD SUCCESS | |
