# Caminho da operação "cadastrar um cenário" pelas cinco camadas

Requisição de exemplo:

```http
POST /api/historias/1/cenarios
X-Usuario-Id: 1
Content-Type: application/json

{
  "titulo": "Login com dados corretos",
  "passos": [
    { "tipo": "DADO",   "texto": "o cliente cadastrado" },
    { "tipo": "QUANDO", "texto": "ele entra com e-mail e senha corretos" },
    { "tipo": "ENTAO",  "texto": "o acesso é liberado" }
  ]
}
```

## 1. Apresentação (SPA)

A tela de edição da história monta o JSON com o título e os passos na ordem em que o
analista escreveu, e envia com o id do usuário selecionado no cabeçalho `X-Usuario-Id`.
A SPA não valida a ordem dos passos: se o cenário estiver errado, ela só mostra a
mensagem que a API devolveu.

## 2. Controller (`CenarioController.criar`)

- O Spring converte o JSON em `CenarioRequest` (DTO).
- `@Valid` aplica o Bean Validation: título obrigatório, ao menos um passo, tipo e texto
  em cada passo. Se falhar, o `TratadorErros` devolve **400** com a lista de campos.
- `req.paraDominio()` transforma o DTO num objeto `Cenario` (ainda não salvo) com os passos.
- Chama `cenarioService.criar(historiaId, usuarioId, cenario)`.
- Converte o resultado em `CenarioResponse` e devolve **201 Created** com o cabeçalho
  `Location: /api/cenarios/{id}`.

O controller não acessa repositório nenhum.

## 3. Serviço (`CenarioService.criar`), dentro de uma transação

1. `historiaService.buscarParaAlterar` busca a história e confere se o usuário do cabeçalho
   é o autor (**regra 3**). Se não for, `AcessoNegadoException` → **403**.
2. Confere se o título do cenário já existe na história (**regra 2**). Se existir,
   `ConflitoException` → **409**.
3. `ValidadorCenario.validarPassos` confere a gramática (**regra 2**): ao menos um Quando
   e um Então, ordem Dado → Quando → Então, E/Mas não iniciam. Se falhar,
   `RegraNegocioException` → **422**.
4. `historia.adicionarCenario(cenario)` liga o cenário à história e define a posição.
5. `cenarioRepository.save(cenario)`.

Se qualquer passo lançar exceção, a transação é desfeita e nada é gravado.

## 4. Repositório (`CenarioRepository`)

Interface do Spring Data JPA, sem regra nenhuma. O `save` faz o `INSERT` do cenário e,
por causa do `cascade = ALL` em `Cenario.passos`, também insere os passos.

## 5. Domínio (`Cenario`, `Passo`)

Entidades JPA que definem as tabelas `cenario` e `passo`, as chaves estrangeiras e a
restrição de título único por história (`uk_cenario_titulo_historia`). O método
`adicionarPasso` numera a posição de cada passo, e o `@OrderBy("posicao ASC")` garante que
eles voltem do banco na mesma ordem.
