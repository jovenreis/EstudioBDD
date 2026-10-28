# Diagrama de classes

O GitHub renderiza o diagrama abaixo automaticamente (Mermaid).

```mermaid
classDiagram
    direction LR

    class Usuario {
        Long id
        String nome
        String email
    }

    class Projeto {
        Long id
        String nome
        String descricao
        String contextoDominio
        LocalDateTime dataCriacao
    }

    class HistoriaUsuario {
        Long id
        String titulo
        String papel
        String objetivo
        String beneficio
        Prioridade prioridade
        LocalDateTime criadaEm
        LocalDateTime atualizadaEm
    }

    class Cenario {
        Long id
        String titulo
        int posicao
        OrigemCenario origem
    }

    class Passo {
        Long id
        TipoPasso tipo
        String texto
        int posicao
    }

    class Sugestao {
        Long id
        String ferramenta
        String prompt
        String conteudo
        DecisaoSugestao decisao
        MotivoRejeicao motivoRejeicao
        String justificativa
        LocalDateTime registradaEm
        LocalDateTime decididaEm
    }

    class Prioridade {
        <<enumeration>>
        ALTA
        MEDIA
        BAIXA
    }
    class OrigemCenario {
        <<enumeration>>
        HUMANO
        SUGESTAO_IA
    }
    class TipoPasso {
        <<enumeration>>
        DADO
        QUANDO
        ENTAO
        E
        MAS
    }
    class DecisaoSugestao {
        <<enumeration>>
        PENDENTE
        ACEITA
        REJEITADA
    }
    class MotivoRejeicao {
        <<enumeration>>
        INCORRETA
        GENERICA
        DUPLICADA
        FORA_DO_ESCOPO
        OUTRO
    }

    Projeto "1" --> "0..*" HistoriaUsuario : historias
    HistoriaUsuario "0..*" --> "1" Usuario : autor
    HistoriaUsuario "1" *-- "0..*" Cenario : cenarios
    Cenario "1" *-- "0..*" Passo : passos (ordenados)
    HistoriaUsuario "1" *-- "0..*" Sugestao : sugestoes
    Sugestao "0..*" --> "1" Usuario : registradaPor
    Sugestao "0..*" --> "0..1" Usuario : decididaPor
```

## Decisões de modelagem

- **Passo é composição de Cenário**: `cascade = ALL` e `orphanRemoval = true`. Apagar um
  cenário apaga os passos; tirar um passo da lista também o apaga do banco.
- **Ordenação**: `Cenario` e `Passo` têm o campo `posicao`, e as coleções usam
  `@OrderBy("posicao ASC")`, então sempre voltam do banco na ordem certa.
- **Unicidade simples**: `Usuario.email` e `Projeto.nome` têm `unique = true`.
- **Unicidade composta**: o título da história é único por projeto
  (`uk_historia_titulo_projeto`) e o título do cenário é único por história
  (`uk_cenario_titulo_historia`). O serviço também valida antes de salvar, para devolver
  uma mensagem clara em vez de erro do banco.
- **Carregamento**: todos os `@ManyToOne` são `LAZY` (o padrão do JPA seria EAGER).
  Coleções `@OneToMany` já são `LAZY` por padrão. Assim, buscar uma história não traz o
  projeto e o autor inteiros sem necessidade.
- **Projeto → HistoriaUsuario sem cascata**: as histórias são criadas pelo serviço
  individualmente, com validação de título e autoria.
