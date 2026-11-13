// Textos exibidos na tela para os valores dos enums da API.

export const PRIORIDADES = [
  { valor: 'ALTA', rotulo: 'Alta' },
  { valor: 'MEDIA', rotulo: 'Média' },
  { valor: 'BAIXA', rotulo: 'Baixa' },
]

export const TIPOS_PASSO = [
  { valor: 'DADO', rotulo: 'Dado' },
  { valor: 'QUANDO', rotulo: 'Quando' },
  { valor: 'ENTAO', rotulo: 'Então' },
  { valor: 'E', rotulo: 'E' },
  { valor: 'MAS', rotulo: 'Mas' },
]

export const ORIGENS = [
  { valor: 'HUMANO', rotulo: 'Escrito pelo analista' },
  { valor: 'SUGESTAO_IA', rotulo: 'Ajustado a partir de sugestão da IA' },
]

export const DECISOES = [
  { valor: 'PENDENTE', rotulo: 'Pendente' },
  { valor: 'ACEITA', rotulo: 'Aceita' },
  { valor: 'REJEITADA', rotulo: 'Rejeitada' },
]

export const MOTIVOS = [
  { valor: 'INCORRETA', rotulo: 'Incorreta' },
  { valor: 'GENERICA', rotulo: 'Genérica' },
  { valor: 'DUPLICADA', rotulo: 'Duplicada' },
  { valor: 'FORA_DO_ESCOPO', rotulo: 'Fora do escopo' },
  { valor: 'OUTRO', rotulo: 'Outro' },
]

export function rotulo(lista, valor) {
  return lista.find((item) => item.valor === valor)?.rotulo ?? valor
}

export function plural(n, singular, pluralTexto) {
  return `${n} ${n === 1 ? singular : pluralTexto}`
}
