import { useUsuario } from '../UsuarioContext.jsx'

export default function SeletorUsuario() {
  const { usuarios, usuario, setUsuarioId, erro } = useUsuario()

  if (erro) return <span className="seletor erro-texto">{erro}</span>

  return (
    <label className="seletor">
      <span>Usando como</span>
      <select
        value={usuario?.id ?? ''}
        onChange={(e) => setUsuarioId(Number(e.target.value))}
        disabled={usuarios.length === 0}
      >
        {usuarios.map((u) => (
          <option key={u.id} value={u.id}>{u.nome}</option>
        ))}
      </select>
    </label>
  )
}
