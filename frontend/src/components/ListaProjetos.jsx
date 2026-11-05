import { useEffect, useState } from 'react'
import { NavLink } from 'react-router-dom'
import { api } from '../api.js'

export default function ListaProjetos() {
  const [projetos, setProjetos] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    api.projetos().then(setProjetos).catch((e) => setErro(e.message))
  }, [])

  return (
    <nav aria-label="Projetos">
      <h2 className="lateral-titulo">Projetos</h2>
      {erro && <p className="erro-texto">{erro}</p>}
      {projetos === null && !erro && <p className="aviso">Carregando…</p>}
      {projetos?.length === 0 && <p className="aviso">Nenhum projeto cadastrado.</p>}
      <ul className="projetos">
        {projetos?.map((p) => (
          <li key={p.id}>
            <NavLink to={`/projetos/${p.id}`}>{p.nome}</NavLink>
          </li>
        ))}
      </ul>
    </nav>
  )
}
