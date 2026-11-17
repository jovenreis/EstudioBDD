import { useState } from 'react'
import { useUsuario } from '../UsuarioContext.jsx'
import MensagemErro from '../components/MensagemErro.jsx'

// usuários de demonstração (dados fictícios da carga inicial, todos com a mesma senha)
const CONTAS_DEMO = [
  { nome: 'Ana Souza', email: 'ana.souza@exemplo.com' },
  { nome: 'Carlos Lima', email: 'carlos.lima@exemplo.com' },
  { nome: 'Beatriz Rocha', email: 'beatriz.rocha@exemplo.com' },
]
const SENHA_DEMO = 'senha123'

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

        <div className="contas-demo">
          <p>Ou selecione um usuário de demonstração:</p>
          <div className="contas-demo-botoes">
            {CONTAS_DEMO.map((c) => (
              <button
                key={c.email}
                type="button"
                className={`botao botao-discreto${email === c.email ? ' selecionado' : ''}`}
                onClick={() => {
                  setEmail(c.email)
                  setSenha(SENHA_DEMO)
                  setErro(null)
                }}
              >
                {c.nome}
              </button>
            ))}
          </div>
          <p>Ele preenche o e-mail e a senha (<code>{SENHA_DEMO}</code>); depois é só entrar.</p>
        </div>
      </form>
    </div>
  )
}
