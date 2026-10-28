# Atividade inicial: história e cenários BDD

## 1. Regra escolhida

**Regra 2:** um cenário válido tem título único na história, ao menos um passo Quando e um
passo Então, e segue a ordem Dado → Quando → Então. Passos E e Mas continuam o tipo
anterior e não podem iniciar o cenário.

## 2. Cenários escritos pelo grupo, sem IA

Transcritos como o grupo escreveu.

### Exemplo 1: Cadastro de cenário correto (Sucesso)

```gherkin
Funcionalidade: Criar cenário
  Cenário: Salvar um cenário com todos os passos certos
    Dado que eu tenho uma história chamada "Exportar feature" criada
    Quando eu crio um cenário colocando os passos na ordem "Dado", "Quando" e "Então"
    Então o sistema salva o cenário normalmente
    E marca que ele foi criado por um humano
```

### Exemplo 2: Esquecer de colocar o "Então" (Falha)

```gherkin
Funcionalidade: Criar cenário
  Cenário: Dar erro ao tentar salvar cenário sem o passo Então
    Dado que a Ana tem a história "Exportar feature" aberta
    Quando ela tenta salvar um cenário só com "Dado" e "Quando"
    Então o sistema recusa o cadastro
    E mostra um aviso dizendo que faltou o "Então"
```

### Exemplo 3: Rejeitar ideia da IA sem dar motivo (Falha)

```gherkin
Funcionalidade: Avaliar sugestão da IA
  Cenário: Não deixar rejeitar uma sugestão sem explicar o porquê
    Dado que tem uma sugestão da IA pendente na história "Login"
    Quando o analista clica em rejeitar mas não escolhe nenhum motivo
    Então o sistema barra a ação
    E a sugestão continua pendente do mesmo jeito
```

## 3. Ampliação pela IA

- **Ferramenta:** Claude (Anthropic)
- **Prompt:** "Revise os três cenários abaixo para a regra 2 do enunciado e sugira melhorias
  e novos cenários que cubram casos de borda ainda não tratados. Use Gherkin em português."

## 4. Decisão sobre cada sugestão

| # | Sugestão da IA | Decisão | Motivo |
|---|---|---|---|
| 1 | O exemplo 3 trata da regra 4 (sugestões), e a atividade pede três cenários de uma regra só. Trocar por "cenário iniciado por E é rejeitado" | ACEITA | O exemplo 3 será reaproveitado na regra 4, que é um dos testes obrigatórios |
| 2 | "Clica em rejeitar" descreve a tela; escrever de forma declarativa | ACEITA | Segue o slide "Cenários bons e ruins" |
| 3 | Incluir a narrativa da história (Como / Quero / Para) na Funcionalidade | ACEITA | Faltava a história em si |
| 4 | Rejeitar cenário com Então antes de Quando (fora de ordem) | ACEITA | Caso de borda da ordem Dado → Quando → Então que faltava |
| 5 | Rejeitar cenário com título igual ao de outro cenário da mesma história | ACEITA | Cobre a parte de título único da regra 2 |
| 6 | Rejeitar cenário iniciado por Mas | ACEITA | Complementa o caso do E |
| 7 | Aceitar cenário sem Dado, só com Quando e Então | ACEITA | A regra exige Quando e Então, não Dado. Dúvida registrada para o professor |
| 8 | "Dado que o sistema está funcionando corretamente" como contexto | REJEITADA (GENERICA) | Não descreve nenhum estado concreto |
| 9 | Rejeitar cenário que tem Dado e Quando, mas não tem Então | REJEITADA (DUPLICADA) | É o exemplo 2 com outro título |
| 10 | Permitir que Mas inicie o cenário quando expressar uma exceção | REJEITADA (INCORRETA) | Contradiz a regra 2 |
| 11 | Avisar o autor por e-mail quando o cenário for rejeitado | REJEITADA (FORA_DO_ESCOPO) | O sistema não envia e-mails |

## 5. Versão final, depois da ampliação

```gherkin
# language: pt
Funcionalidade: Cadastro de cenários
  Como analista
  Quero registrar os cenários de uma história
  Para documentar seus critérios de aceite

  Cenário: Salvar um cenário com todos os passos certos
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário com os passos Dado, Quando e Então, nesta ordem
    Então o cadastro é aceito
    E o cenário fica com origem HUMANO

  Cenário: Cenário sem o passo Então é rejeitado
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário só com os passos Dado e Quando
    Então o cadastro é rejeitado
    E a mensagem informa que falta o passo Então

  Cenário: Cenário iniciado por E é rejeitado
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário cujo primeiro passo é do tipo E
    Então o cadastro é rejeitado
    E a mensagem informa que o cenário não pode começar com E

  Cenário: Cenário iniciado por Mas é rejeitado
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário cujo primeiro passo é do tipo Mas
    Então o cadastro é rejeitado

  Cenário: Cenário fora de ordem é rejeitado
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário com os passos Dado, Então e Quando, nesta ordem
    Então o cadastro é rejeitado
    E a mensagem informa que o passo Quando está fora de ordem

  Cenário: Cenário com título repetido na história é rejeitado
    Dado a história "Exportar feature" com o cenário "Exportação com sucesso"
    Quando Ana cadastra outro cenário "Exportação com sucesso" na mesma história
    Então o cadastro é rejeitado
    E a mensagem informa título repetido

  Cenário: Cenário sem Dado é aceito
    Dado a história "Exportar feature" escrita por "Ana"
    Quando Ana cadastra um cenário só com os passos Quando e Então
    Então o cadastro é aceito
```

O exemplo 3 do grupo, reescrito no estilo declarativo, fica guardado para a regra 4:

```gherkin
  Cenário: Rejeição de sugestão sem motivo é recusada
    Dado uma sugestão pendente na história "Login"
    Quando o analista rejeita a sugestão sem informar motivo
    Então a rejeição é recusada
    E a sugestão continua pendente
```

Os cenários da versão final viraram casos de teste em `ValidadorCenarioTest` e requisições
em `requisicoes.http`.
