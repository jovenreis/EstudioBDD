import { useUsuario } from '../UsuarioContext.jsx'

export default function UsuarioLogado() {
  const { usuario, sair } = useUsuario()

  return (
    <div className="usuario-logado">
      <span>{usuario.nome}</span>
      <button type="button" className="botao botao-discreto" onClick={sair}>Sair</button>
    </div>
  )
}
