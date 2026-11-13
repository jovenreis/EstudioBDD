import { useState } from 'react'

// Pede uma segunda confirmação antes de excluir, sem abrir janela do navegador.
export default function BotaoExcluir({ rotulo = 'Excluir', onConfirmar }) {
  const [confirmando, setConfirmando] = useState(false)

  if (!confirmando) {
    return (
      <button type="button" className="botao botao-discreto" onClick={() => setConfirmando(true)}>
        {rotulo}
      </button>
    )
  }

  return (
    <span className="confirmacao">
      <button type="button" className="botao botao-perigo" onClick={onConfirmar}>
        Confirmar exclusão
      </button>
      <button type="button" className="botao botao-discreto" onClick={() => setConfirmando(false)}>
        Cancelar
      </button>
    </span>
  )
}
