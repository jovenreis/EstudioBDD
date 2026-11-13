import { useState } from 'react'
import { api } from '../api.js'
import MensagemErro from './MensagemErro.jsx'

// Pede o .feature para a API, mostra o texto e oferece o download.
// Se a história não puder ser exportada (regra 5), mostra a mensagem da API.
export default function ExportarFeature({ historiaId }) {
  const [arquivo, setArquivo] = useState(null)
  const [erro, setErro] = useState(null)

  async function exportar() {
    setErro(null)
    setArquivo(null)
    try {
      setArquivo(await api.exportarFeature(historiaId))
    } catch (err) {
      setErro(err)
    }
  }

  function baixar() {
    const blob = new Blob([arquivo.texto], { type: 'text/plain;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = arquivo.nome
    link.click()
    URL.revokeObjectURL(url)
  }

  return (
    <section className="exportacao">
      <button type="button" className="botao botao-principal" onClick={exportar}>
        Exportar .feature
      </button>

      <MensagemErro erro={erro} />

      {arquivo && (
        <div className="feature">
          <div className="feature-topo">
            <span>{arquivo.nome}</span>
            <button type="button" className="botao botao-discreto" onClick={baixar}>Baixar arquivo</button>
          </div>
          <pre>{arquivo.texto}</pre>
        </div>
      )}
    </section>
  )
}
