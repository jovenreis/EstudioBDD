import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { PRIORIDADES, rotulo } from '../rotulos.js'
import MensagemErro from '../components/MensagemErro.jsx'

// Atividade bônus: uma coluna por etapa cadastrada no fluxo do projeto.
// Se uma etapa nova for cadastrada no banco, ela aparece aqui sem mudar o código.
export default function QuadroPagina() {
  const { id } = useParams()
  const [quadro, setQuadro] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    setQuadro(null)
    api.quadro(id).then(setQuadro).catch(setErro)
  }, [id])

  if (erro) return <MensagemErro erro={erro} />
  if (!quadro) return <p className="aviso">Carregando…</p>

  return (
    <section className="quadro-pagina">
      <Link to={`/projetos/${id}`} className="voltar">{quadro.projetoNome}</Link>
      <h1>Quadro</h1>
      <p className="aviso">Fluxo: {quadro.fluxoNome}</p>

      <div className="quadro" style={{ '--colunas': quadro.etapas.length }}>
        {quadro.etapas.map((etapa) => (
          <section key={etapa.id} className="coluna" aria-label={etapa.nome}>
            <header className="coluna-topo">
              <h2>{etapa.nome}</h2>
              <span className="aviso">{etapa.historias.length}</span>
            </header>
            {etapa.historias.map((h) => (
              <Link key={h.id} to={`/historias/${h.id}`} className={`mini-ficha prioridade-${h.prioridade.toLowerCase()}`}>
                <strong>{h.titulo}</strong>
                <span className="aviso">{h.autorNome}</span>
                <span className="prioridade">{rotulo(PRIORIDADES, h.prioridade)}</span>
              </Link>
            ))}
          </section>
        ))}
      </div>
    </section>
  )
}
