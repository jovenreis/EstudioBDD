import { Link } from 'react-router-dom'
import { PRIORIDADES, plural, rotulo } from '../rotulos.js'

export default function CartaoHistoria({ historia }) {
  const pendentes = historia.quantidadeSugestoesPendentes

  return (
    <Link to={`/historias/${historia.id}`} className={`cartao prioridade-${historia.prioridade.toLowerCase()}`}>
      <div className="cartao-topo">
        <h3>{historia.titulo}</h3>
        <span className="prioridade">{rotulo(PRIORIDADES, historia.prioridade)}</span>
      </div>
      <div className="cartao-corpo">
        <p className="autor">por {historia.autorNome}</p>
        {historia.etapaNome && <p className="autor">Etapa: {historia.etapaNome}</p>}
        <ul className="contagens">
          <li>{plural(historia.quantidadeCenarios, 'cenário', 'cenários')}</li>
          <li className={pendentes > 0 ? 'tem-pendencia' : undefined}>
            {pendentes === 0
              ? 'nenhuma sugestão pendente'
              : plural(pendentes, 'sugestão pendente', 'sugestões pendentes')}
          </li>
        </ul>
      </div>
    </Link>
  )
}
