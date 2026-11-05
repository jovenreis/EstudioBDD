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
    this.erros = problema?.erros ?? {}
  }
}

async function requisitar(metodo, caminho, corpo) {
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
  if (resposta.status === 204) return null

  const tipo = resposta.headers.get('Content-Type') || ''
  return tipo.includes('json') ? resposta.json() : resposta.text()
}

export const api = {
  usuarios: () => requisitar('GET', '/api/usuarios'),
  projetos: () => requisitar('GET', '/api/projetos'),
  projeto: (id) => requisitar('GET', `/api/projetos/${id}`),
  historiasDoProjeto: (id, prioridade) =>
    requisitar('GET', `/api/projetos/${id}/historias${prioridade ? `?prioridade=${prioridade}` : ''}`),
}
