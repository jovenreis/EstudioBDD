// Toda comunicação com a API passa por aqui.
// A SPA não valida regra de negócio: ela envia o pedido e mostra a mensagem que a API devolver.

const BASE = import.meta.env.VITE_API_URL ?? ''

let usuarioAtual = null

export function definirUsuario(id) {
  usuarioAtual = id
}

export class ErroApi extends Error {
  constructor(status, problema) {
    super(problema?.detail || `Erro ${status} ao falar com a API`)
    this.status = status
    // erros de campo (400 do Bean Validation): { "titulo": "Informe o título", ... }
    this.erros = problema?.erros ?? {}
  }
}

// faz a requisição e transforma qualquer resposta de erro em ErroApi
async function enviar(metodo, caminho, corpo) {
  const headers = {}
  if (corpo !== undefined) headers['Content-Type'] = 'application/json'
  if (usuarioAtual) headers['X-Usuario-Id'] = String(usuarioAtual)

  let resposta
  try {
    resposta = await fetch(BASE + caminho, {
      method: metodo,
      headers,
      body: corpo === undefined ? undefined : JSON.stringify(corpo),
    })
  } catch {
    throw new ErroApi(0, { detail: 'Não foi possível conectar à API. Confira se ela está rodando na porta 8080.' })
  }

  if (!resposta.ok) {
    const problema = await resposta.json().catch(() => null)
    throw new ErroApi(resposta.status, problema)
  }
  return resposta
}

async function requisitar(metodo, caminho, corpo) {
  const resposta = await enviar(metodo, caminho, corpo)
  if (resposta.status === 204) return null
  const tipo = resposta.headers.get('Content-Type') || ''
  return tipo.includes('json') ? resposta.json() : resposta.text()
}

// lê o nome do arquivo de: attachment; filename*=UTF-8''login.feature
function nomeDoArquivo(cabecalho) {
  const utf8 = /filename\*=UTF-8''([^;]+)/i.exec(cabecalho || '')
  if (utf8) return decodeURIComponent(utf8[1])
  const simples = /filename="?([^";]+)"?/i.exec(cabecalho || '')
  return simples ? simples[1] : 'historia.feature'
}

export const api = {
  login: (email, senha) => requisitar('POST', '/api/login', { email, senha }),
  usuarios: () => requisitar('GET', '/api/usuarios'),

  projetos: () => requisitar('GET', '/api/projetos'),
  projeto: (id) => requisitar('GET', `/api/projetos/${id}`),
  resumo: (id) => requisitar('GET', `/api/projetos/${id}/resumo`),
  historiasDoProjeto: (id, prioridade) =>
    requisitar('GET', `/api/projetos/${id}/historias${prioridade ? `?prioridade=${prioridade}` : ''}`),

  historia: (id) => requisitar('GET', `/api/historias/${id}`),
  criarHistoria: (projetoId, dados) => requisitar('POST', `/api/projetos/${projetoId}/historias`, dados),
  atualizarHistoria: (id, dados) => requisitar('PUT', `/api/historias/${id}`, dados),
  excluirHistoria: (id) => requisitar('DELETE', `/api/historias/${id}`),

  criarCenario: (historiaId, dados) => requisitar('POST', `/api/historias/${historiaId}/cenarios`, dados),
  atualizarCenario: (id, dados) => requisitar('PUT', `/api/cenarios/${id}`, dados),
  excluirCenario: (id) => requisitar('DELETE', `/api/cenarios/${id}`),

  sugestoes: (historiaId, decisao) =>
    requisitar('GET', `/api/historias/${historiaId}/sugestoes${decisao ? `?decisao=${decisao}` : ''}`),
  registrarSugestao: (historiaId, dados) => requisitar('POST', `/api/historias/${historiaId}/sugestoes`, dados),
  aceitarSugestao: (id, justificativa) => requisitar('POST', `/api/sugestoes/${id}/aceitar`, { justificativa }),
  rejeitarSugestao: (id, motivo, justificativa) =>
    requisitar('POST', `/api/sugestoes/${id}/rejeitar`, { motivo: motivo || null, justificativa }),

  // atividade bônus: fluxo de aprovação
  quadro: (projetoId) => requisitar('GET', `/api/projetos/${projetoId}/quadro`),
  acoes: (historiaId) => requisitar('GET', `/api/historias/${historiaId}/acoes`),
  executarAcao: (historiaId, transicaoId, comentario) =>
    requisitar('POST', `/api/historias/${historiaId}/acoes/${transicaoId}`, { comentario }),
  historico: (historiaId) => requisitar('GET', `/api/historias/${historiaId}/historico`),

  exportarFeature: async (historiaId) => {
    const resposta = await enviar('GET', `/api/historias/${historiaId}/feature`)
    return {
      texto: await resposta.text(),
      nome: nomeDoArquivo(resposta.headers.get('Content-Disposition')),
    }
  },
}
