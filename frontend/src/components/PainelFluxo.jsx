import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import MensagemErro from './MensagemErro.jsx'

function dataHora(texto) {
  return new Date(texto).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

// Atividade bônus. Os botões vêm de GET /acoes: a tela não sabe quais etapas existem.
// As condições (papel, comentário, autor, pendências) são conferidas pela API.
export default function PainelFluxo({ historia, onMudou }) {
  const [acoes, setAcoes] = useState([])
  const [historico, setHistorico] = useState([])
  const [escolhida, setEscolhida] = useState(null)
  const [comentario, setComentario] = useState('')
  const [erro, setErro] = useState(null)

  const carregar = useCallback(() => {
    api.acoes(historia.id).then(setAcoes).catch(setErro)
    api.historico(historia.id).then(setHistorico).catch(setErro)
  }, [historia.id])

  // recarrega quando a etapa muda
  useEffect(carregar, [carregar, historia.etapaNome])

  async function executar(e) {
    e.preventDefault()
    setErro(null)
    try {
      await api.executarAcao(historia.id, escolhida.transicaoId, comentario)
      setEscolhida(null)
      setComentario('')
      onMudou()
    } catch (err) {
      setErro(err)
    }
  }

  return (
    <section className="painel-fluxo" aria-label="Fluxo de aprovação">
      <div className="fluxo-topo">
        <span className="aviso">Etapa atual</span>
        <strong className="etapa-atual">{historia.etapaNome}</strong>
        {!historia.podeEditar && <span className="selo">Edição bloqueada nesta etapa</span>}
      </div>

      {acoes.length === 0 ? (
        <p className="aviso">Nenhuma ação disponível para você nesta etapa.</p>
      ) : (
        <div className="acoes">
          {acoes.map((a) => (
            <button
              key={a.transicaoId}
              type="button"
              className={`botao ${escolhida?.transicaoId === a.transicaoId ? 'botao-principal' : 'botao-discreto'}`}
              onClick={() => {
                setEscolhida(a)
                setErro(null)
              }}
            >
              {a.nomeAcao}
            </button>
          ))}
        </div>
      )}

      {escolhida && (
        <form className="decisao" onSubmit={executar}>
          <p>
            <strong>{escolhida.nomeAcao}</strong>: a história vai para <strong>{escolhida.destino}</strong>.
          </p>
          <label>
            Comentário {escolhida.exigeComentario ? '(obrigatório)' : '(opcional)'}
            <textarea rows={2} value={comentario} onChange={(e) => setComentario(e.target.value)} />
          </label>
          <div className="acoes">
            <button type="submit" className="botao botao-principal">Confirmar</button>
            <button type="button" className="botao botao-discreto" onClick={() => setEscolhida(null)}>Cancelar</button>
          </div>
        </form>
      )}

      <MensagemErro erro={erro} />

      {historico.length > 0 && (
        <details className="historico">
          <summary>Histórico ({historico.length})</summary>
          <ol>
            {historico.map((h) => (
              <li key={h.id}>
                <span className="aviso">{dataHora(h.dataHora)}</span> {h.usuarioNome}: <strong>{h.acao}</strong>{' '}
                ({h.origem} → {h.destino})
                {h.comentario && <span className="aviso">: “{h.comentario}”</span>}
              </li>
            ))}
          </ol>
        </details>
      )}
    </section>
  )
}
