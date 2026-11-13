import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { DECISOES, MOTIVOS, rotulo } from '../rotulos.js'
import MensagemErro from './MensagemErro.jsx'

const FILTROS = [{ valor: '', rotulo: 'Todas' }, ...DECISOES]

function Decisao({ sugestao, onMudou }) {
  const [acao, setAcao] = useState(null) // null | 'aceitar' | 'rejeitar'
  const [motivo, setMotivo] = useState('')
  const [justificativa, setJustificativa] = useState('')
  const [erro, setErro] = useState(null)

  async function confirmar(e) {
    e.preventDefault()
    setErro(null)
    try {
      if (acao === 'aceitar') {
        await api.aceitarSugestao(sugestao.id, justificativa)
      } else {
        // o motivo vazio vai como null de propósito: quem recusa é a API (regra 4)
        await api.rejeitarSugestao(sugestao.id, motivo, justificativa)
      }
      onMudou()
    } catch (err) {
      setErro(err)
    }
  }

  if (!acao) {
    return (
      <div className="acoes">
        <button type="button" className="botao botao-principal" onClick={() => setAcao('aceitar')}>Aceitar</button>
        <button type="button" className="botao botao-discreto" onClick={() => setAcao('rejeitar')}>Rejeitar</button>
      </div>
    )
  }

  return (
    <form className="decisao" onSubmit={confirmar}>
      {acao === 'rejeitar' && (
        <label>
          Motivo
          <select value={motivo} onChange={(e) => setMotivo(e.target.value)}>
            <option value="">Escolha um motivo</option>
            {MOTIVOS.map((m) => (
              <option key={m.valor} value={m.valor}>{m.rotulo}</option>
            ))}
          </select>
        </label>
      )}
      <label>
        Justificativa {acao === 'aceitar' && '(opcional)'}
        <textarea rows={2} value={justificativa} onChange={(e) => setJustificativa(e.target.value)} />
      </label>

      <MensagemErro erro={erro} />

      <div className="acoes">
        <button type="submit" className="botao botao-principal">
          {acao === 'aceitar' ? 'Confirmar aceite' : 'Confirmar rejeição'}
        </button>
        <button type="button" className="botao botao-discreto" onClick={() => setAcao(null)}>Cancelar</button>
      </div>
    </form>
  )
}

function NovaSugestao({ historiaId, onRegistrada }) {
  const [aberto, setAberto] = useState(false)
  const [dados, setDados] = useState({ ferramenta: '', prompt: '', conteudo: '' })
  const [erro, setErro] = useState(null)

  async function registrar(e) {
    e.preventDefault()
    setErro(null)
    try {
      await api.registrarSugestao(historiaId, dados)
      setDados({ ferramenta: '', prompt: '', conteudo: '' })
      setAberto(false)
      onRegistrada()
    } catch (err) {
      setErro(err)
    }
  }

  if (!aberto) {
    return (
      <button type="button" className="botao botao-principal" onClick={() => setAberto(true)}>
        Registrar sugestão
      </button>
    )
  }

  const alterar = (campo) => (e) => setDados({ ...dados, [campo]: e.target.value })

  return (
    <form className="formulario nova-sugestao" onSubmit={registrar}>
      <label>
        Ferramenta de IA
        <input value={dados.ferramenta} onChange={alterar('ferramenta')} placeholder="ChatGPT, Claude, Copilot…" />
      </label>
      <label>
        Prompt utilizado
        <textarea rows={2} value={dados.prompt} onChange={alterar('prompt')} />
      </label>
      <label>
        Sugestão recebida
        <textarea rows={4} value={dados.conteudo} onChange={alterar('conteudo')} />
      </label>

      <MensagemErro erro={erro} />

      <div className="acoes">
        <button type="submit" className="botao botao-principal">Registrar</button>
        <button type="button" className="botao botao-discreto" onClick={() => setAberto(false)}>Cancelar</button>
      </div>
    </form>
  )
}

// Painel exibido ao lado da história: lista, registra e decide sugestões.
export default function PainelSugestoes({ historiaId, onMudou }) {
  const [filtro, setFiltro] = useState('')
  const [sugestoes, setSugestoes] = useState(null)
  const [erro, setErro] = useState(null)

  const carregar = useCallback(() => {
    api.sugestoes(historiaId, filtro).then(setSugestoes).catch(setErro)
  }, [historiaId, filtro])

  useEffect(carregar, [carregar])

  function aposMudanca() {
    carregar()
    onMudou()
  }

  return (
    <aside className="painel-sugestoes" aria-label="Sugestões da IA">
      <h2>Sugestões da IA</h2>

      <div className="filtro" role="group" aria-label="Filtrar por decisão">
        {FILTROS.map((f) => (
          <button
            key={f.valor}
            type="button"
            className={filtro === f.valor ? 'ativo' : undefined}
            aria-pressed={filtro === f.valor}
            onClick={() => setFiltro(f.valor)}
          >
            {f.rotulo}
          </button>
        ))}
      </div>

      <NovaSugestao historiaId={historiaId} onRegistrada={aposMudanca} />

      <MensagemErro erro={erro} />
      {sugestoes?.length === 0 && <p className="aviso">Nenhuma sugestão aqui.</p>}

      <ul className="sugestoes">
        {sugestoes?.map((s) => (
          <li key={s.id} className={`sugestao decisao-${s.decisao.toLowerCase()}`}>
            <div className="sugestao-topo">
              <span className="selo">{rotulo(DECISOES, s.decisao)}</span>
              <span className="aviso">{s.ferramenta}, registrada por {s.registradaPorNome}</span>
            </div>

            <p className="sugestao-conteudo">{s.conteudo}</p>

            <details>
              <summary>Prompt</summary>
              <p>{s.prompt}</p>
            </details>

            {s.decisao === 'PENDENTE' ? (
              <Decisao sugestao={s} onMudou={aposMudanca} />
            ) : (
              <p className="aviso">
                {s.decisao === 'ACEITA' ? 'Aceita' : `Rejeitada (${rotulo(MOTIVOS, s.motivoRejeicao)})`} por {s.decididaPorNome}
                {s.justificativa && `: ${s.justificativa}`}
              </p>
            )}
          </li>
        ))}
      </ul>
    </aside>
  )
}
