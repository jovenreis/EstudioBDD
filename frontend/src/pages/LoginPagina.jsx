import { useState } from 'react'
import { useUsuario } from '../UsuarioContext.jsx'
import MensagemErro from '../components/MensagemErro.jsx'

export default function LoginPagina() {
  const { entrar } = useUsuario()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [entrando, setEntrando] = useState(false)
  const [erro, setErro] = useState(null)

  async function enviar(e) {
    e.preventDefault()
    setEntrando(true)
    setErro(null)
    try {
      await entrar(email, senha)
    } catch (err) {
      setErro(err)
      setEntrando(false)
    }
  }

  return (
    <div className="login">
      <form className="login-ficha formulario" onSubmit={enviar}>
        <h1>Estúdio BDD</h1>
        <p className="aviso">Entre para escrever histórias e cenários.</p>

        <label>
          E-mail
          <input type="email" autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Senha
          <input type="password" autoComplete="current-password" value={senha} onChange={(e) => setSenha(e.target.value)} />
        </label>

        <MensagemErro erro={erro} />

        <button type="submit" className="botao botao-principal" disabled={entrando}>
          {entrando ? 'Entrando…' : 'Entrar'}
        </button>

        <details className="contas-demo">
          <summary>Contas de demonstração</summary>
          <p>Senha de todas: <code>senha123</code></p>
          <ul>
            <li><code>ana.souza@exemplo.com</code></li>
            <li><code>carlos.lima@exemplo.com</code></li>
            <li><code>beatriz.rocha@exemplo.com</code></li>
          </ul>
        </details>
      </form>
    </div>
  )
}
