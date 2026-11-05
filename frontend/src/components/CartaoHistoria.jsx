const ROTULO_PRIORIDADE = { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }

function plural(n, singular, pluralTexto) {
  return `${n} ${n === 1 ? singular : pluralTexto}`
}

export default function CartaoHistoria({ historia }) {
  const pendentes = historia.quantidadeSugestoesPendentes

  return (
    <article className={`cartao prioridade-${historia.prioridade.toLowerCase()}`}>
      <div className="cartao-topo">
        <h3>{historia.titulo}</h3>
        <span className="prioridade">{ROTULO_PRIORIDADE[historia.prioridade]}</span>
      </div>
      <div className="cartao-corpo">
        <p className="autor">por {historia.autorNome}</p>
        <ul className="contagens">
          <li>{plural(historia.quantidadeCenarios, 'cenário', 'cenários')}</li>
          <li className={pendentes > 0 ? 'tem-pendencia' : undefined}>
            {pendentes === 0
              ? 'nenhuma sugestão pendente'
              : plural(pendentes, 'sugestão pendente', 'sugestões pendentes')}
          </li>
        </ul>
      </div>
    </article>
  )
}
