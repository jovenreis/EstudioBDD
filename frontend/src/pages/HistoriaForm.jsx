import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { PRIORIDADES } from '../rotulos.js'
import MensagemErro from '../components/MensagemErro.jsx'

const VAZIA = { titulo: '', papel: '', objetivo: '', beneficio: '', prioridade: 'MEDIA' }

// Usada para criar (/projetos/:projetoId/historias/nova) e editar (/historias/:id/editar).
export default function HistoriaForm() {
  const { projetoId, id } = useParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [dados, setDados] = useState(VAZIA)
  const [voltarPara, setVoltarPara] = useState(editando ? `/historias/${id}` : `/projetos/${projetoId}`)
  const [carregando, setCarregando] = useState(editando)
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    if (!editando) return
    api.historia(id)
      .then((h) => {
        setDados({
          titulo: h.titulo,
          papel: h.papel,
          objetivo: h.objetivo,
          beneficio: h.beneficio,
          prioridade: h.prioridade,
        })
        setVoltarPara(`/historias/${h.id}`)
      })
      .catch(setErro)
      .finally(() => setCarregando(false))
  }, [editando, id])

  function alterar(campo) {
    return (e) => setDados({ ...dados, [campo]: e.target.value })
  }

  async function salvar(e) {
    e.preventDefault()
    setSalvando(true)
    setErro(null)
    try {
      const salva = editando
        ? await api.atualizarHistoria(id, dados)
        : await api.criarHistoria(projetoId, dados)
      navigate(`/historias/${salva.id}`)
    } catch (err) {
      setErro(err)
      setSalvando(false)
    }
  }

  if (carregando) return <p className="aviso">Carregando…</p>

  return (
    <section className="formulario-pagina">
      <h1>{editando ? 'Editar história' : 'Nova história'}</h1>

      <form onSubmit={salvar} className="formulario">
        <label>
          Título
          <input value={dados.titulo} onChange={alterar('titulo')} />
        </label>

        <fieldset className="narrativa-campos">
          <legend>Narrativa</legend>
          <label>
            Como
            <input value={dados.papel} onChange={alterar('papel')} placeholder="cliente" />
          </label>
          <label>
            Quero
            <input value={dados.objetivo} onChange={alterar('objetivo')} placeholder="recuperar minha senha por e-mail" />
          </label>
          <label>
            Para
            <input value={dados.beneficio} onChange={alterar('beneficio')} placeholder="voltar a acessar a loja" />
          </label>
        </fieldset>

        <label>
          Prioridade
          <select value={dados.prioridade} onChange={alterar('prioridade')}>
            {PRIORIDADES.map((p) => (
              <option key={p.valor} value={p.valor}>{p.rotulo}</option>
            ))}
          </select>
        </label>

        <MensagemErro erro={erro} />

        <div className="acoes">
          <button type="submit" className="botao botao-principal" disabled={salvando}>
            {editando ? 'Salvar alterações' : 'Criar história'}
          </button>
          <Link to={voltarPara} className="botao botao-discreto">Cancelar</Link>
        </div>
      </form>
    </section>
  )
}
