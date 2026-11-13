import { useState } from 'react'
import { ORIGENS, TIPOS_PASSO } from '../rotulos.js'
import MensagemErro from './MensagemErro.jsx'

let proximaChave = 1
const novaChave = () => `novo-${proximaChave++}`

function passoVazio(tipo) {
  return { chave: novaChave(), tipo, texto: '' }
}

// Edita o título, a origem e a lista de passos de um cenário.
// A ordem da lista é a ordem enviada para a API; quem valida a gramática é o backend (regra 2).
export default function CenarioEditor({ inicial, onSalvar, onCancelar }) {
  const [titulo, setTitulo] = useState(inicial?.titulo ?? '')
  const [origem, setOrigem] = useState(inicial?.origem ?? 'HUMANO')
  const [passos, setPassos] = useState(
    inicial
      ? inicial.passos.map((p) => ({ chave: `p-${p.id}`, tipo: p.tipo, texto: p.texto }))
      : [passoVazio('DADO'), passoVazio('QUANDO'), passoVazio('ENTAO')],
  )
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState(null)

  function alterarPasso(indice, campo, valor) {
    setPassos(passos.map((p, i) => (i === indice ? { ...p, [campo]: valor } : p)))
  }

  function mover(indice, direcao) {
    const destino = indice + direcao
    if (destino < 0 || destino >= passos.length) return
    const copia = [...passos]
    ;[copia[indice], copia[destino]] = [copia[destino], copia[indice]]
    setPassos(copia)
  }

  function remover(indice) {
    setPassos(passos.filter((_, i) => i !== indice))
  }

  async function salvar(e) {
    e.preventDefault()
    setSalvando(true)
    setErro(null)
    try {
      await onSalvar({
        titulo,
        origem,
        passos: passos.map(({ tipo, texto }) => ({ tipo, texto })),
      })
    } catch (err) {
      setErro(err)
      setSalvando(false)
    }
  }

  return (
    <form className="cenario-editor" onSubmit={salvar}>
      <label>
        Título do cenário
        <input value={titulo} onChange={(e) => setTitulo(e.target.value)} />
      </label>

      <label>
        Origem
        <select value={origem} onChange={(e) => setOrigem(e.target.value)}>
          {ORIGENS.map((o) => (
            <option key={o.valor} value={o.valor}>{o.rotulo}</option>
          ))}
        </select>
      </label>

      <ol className="passos-editor">
        {passos.map((passo, i) => (
          <li key={passo.chave}>
            <select
              aria-label={`Tipo do passo ${i + 1}`}
              value={passo.tipo}
              onChange={(e) => alterarPasso(i, 'tipo', e.target.value)}
            >
              {TIPOS_PASSO.map((t) => (
                <option key={t.valor} value={t.valor}>{t.rotulo}</option>
              ))}
            </select>
            <input
              aria-label={`Texto do passo ${i + 1}`}
              value={passo.texto}
              onChange={(e) => alterarPasso(i, 'texto', e.target.value)}
            />
            <span className="passo-botoes">
              <button type="button" onClick={() => mover(i, -1)} disabled={i === 0} aria-label={`Subir passo ${i + 1}`}>↑</button>
              <button type="button" onClick={() => mover(i, 1)} disabled={i === passos.length - 1} aria-label={`Descer passo ${i + 1}`}>↓</button>
              <button type="button" onClick={() => remover(i)} aria-label={`Remover passo ${i + 1}`}>✕</button>
            </span>
          </li>
        ))}
      </ol>

      <button type="button" className="botao botao-discreto" onClick={() => setPassos([...passos, passoVazio('E')])}>
        Adicionar passo
      </button>

      <MensagemErro erro={erro} />

      <div className="acoes">
        <button type="submit" className="botao botao-principal" disabled={salvando}>Salvar cenário</button>
        <button type="button" className="botao botao-discreto" onClick={onCancelar}>Cancelar</button>
      </div>
    </form>
  )
}
