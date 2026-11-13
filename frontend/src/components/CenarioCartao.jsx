import { useState } from 'react'
import { api } from '../api.js'
import { TIPOS_PASSO, rotulo } from '../rotulos.js'
import BotaoExcluir from './BotaoExcluir.jsx'
import CenarioEditor from './CenarioEditor.jsx'
import MensagemErro from './MensagemErro.jsx'

export default function CenarioCartao({ cenario, onMudou }) {
  const [editando, setEditando] = useState(false)
  const [erro, setErro] = useState(null)

  async function salvar(dados) {
    await api.atualizarCenario(cenario.id, dados)
    setEditando(false)
    onMudou()
  }

  async function excluir() {
    setErro(null)
    try {
      await api.excluirCenario(cenario.id)
      onMudou()
    } catch (err) {
      setErro(err)
    }
  }

  if (editando) {
    return (
      <div className="cenario">
        <CenarioEditor inicial={cenario} onSalvar={salvar} onCancelar={() => setEditando(false)} />
      </div>
    )
  }

  return (
    <div className="cenario">
      <div className="cenario-topo">
        <h3>
          <span className="palavra-chave">Cenário:</span> {cenario.titulo}
        </h3>
        {cenario.origem === 'SUGESTAO_IA' && <span className="selo">Sugestão da IA</span>}
      </div>

      <ol className="passos">
        {cenario.passos.map((p) => (
          <li key={p.id} className={p.tipo === 'E' || p.tipo === 'MAS' ? 'continuacao' : undefined}>
            <span className="palavra-chave">{rotulo(TIPOS_PASSO, p.tipo)}</span> {p.texto}
          </li>
        ))}
      </ol>

      <MensagemErro erro={erro} />

      <div className="acoes">
        <button type="button" className="botao botao-discreto" onClick={() => setEditando(true)}>Editar</button>
        <BotaoExcluir onConfirmar={excluir} />
      </div>
    </div>
  )
}
