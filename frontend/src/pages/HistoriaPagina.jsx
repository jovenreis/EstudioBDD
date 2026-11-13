import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { PRIORIDADES, plural, rotulo } from '../rotulos.js'
import BotaoExcluir from '../components/BotaoExcluir.jsx'
import CenarioCartao from '../components/CenarioCartao.jsx'
import CenarioEditor from '../components/CenarioEditor.jsx'
import ExportarFeature from '../components/ExportarFeature.jsx'
import MensagemErro from '../components/MensagemErro.jsx'
import PainelSugestoes from '../components/PainelSugestoes.jsx'

export default function HistoriaPagina() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [historia, setHistoria] = useState(null)
  const [erroCarga, setErroCarga] = useState(null)
  const [erroAcao, setErroAcao] = useState(null)
  const [criandoCenario, setCriandoCenario] = useState(false)

  const carregar = useCallback(() => {
    api.historia(id).then(setHistoria).catch(setErroCarga)
  }, [id])

  useEffect(carregar, [carregar])

  async function excluirHistoria() {
    setErroAcao(null)
    try {
      await api.excluirHistoria(id)
      navigate(`/projetos/${historia.projetoId}`)
    } catch (err) {
      setErroAcao(err)
    }
  }

  async function criarCenario(dados) {
    await api.criarCenario(id, dados)
    setCriandoCenario(false)
    carregar()
  }

  if (erroCarga) return <MensagemErro erro={erroCarga} />
  if (!historia) return <p className="aviso">Carregando…</p>

  return (
    <div className="historia-pagina">
      <section className="historia-principal">
        <Link to={`/projetos/${historia.projetoId}`} className="voltar">{historia.projetoNome}</Link>

        <header className={`historia-cabecalho prioridade-${historia.prioridade.toLowerCase()}`}>
          <div className="cartao-topo">
            <h1>{historia.titulo}</h1>
            <span className="prioridade">{rotulo(PRIORIDADES, historia.prioridade)}</span>
          </div>
          <p className="narrativa">
            <span className="palavra-chave">Como</span> {historia.papel},{' '}
            <span className="palavra-chave">quero</span> {historia.objetivo},{' '}
            <span className="palavra-chave">para</span> {historia.beneficio}.
          </p>
          <p className="aviso">Autor: {historia.autorNome}</p>

          <MensagemErro erro={erroAcao} />

          <div className="acoes">
            <Link to={`/historias/${id}/editar`} className="botao botao-discreto">Editar história</Link>
            <BotaoExcluir rotulo="Excluir história" onConfirmar={excluirHistoria} />
          </div>
        </header>

        <div className="secao-titulo">
          <h2>Cenários</h2>
          <span className="aviso">
            {plural(historia.cenarios.length, 'cenário', 'cenários')},{' '}
            {plural(historia.quantidadeSugestoesPendentes, 'sugestão pendente', 'sugestões pendentes')}
          </span>
        </div>

        {historia.cenarios.length === 0 && !criandoCenario && (
          <p className="aviso">Esta história ainda não tem cenários.</p>
        )}

        <div className="cenarios">
          {historia.cenarios.map((c) => (
            <CenarioCartao key={c.id} cenario={c} onMudou={carregar} />
          ))}
        </div>

        {criandoCenario ? (
          <div className="cenario">
            <CenarioEditor onSalvar={criarCenario} onCancelar={() => setCriandoCenario(false)} />
          </div>
        ) : (
          <button type="button" className="botao botao-principal" onClick={() => setCriandoCenario(true)}>
            Novo cenário
          </button>
        )}

        <h2 className="secao-titulo">Exportação</h2>
        <ExportarFeature historiaId={id} />
      </section>

      <PainelSugestoes historiaId={id} onMudou={carregar} />
    </div>
  )
}
