import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import CartaoHistoria from '../components/CartaoHistoria.jsx'
import { PRIORIDADES } from '../rotulos.js'

const FILTROS = [{ valor: '', rotulo: 'Todas' }, ...PRIORIDADES]

export default function ProjetoPagina() {
  const { id } = useParams()
  const [projeto, setProjeto] = useState(null)
  const [historias, setHistorias] = useState(null)
  const [prioridade, setPrioridade] = useState('')
  const [erro, setErro] = useState(null)

  useEffect(() => {
    setProjeto(null)
    setErro(null)
    setPrioridade('')
    api.projeto(id).then(setProjeto).catch((e) => setErro(e.message))
  }, [id])

  // o filtro é aplicado pela API (regra 6), não aqui
  useEffect(() => {
    setHistorias(null)
    api.historiasDoProjeto(id, prioridade).then(setHistorias).catch((e) => setErro(e.message))
  }, [id, prioridade])

  if (erro) return <p className="erro-texto">{erro}</p>
  if (!projeto) return <p className="aviso">Carregando…</p>

  return (
    <section>
      <header className="projeto-cabecalho">
        <h1>{projeto.nome}</h1>
        {projeto.descricao && <p className="descricao">{projeto.descricao}</p>}
        <div className="acoes">
          <Link to={`/projetos/${id}/historias/nova`} className="botao botao-principal">Nova história</Link>
          <Link to={`/projetos/${id}/resumo`} className="botao botao-discreto">Ver resumo</Link>
        </div>
        {projeto.contextoDominio && (
          <details className="glossario">
            <summary>Contexto do domínio</summary>
            <p>{projeto.contextoDominio}</p>
          </details>
        )}
      </header>

      <div className="filtro" role="group" aria-label="Filtrar por prioridade">
        {FILTROS.map((f) => (
          <button
            key={f.valor}
            type="button"
            className={prioridade === f.valor ? 'ativo' : undefined}
            aria-pressed={prioridade === f.valor}
            onClick={() => setPrioridade(f.valor)}
          >
            {f.rotulo}
          </button>
        ))}
      </div>

      {historias === null && <p className="aviso">Carregando histórias…</p>}
      {historias?.length === 0 && (
        <p className="aviso">
          {prioridade ? 'Nenhuma história com essa prioridade.' : 'Este projeto ainda não tem histórias.'}
        </p>
      )}
      <div className="historias">
        {historias?.map((h) => <CartaoHistoria key={h.id} historia={h} />)}
      </div>
    </section>
  )
}
