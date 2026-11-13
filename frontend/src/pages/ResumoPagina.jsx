import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { DECISOES, MOTIVOS, ORIGENS, rotulo } from '../rotulos.js'
import MensagemErro from '../components/MensagemErro.jsx'

// Uma linha com rótulo, quantidade e uma barra proporcional ao maior valor do grupo.
function Barras({ itens }) {
  const maior = Math.max(1, ...itens.map((i) => i.quantidade))
  return (
    <ul className="barras">
      {itens.map((i) => (
        <li key={i.rotulo}>
          <span className="barra-rotulo">{i.rotulo}</span>
          <span className="barra-trilho">
            <span className="barra" style={{ width: `${(i.quantidade / maior) * 100}%` }} />
          </span>
          <span className="barra-valor">{i.quantidade}</span>
        </li>
      ))}
    </ul>
  )
}

// Os números vêm prontos da API (regra 7); a tela só organiza.
export default function ResumoPagina() {
  const { id } = useParams()
  const [resumo, setResumo] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    api.resumo(id).then(setResumo).catch(setErro)
  }, [id])

  if (erro) return <MensagemErro erro={erro} />
  if (!resumo) return <p className="aviso">Carregando…</p>

  const origens = ORIGENS.map((o) => ({ rotulo: o.rotulo, quantidade: resumo.cenariosPorOrigem[o.valor] ?? 0 }))
  const decisoes = DECISOES.map((d) => ({ rotulo: d.rotulo, quantidade: resumo.sugestoesPorDecisao[d.valor] ?? 0 }))
  const motivos = resumo.motivosRejeicao.map((m) => ({ rotulo: rotulo(MOTIVOS, m.motivo), quantidade: m.quantidade }))

  return (
    <section className="resumo">
      <Link to={`/projetos/${id}`} className="voltar">{resumo.projetoNome}</Link>
      <h1>Resumo do projeto</h1>

      <p className="total">
        <strong>{resumo.totalHistorias}</strong> {resumo.totalHistorias === 1 ? 'história' : 'histórias'}
      </p>

      <div className="resumo-grupos">
        <div>
          <h2>Cenários por origem</h2>
          <Barras itens={origens} />
        </div>
        <div>
          <h2>Sugestões por decisão</h2>
          <Barras itens={decisoes} />
        </div>
        <div>
          <h2>Motivos de rejeição</h2>
          {motivos.length === 0 ? <p className="aviso">Nenhuma sugestão rejeitada.</p> : <Barras itens={motivos} />}
        </div>
      </div>
    </section>
  )
}
